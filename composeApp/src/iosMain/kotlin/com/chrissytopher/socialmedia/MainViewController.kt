package com.chrissytopher.socialmedia

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.interop.LocalUIViewController
import androidx.compose.ui.window.ComposeUIViewController
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.refTo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.io.Buffer
import kotlinx.io.Source
import kotlin.uuid.ExperimentalUuidApi
import platform.darwin.NSObject
import platform.PhotosUI.PHPickerViewControllerDelegateProtocol
import platform.PhotosUI.PHPickerConfiguration
import platform.PhotosUI.PHPickerResult
import platform.PhotosUI.PHPickerViewController
import platform.UIKit.UIImage
import platform.UIKit.UIImageJPEGRepresentation
import platform.posix.memcpy

var imagePickerCallback: (suspend (List<Source>) -> Unit)? = null

fun MainViewController(newKeypair: NewKeypairLambda, createCsr: CreateCsrLambda, verifyAccountCertificate: VerifyAccountCertificateLambda, accountSignature: AccountSignature, viewModel: IosAppViewModel) = ComposeUIViewController {
    new_keypair = newKeypair
    create_csr = createCsr
    verify_account_certificate = verifyAccountCertificate
    account_signature = accountSignature
    val launchPhotoPicker = rememberOpenPickerAction()
    val platform = IOSPlatform(launchPhotoPicker)
    CompositionLocalProvider(LocalPlatform provides platform) {
        App(viewModel)
    }
}

@OptIn(ExperimentalUuidApi::class)
@Composable
fun rememberOpenPickerAction(): () -> Unit {
    val uiViewController = LocalUIViewController.current
    val pickerDelegate = remember {
        object : NSObject(), PHPickerViewControllerDelegateProtocol {
            override fun picker(picker: PHPickerViewController, didFinishPicking: List<*>) {
                println("didFinishPicking: $didFinishPicking")
                picker.dismissViewControllerAnimated(flag = false, completion = {})
                val itemProvider = (didFinishPicking.firstOrNull() as? PHPickerResult)?.itemProvider
                if (itemProvider == null) {
                    CoroutineScope(Dispatchers.Main).launch { imagePickerCallback?.invoke(listOf()) }
                    return
                }
                itemProvider.loadDataRepresentationForTypeIdentifier(
                    typeIdentifier = "public.image",
                ) { nsData, _ ->
                    CoroutineScope(Dispatchers.Main).launch {
                        nsData?.let {
                            val image = UIImage.imageWithData(it)
                            val bytes = image?.toByteArray(compressionQuality = 1.0)
                            if (bytes == null) {
                                imagePickerCallback?.invoke(emptyList())
                                return@launch
                            }
                            imagePickerCallback?.invoke(listOf(Buffer().apply { write(bytes) }))
                        }
                    }
                }
            }
        }
    }

    return remember {
        {
            val configuration = PHPickerConfiguration()
            val pickerController = PHPickerViewController(configuration)
            pickerController.setDelegate(pickerDelegate)
            uiViewController.presentViewController(pickerController, animated = true, completion = null)
        }
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun UIImage.toByteArray(compressionQuality: Double): ByteArray {
    val validCompressionQuality = compressionQuality.coerceIn(0.0, 1.0)
    val jpegData = UIImageJPEGRepresentation(this, validCompressionQuality)!!
    return ByteArray(jpegData.length.toInt()).apply {
        memcpy(this.refTo(0), jpegData.bytes, jpegData.length)
    }
}