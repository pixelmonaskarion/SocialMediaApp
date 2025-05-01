use jni::{objects::{JByteArray, JClass, JString}, sys::{jboolean, jstring}, JNIEnv};
use log::{error, warn};
use openssl::{base64, error::ErrorStack, hash::MessageDigest, nid::Nid, pkey::{PKey, Private, Public}, rsa::Rsa, sign::Signer, x509::{X509Req, X509}};

uniffi::setup_scaffolding!();

#[no_mangle]
pub extern "system" fn Java_com_chrissytopher_socialmedia_RustCrypto_newKeypair<'local>(env: JNIEnv<'local>, _class: JClass<'local>)
    -> JByteArray<'local> {
    let keypair = Rsa::generate(2048).unwrap();
    let keypair_bytes = serialize_key(keypair);

    let output = env.byte_array_from_slice(&keypair_bytes)
        .expect("Couldn't create java string!");

    output
}

#[uniffi::export]
pub fn new_keypair() -> Vec<u8> {
    let keypair = Rsa::generate(2048).unwrap();
    serialize_key(keypair)
}

fn serialize_key(key: Rsa<Private>) -> Vec<u8> {
    let mut public = key.public_key_to_der().unwrap();
    let mut private = key.private_key_to_der().unwrap();
    let mut output = vec![];
    output.append(&mut (public.len() as i32).to_be_bytes().to_vec());
    output.append(&mut public);
    output.append(&mut private);
    output
}

fn deserialize_private_key(bytes: &[u8]) -> Result<Rsa<Private>, ErrorStack> {
    let public_len = i32::from_be_bytes(bytes[0..4].try_into().unwrap()) as usize;
    let private = bytes[4+public_len..].to_vec();
    let key = Rsa::private_key_from_der(&private)?;
    Ok(key)
}

fn deserialize_public_key(bytes: &[u8]) -> Result<Rsa<Public>, ErrorStack> {
    let public_len = i32::from_be_bytes(bytes[0..4].try_into().unwrap()) as usize;
    let public = bytes[4..4+public_len].to_vec();
    let key = Rsa::public_key_from_der(&public)?;
    Ok(key)
}

#[no_mangle]
pub extern "system" fn Java_com_chrissytopher_socialmedia_RustCrypto_createCsr<'local>(env: JNIEnv<'local>, _class: JClass<'local>, keypair: JByteArray<'local>)
    -> JByteArray<'local> {
    android_logger::init_once(android_logger::Config::default().with_max_level(log::LevelFilter::Trace));
    let csr_bytes = create_csr_inner(env.convert_byte_array(keypair).unwrap());
    if let Err(e) = &csr_bytes {
        error!("error: {e}");
    }

    let output = env.byte_array_from_slice(&csr_bytes.unwrap())
        .expect("Couldn't create java string!");

    output
}

#[uniffi::export]
fn create_csr(keypair: Vec<u8>) -> Vec<u8> {
    create_csr_inner(keypair).unwrap()
}

pub fn create_csr_inner(keypair: Vec<u8>) -> Result<Vec<u8>, ErrorStack> {
    let public_key = PKey::from_rsa(deserialize_public_key(&keypair)?)?;
    let private_key = PKey::from_rsa(deserialize_private_key(&keypair)?)?;
    let mut csr_build = X509Req::builder()?;
    csr_build.set_pubkey(&public_key)?;
    csr_build.sign(&private_key, MessageDigest::sha256())?;
    let csr = csr_build.build();
    let csr_bytes = csr.to_der()?;
    Ok(csr_bytes)
}

