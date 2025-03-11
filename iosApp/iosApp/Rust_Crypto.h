#ifndef RustCrypto_Bridging_Header_h
#define RustCrypto_Bridging_Header_h

const char* new_keypair();
const char* create_csr(const char* keypair);
BOOL verify_account_certificate(const char* keypair, const char*  username, const char*  certificateBase64, const char* serverPublicKey);
const char* account_signature(const char* keypair, const char*  username, const char*  nonce);

#endif /* RustCrypto_Bridging_Header_h */
