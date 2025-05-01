cargo build
cargo run --bin uniffi-bindgen generate --library target/debug/librust_crypto.dylib --language swift --out-dir ./bindings

# definitely should do this
# mv bindings/rust_cryptoFFI.modulemap bindings/module.modulemap
# but this works
rm bindings/rust_cryptoFFI.modulemap
mv bindings/rust_crypto.swift ../iosApp/iosApp/rust_crypto.swift

rm -r target/rust_cryptoFFI.xcframework

xcframework -r

mv target/rust_cryptoFFI.xcframework rust_cryptoFFI.xcframework

# rm -r bindings
