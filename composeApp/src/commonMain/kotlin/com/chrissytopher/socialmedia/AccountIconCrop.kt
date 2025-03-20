package com.chrissytopher.socialmedia

import androidx.compose.animation.core.animateOffsetAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.draggable
import androidx.compose.foundation.gestures.rememberDraggableState
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Crop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil3.compose.rememberAsyncImagePainter
import com.chrissytopher.socialmedia.navigation.NavigationStack
import kotlin.math.roundToInt

@Composable
fun CropScreen(viewModel: AppViewModel, navHost: NavigationStack<NavScreen>)
{
    val platform = LocalPlatform.current
    val coroutineScope = rememberCoroutineScope()
    var offsetX by remember { mutableStateOf(0f) }
    var offsetY by remember { mutableStateOf(0f) }
    var cropSize by remember{mutableStateOf(0f)}

    Row(horizontalArrangement = Arrangement.End,modifier = Modifier
        .fillMaxWidth())
    {
        Icon(Icons.Filled.Close,"hi",
            modifier = Modifier
                .clickable(onClick = {navHost.popStack() })
                .requiredSize(50.dp))
    }
    Column (
        Modifier
        .fillMaxWidth()
        ,horizontalAlignment = Alignment.CenterHorizontally){
        var screenWidth by remember{mutableStateOf(0)}
        var screenHeight by remember{mutableStateOf(0)}
        var scale by remember { mutableStateOf(1f) }
        var rotation by remember { mutableStateOf(0f) }
        var offset by remember { mutableStateOf(Offset.Zero) }
        var finalWidth by remember { mutableStateOf(0) }
        var finalHeight by remember { mutableStateOf(0) }
        val state = rememberTransformableState { zoomChange, offsetChange, rotationChange ->
            scale *= zoomChange
            rotation += rotationChange
            offset += offsetChange}
        val density = LocalDensity.current.density
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
                .border(1.dp,Color.Cyan, RectangleShape)
            )
        {
            Box(contentAlignment = Alignment.Center
            ){
                val profilePicture = "https://upload.wikimedia.org/wikipedia/commons/a/a9/Grace_Abbott_1929.jpg"
                //val profilePicture = "https://images.pexels.com/photos/104827/cat-pet-animal-domestic-104827.jpeg"
                val painter = rememberAsyncImagePainter(model = profilePicture)
                var correctScale by remember{ mutableStateOf(0f)}
//                var finalWidth by remember { mutableStateOf(0) }
//                var finalHeight by remember { mutableStateOf(0) }
                Image(
                    painter = painter,
                    contentDescription = "User profile picture",
                    modifier = Modifier
                        //.border(1.dp,Color.Blue, RectangleShape)
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
                        //.border(1.dp,Color.Blue, RectangleShape)
                        .scale(correctScale)



                )
                Box(contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            translationX = offset.x
                            translationY = offset.y
                        }
                        .background(Color.Transparent)
                        .transformable(state = state)
                        .size(30.dp)
                )
                {
                    Box(modifier = Modifier
                        .border(1.dp,Color.Magenta, RectangleShape)
                        .fillMaxWidth()
                        .fillMaxHeight()
                    )
                }
//                Box(
//                    contentAlignment = Alignment.Center,
//                    modifier = Modifier
//                        .offset { IntOffset(offsetX.roundToInt(),  offsetY.roundToInt()) }
//                        .background(Color.Blue)
//                        .size(20.dp)
//                        .pointerInput(Unit) {
//                            detectDragGestures { change, dragAmount ->
//                                change.consume()
//                                //offsetX += dragAmount.x
//                                if(offsetX + dragAmount.x> ((finalWidth)/2)-cropSize){
//                                    offsetX = (finalWidth/2).toFloat()-cropSize
//                                }else if (offsetX + dragAmount.x < -((finalWidth)/2) ){
//                                    offsetX = -(finalWidth/2).toFloat()+10*density
//                                }else{
//                                    offsetX += dragAmount.x
//                                }
//                                //offsetY += dragAmount.y
//                                if(offsetY + dragAmount.y > ((finalHeight)/2)-cropSize){
//                                    offsetY = (finalHeight/2).toFloat()-cropSize
//                                }else if (offsetY + dragAmount.y < -((finalHeight)/2) ){
//                                    offsetY = -(finalHeight/2).toFloat()+10*density
//                                }else{
//                                    offsetY += dragAmount.y
//                                }
//                            }
//                        }
//                ){
//                    Box(modifier = Modifier
//                        .offset { IntOffset(cropSize.roundToInt(), cropSize.roundToInt()) }
//                        .background(Color.Green)
//                        .size(20.dp)
//                        .pointerInput(Unit) {
//                            detectDragGestures { change, dragAmount ->
//                                change.consume()
//                                cropSize += if(dragAmount.x > dragAmount.y){
//                                    dragAmount.x
//                                }else{
//                                    dragAmount.y
//                                }
//                                if (cropSize<1f){
//                                    cropSize = 2f
//                                }else if(cropSize>screenWidth){
//                                    cropSize = screenWidth.toFloat()
//                                }else if (cropSize> screenHeight){
//                                    cropSize = screenHeight.toFloat()
//                                }
//                            }
//                        }
//
//                    )
//                    Box(contentAlignment = Alignment.Center,modifier = Modifier
//                        .requiredSize((((cropSize/density)).roundToInt()+10).dp)
//                        .offset { IntOffset((cropSize/2).roundToInt(),  (cropSize/2).roundToInt()) }
//                        .border(1.dp,Color.Black,RectangleShape)
//
//
//
//                    ){
//
//                    }
//                }
            }

        }
    }
}
