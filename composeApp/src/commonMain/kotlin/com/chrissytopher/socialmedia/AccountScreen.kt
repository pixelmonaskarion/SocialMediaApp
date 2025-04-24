package com.chrissytopher.socialmedia

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredSize
import androidx.compose.foundation.layout.requiredSizeIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.layout.ContentScale
import coil3.Uri
import coil3.compose.AsyncImage
import coil3.compose.rememberAsyncImagePainter
import coil3.request.ImageRequest
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.sourceInformationMarkerEnd
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.layout
import androidx.compose.ui.node.ModifierNodeElement
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import coil3.Bitmap
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.compose.AsyncImagePainter
import com.chrissytopher.socialmedia.navigation.NavigationStack
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlin.math.abs





@Composable
fun AccountSettingScreen(viewModel: AppViewModel,navHost:NavigationStack<NavScreen>) {
    val platform = LocalPlatform.current
    val coroutineScope = rememberCoroutineScope()
    var username: String? by remember { mutableStateOf(null) }
    var email: String? by remember { mutableStateOf(null) }
    if (viewModel.authenticationManager.loggedIn()) {
        username = viewModel.authenticationManager.username
        email = viewModel.authenticationManager.email
    }
    var profilePicture: String? by remember { mutableStateOf(viewModel.iconImageLink.value) }
    if (profilePicture == null) {
        profilePicture = "https://images.pexels.com/photos/104827/cat-pet-animal-domestic-104827.jpeg"
    }
    var yPosition by remember { mutableStateOf(viewModel.iconYPer.value) }
    var xPosition by remember { mutableStateOf(viewModel.iconXPer.value) }
    var inputSize by remember { mutableStateOf(viewModel.iconScale.value) }
    if (100f <inputSize || inputSize < 0.25f){
    inputSize = 1f
    }
    var outputSize by remember { mutableStateOf(viewModel.iconOutputSize.value) }
    if (outputSize < 50) {
        outputSize = 50
    }
    val painter = rememberAsyncImagePainter(model = profilePicture)
    viewModel.changeIconImage(xPosition,yPosition,inputSize,outputSize,profilePicture)
    // I would put the image fetch request here
    Column(
        Modifier
            .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Spacer(Modifier.size(10.dp))
        Image(
            painter = painter,
            contentDescription = "User profile picture",
            modifier = croppingScream(xPosition, yPosition, inputSize, outputSize)
                .clickable(onClick = { navHost.navigateTo(NavScreen.CropScreen) })
        )

//        Slider(
//            modifier = Modifier.semantics { contentDescription = "Y translate" },
//            value = yPosition,
//            onValueChange = { yPosition = it },
//            valueRange = 0f..1f,
//        )
//        Text("Y Position")
//        Slider(
//            modifier = Modifier.semantics { contentDescription = "X translate" },
//            value = xPosition,
//            onValueChange = { xPosition = it },
//            valueRange = 0f..1f,
//        )
//        Text("X Position")
//        Slider(
//            modifier = Modifier.semantics { contentDescription = "Input Size" },
//            value = inputSize,
//            onValueChange = { inputSize = it },
//            valueRange = 0f..4f
//        )
//        Text("Input Size")
//        Slider(
//            modifier = Modifier.semantics { contentDescription = "Output Size" },
//            value = outputSize.toFloat(),
//            onValueChange = { outputSize = it.toInt() },
//            valueRange = 100f..500f,
//        )
//        Text("Output Size")



        username?.let { Text(it, style = MaterialTheme.typography.titleLarge) }
        email?.let { Text(it, style = MaterialTheme.typography.titleLarge) }

    }
}

