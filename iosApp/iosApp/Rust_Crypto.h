#ifndef RustCrypto_Bridging_Header_h
#define RustCrypto_Bridging_Header_h

#import <Foundation/Foundation.h>

NSData *rust_new_keypair(void);
NSData *rust_create_csr(NSData *keypair);
BOOL rust_verify_account_certificate(NSData *keypair, NSString *username, NSString *certificateBase64, NSString *serverPublicKey);
NSString *rust_account_signature(NSData *keypair, NSString *username, NSString *nonce);

#endif /* RustCrypto_Bridging_Header_h */
