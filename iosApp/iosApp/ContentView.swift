import UIKit
import SwiftUI
import ComposeApp
import rust_cryptoFFI
import CoreLocation

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        MainViewControllerKt.MainViewController(
            newKeypair: newKeypair,
            createCsr: createCsr,
            verifyAccountCertificate: verifyAccountCertificateSwift,
            accountSignature: accountSignature,
            viewModel: IosAppViewModel(),
        )
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

func verifyAccountCertificateSwift(keypair: Data, username: String, certificateBase64: String, serverPublicKey: String) -> KotlinBoolean {
    return KotlinBoolean(bool: verifyAccountCertificate(keypair: keypair, username: username, certificateBase64: certificateBase64, serverPublicKey: serverPublicKey))
}

struct ContentView: View {
    var body: some View {
        ComposeView()
            .ignoresSafeArea(.all) // Compose has own keyboard handler
    }
}

func filesDir() -> String {
    return FileManager.default.urls(for: .applicationSupportDirectory, in: .userDomainMask).first!.path
}