fun croppingScream(x:Float,y:Float,scalingFactor: Float,outputSize: Int):Modifier
{
   //I'm going to have to draw this to figure out the math - scream
    var originalW:Int by mutableStateOf(0) //annotated as OGW on graph - represents the width of original image
    var originalH:Int by mutableStateOf(0) //annotated as OGH on graph - represents the height of the original image
    var scaledW:Int by mutableStateOf(0 )//annotated as SW on graph - represents the width of the scaled image
    var scaledH:Int by mutableStateOf(0) //annotated as SH on graph - represents the height of the scaled image
    var sX:Int by mutableStateOf(0) //coord of upper left corner of scaled image
    var sY:Int by mutableStateOf(0)//coord of upper left corner of scaled image
    var cS = outputSize //crop size
    var cX:Int by mutableStateOf(0) //center of cropped output image
    var cY:Int by mutableStateOf(0)//center of cropped output image
    var originCX:Int by mutableStateOf(0) //upper left hand corner of cropped image
    var originCY:Int by mutableStateOf(0) //upper left hand corner of cropped image

    return Modifier
        .clip(CircleShape)
        //.clip(RectangleShape)
        //.border(1.dp,Color.Blue,RectangleShape)
        .layout{measurable,constraints->
            val placeable = measurable.measure(constraints)
            originalW = placeable.width
            originalH = placeable.height
            scaledW = (originalW*scalingFactor).toInt()
            scaledH = (originalH*scalingFactor).toInt()
            if (scalingFactor > 1){ //setting origin of the scaled image at upper left hand corner. This changes compared to the original image since scale is only set around the center of the image
                //scaled image is outside of bounds, so needs negative coord to move layout outside of original bound
                sX = 0-((scaledW-originalW)/2)
                sY = 0-((scaledH-originalH)/2)
            }else{
                //scaled image is inside bounds, so needs positive coord to move layout further inside of original bound
                sX = 0+((originalW-scaledW)/2)
                sY = 0+((originalH-scaledH)/2)
            }
//            layout(width =scaledW, height = scaledH) {
//                placeable.place(-sX,-sY)
//            }
            cX = (scaledW*x).toInt() +sX // finds center of cropped image on scaled image
            cY = (scaledH*y).toInt() + sY //finds center of cropped image on scaled image
            originCX = if((cX-(cS/2))<sX){ //if cropped image is too far left, set left hand corner to the scaled image x coord
                sX
            }else if((cX+(cS/2))>(sX+scaledW)){ //if cropped image is too far right, set left hand corner to be one size away from right hand bound
                sX+scaledW-cS
            }
            else{ //if we're chilling, we're chilling
                cX-(cS/2)
            }
            originCY = if((cY-(cS/2)) < sY){
                sY
            }else if((cY+(cS/2))>(sY+scaledH)){
                sY+scaledH-cS
            }
            else{
                cY-(cS/2)
            }
            if(cS <scaledH && cS < scaledW){
                layout(width =cS, height = cS) {
                    placeable.place(-originCX,-originCY)
                }
            }
            else{
                if(scaledH > scaledW){
                    cS=scaledW
                    originCY = if((cY-(cS/2)) < sY){
                        sY
                    }else if((cY+(cS/2))>(sY+scaledH)){
                        sY+scaledH-cS
                    }
                    else{
                        cY-(cS/2)
                    }
                    layout(width = cS, height = cS) {
                        placeable.place(-sX, -originCY)
                    }
                }
                else{
                    cS = scaledH
                    originCX = if((cX-(cS/2))<sX){ //if cropped image is too far left, set left hand corner to the scaled image x coord
                        sX
                    }else if((cX+(cS/2))>(sX+scaledW)){ //if cropped image is too far right, set left hand corner to be one size away from right hand bound
                        sX+scaledW-cS
                    }
                    else{ //if we're chilling, we're chilling
                        cX-(cS/2)
                    }
                    layout(width = cS, height = cS) {
                        placeable.place(-originCX, -sY)
                    }
                }
            }
        }
        //.border(1.dp,Color.Red,RectangleShape)
        .scale(scalingFactor)

}










