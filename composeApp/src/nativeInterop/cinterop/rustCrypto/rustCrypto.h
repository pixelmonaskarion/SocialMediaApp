const char* new_keypair();
const char* create_csr(const char* keypair);
#include <stdbool.h>
bool verify_account_certificate(const char* keypair, const char*  username, const char*  certificateBase64, const char* serverPublicKey);
const char* account_signature(const char* keypair, const char*  username, const char*  nonce);