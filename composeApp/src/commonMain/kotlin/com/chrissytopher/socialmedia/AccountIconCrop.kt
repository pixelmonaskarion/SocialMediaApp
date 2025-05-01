package com.chrissytopher.socialmedia

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Save
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.chrissytopher.socialmedia.navigation.NavigationStack
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlin.math.roundToInt
import kotlin.math.pow
//fun kVaultToValue(xPercent:Float,yPercent:Float,inputScale:Float,correctScale: Float){
//    val offsetX = ((xPercent-0.5f)*finalWidth)-(cropSize/2)
//    val offsetY = ((yPercent-0.5f)*finalHeight)-(cropSize/2)
 //   val cropSize = power((inputScale/correctScale),-1)/adjustCropSize

//
//
//}
@Composable
fun CropScreen(viewModel: AppViewModel, navHost: NavigationStack<NavScreen>)
{
    val platform = LocalPlatform.current
    val coroutineScope = rememberCoroutineScope()
    var screenWidth by remember{mutableStateOf(0)}
    var screenHeight by remember{mutableStateOf(0)}
    var finalWidth by remember { mutableStateOf(0) }
    var finalHeight by remember { mutableStateOf(0) }
    var correctScale by remember{ mutableStateOf(1f)}
    val outputSize = 400
    var cropSize by remember{mutableStateOf(viewModel.cropSize.value)}
    var offsetX by remember { mutableStateOf(viewModel.cropOffsetX.value) }
    var offsetY by remember { mutableStateOf(viewModel.cropOffsetY.value) }
    val density = LocalDensity.current.density
    val imageLink = viewModel.iconImageLink.value
    val painter = rememberAsyncImagePainter(viewModel.iconImageLink.value)

    val image: MutableState<ByteArray?> = remember { mutableStateOf(null) }
    Row(horizontalArrangement = Arrangement.Center,modifier = Modifier
        .fillMaxWidth())
    {
        Icon(Icons.Filled.Save,"Save current crop settings",
            modifier = Modifier
                .clickable(onClick = {
                    saveCropToKVault(offsetX=offsetX, finalWidth = finalWidth,offsetY=offsetY,finalHeight=finalHeight, cropSize = cropSize,correctScale=correctScale, adjustCropSize =outputSize,viewModel=viewModel, imageLink = imageLink, coroutineScope = coroutineScope)

                    navHost.popStack() })
                .requiredSize(50.dp))
    }
    Column (
        Modifier
        .fillMaxWidth()
        ,horizontalAlignment = Alignment.CenterHorizontally)
    {

        Box(contentAlignment = Alignment.Center,
            modifier = Modifier
                .layout{measurable,constraints->
                    val placeable = measurable.measure(constraints)
                    screenWidth = placeable.width
                    screenHeight = placeable.height
                    layout(width = placeable.width, height = placeable.height) {
                        placeable.place(0, 0)
                    }
                }
                .fillMaxWidth()
                .fillMaxHeight()
                //.border(1.dp,Color.Cyan, RectangleShape)
            )
        {
            Box(contentAlignment = Alignment.Center
            ){
                Image(
                    painter = painter,
                    contentDescription = "User profile picture",
                    modifier = Modifier
                        .clip(RectangleShape)
                        .layout{measurable,constraints->
                            val placeable = measurable.measure(constraints)
                            val hScale = screenHeight/(placeable.height.toFloat())
                            val wScale = screenWidth/(placeable.width.toFloat())
                            correctScale = if(wScale >hScale){
                                hScale
                            }else{
                                wScale
                            }
                            finalHeight = (placeable.height*correctScale).roundToInt()
                            finalWidth = (placeable.width*correctScale).roundToInt()


                            layout(width = finalWidth, height = finalHeight) {
                                placeable.place((finalWidth-placeable.width)/2, (finalHeight-placeable.height)/2)
                            }
                        }
                        .scale(correctScale)
                )
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .offset { IntOffset(offsetX.roundToInt(),  offsetY.roundToInt()) }
                        .background(Color.Blue,CircleShape)
                        .size(20.dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                //offsetX += dragAmount.x
                                if(offsetX + dragAmount.x> ((finalWidth)/2)-cropSize){
                                    offsetX = (finalWidth/2).toFloat()-cropSize
                                }else if (offsetX + dragAmount.x < -((finalWidth)/2) ){
                                    offsetX = -(finalWidth/2).toFloat()
                                }else{
                                    offsetX += dragAmount.x
                                }
                                //offsetY += dragAmount.y
                                if(offsetY + dragAmount.y > ((finalHeight)/2)-cropSize){
                                    offsetY = (finalHeight/2).toFloat()-cropSize
                                }else if (offsetY + dragAmount.y < -((finalHeight)/2) ){
                                    offsetY = -(finalHeight/2).toFloat()
                                }else{
                                    offsetY += dragAmount.y
                                }
                            }
                        }
                ){
                    Box(modifier = Modifier
                        .offset { IntOffset(cropSize.roundToInt(), cropSize.roundToInt()) }
                        .background(Color.Green, CircleShape)
                        .size(20.dp)
                        .pointerInput(Unit) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                cropSize += if(dragAmount.x > dragAmount.y){
                                    dragAmount.x
                                }else{
                                    dragAmount.y
                                }
                                if (cropSize<8f){
                                    cropSize = 8f
                                }else if(cropSize>finalWidth){
                                    cropSize = finalWidth.toFloat()
                                }else if (cropSize> finalHeight){
                                    cropSize = finalHeight.toFloat()
                                }
                            }
                        }

                    )
                    Box(contentAlignment = Alignment.Center,modifier = Modifier
                        .requiredSize((((cropSize/density)).roundToInt()).dp)
                        .offset { IntOffset((cropSize/2).roundToInt(),  (cropSize/2).roundToInt()) }
                        .border(1.dp,Color.Black,RectangleShape)



                    ){

                    }
                }
            }
                viewModel.changeCropSetting(offsetX,offsetY,cropSize)