#[no_mangle]
pub extern "system" fn Java_com_chrissytopher_socialmedia_RustCrypto_verifyAccountCertificate<'local>(mut env: JNIEnv<'local>, _class: JClass<'local>,
    keypair: JByteArray<'local>, username: JString<'local>, certificate_base64: JString<'local>, server_public_key: JString<'local>
) -> jboolean {
    android_logger::init_once(android_logger::Config::default().with_max_level(log::LevelFilter::Trace));
    let cert_base64: String = env.get_string(&certificate_base64).unwrap().into();
    let cert_bytes = base64::decode_block(&cert_base64).unwrap();
    let valid = verify_account_certificate_inner(env.convert_byte_array(keypair).unwrap(), env.get_string(&username).unwrap().into(), cert_bytes, env.get_string(&server_public_key).unwrap().into());
    if let Err(e) = &valid {
        warn!("error: {e}");
    }

    let output = valid.unwrap() as jboolean;

    output
}

#[uniffi::export]
fn verify_account_certificate(keypair: Vec<u8>, username: String, certificate_base64: String, server_public_key: String) -> bool {
    let cert_bytes = base64::decode_block(&certificate_base64).unwrap();
    verify_account_certificate_inner(keypair, username, cert_bytes, server_public_key).unwrap()
}

pub fn verify_account_certificate_inner(keypair: Vec<u8>, username: String, certificate_bytes: Vec<u8>, server_public_key: String) -> Result<bool, ErrorStack> {
    let cert = X509::from_der(&certificate_bytes)?;

    let keypair = deserialize_public_key(&keypair)?;

    let pub_key = PKey::from_rsa(Rsa::public_key_from_der(&base64::decode_block(&server_public_key)?)?)?;
    
    let mut valid = cert.verify(&pub_key)?;
    valid &= cert.public_key()?.public_eq(PKey::from_rsa(keypair)?.as_ref());
    if let None = cert.subject_name().entries_by_nid(Nid::ACCOUNT).next() {
        return Ok(false);
    }
    valid &= cert.subject_name().entries_by_nid(Nid::ACCOUNT).next().unwrap().data().as_utf8()?.to_string() == username;
    Ok(valid)
}

#[no_mangle]
pub extern "system" fn Java_com_chrissytopher_socialmedia_RustCrypto_accountSignature<'local>(mut env: JNIEnv<'local>, _class: JClass<'local>,
    keypair: JByteArray<'local>, username: JString<'local>, nonce: JString<'local>
) -> jstring {
    android_logger::init_once(android_logger::Config::default().with_max_level(log::LevelFilter::Trace));
    let keypair = PKey::from_rsa(deserialize_private_key(&env.convert_byte_array(keypair).unwrap()).unwrap()).unwrap();
    let username: String = env.get_string(&username).unwrap().into();
    let nonce: String = env.get_string(&nonce).unwrap().into();
    let mut payload = vec![];
    payload.append(&mut username.as_bytes().to_vec());
    payload.append(&mut nonce.as_bytes().to_vec());
    let mut signer = Signer::new(MessageDigest::sha256(), &keypair).unwrap();
    let signature = signer.sign_oneshot_to_vec(&payload).unwrap();
    let output = env.new_string(base64::encode_block(&signature)).unwrap();
    **output
}

#[uniffi::export]
fn account_signature(keypair: Vec<u8>, username: String, nonce: String) -> String {
    let keypair = PKey::from_rsa(deserialize_private_key(&keypair).unwrap()).unwrap();
    let mut payload = vec![];
    payload.append(&mut username.as_bytes().to_vec());
    payload.append(&mut nonce.as_bytes().to_vec());
    let mut signer = Signer::new(MessageDigest::sha256(), &keypair).unwrap();
    let signature = signer.sign_oneshot_to_vec(&payload).unwrap();
    base64::encode_block(&signature)
}

#[cfg(test)]
mod test {
    use openssl::{hash::MessageDigest, pkey::PKey, rsa::Rsa, x509::X509Req};

    #[test]
    fn test_csr() {
        let key = Rsa::generate(2048).unwrap();
        let key = PKey::from_rsa(key).unwrap();
        let mut csr_build = X509Req::builder().unwrap();
        csr_build.set_pubkey(&key).unwrap();
        csr_build.sign(&key, MessageDigest::sha256()).unwrap();
        let csr = csr_build.build();
        let csr_bytes = csr.to_der().unwrap();
        assert!(csr_bytes.len() > 0);
    }
}