//            val xPercent = ((offsetX+(cropSize/2))/finalWidth) + 0.5f
//            val yPercent = ((offsetY+(cropSize/2))/finalHeight) + 0.5f
//            val adjustCropSize = 200
//            val inputScale = (adjustCropSize/cropSize) * correctScale
//            Image(painter = painter,
//                contentDescription = "User profile picture", modifier = croppingScream(xPercent,yPercent, inputScale,adjustCropSize)
//           )

        }
    }
}
fun saveCropToKVault(offsetX:Float,finalWidth:Int, offsetY:Float,finalHeight:Int, cropSize:Float,correctScale:Float,adjustCropSize:Int,viewModel: AppViewModel,imageLink:String?,coroutineScope: CoroutineScope){
    val xPercent = ((offsetX+(cropSize/2))/finalWidth) + 0.5f
    val yPercent = ((offsetY+(cropSize/2))/finalHeight) + 0.5f
    val inputScale = (adjustCropSize/cropSize) * correctScale
    viewModel.changeIconImage(xPercent,yPercent,inputScale,adjustCropSize,imageLink)
    val mime = "image/?"
    val username = viewModel.authenticationManager.username
    coroutineScope.launch {
        val iconInfo = JsonObject(hashMapOf(
                "username" to JsonPrimitive(username),
                "x_percent" to JsonPrimitive(xPercent),
                "y_percent" to JsonPrimitive(yPercent),
                "icon_scale" to JsonPrimitive(inputScale),
                "icon_size" to JsonPrimitive(adjustCropSize),
                "mime" to JsonPrimitive(mime)
            )
        )
        println("sending info for $username")
        val res = viewModel.apiClient.uploadIconInfo(iconInfo)
        println("did it work????")
        res.onSuccess {  println("updating $username") }
    }
}

