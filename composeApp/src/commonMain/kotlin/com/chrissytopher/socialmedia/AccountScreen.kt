package com.chrissytopher.socialmedia

import androidx.compose.foundation.Image
import androidx.compose.foundation.border

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

//import android.graphics.Bitmap
//import android.graphics.Rect
//import android.graphics.BitmapFactory
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.runtime.collectAsState
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
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.launch
import kotlin.math.abs



@Composable
fun AccountSettingScreen() {
    val platform = LocalPlatform.current
    val authManager = LocalAuthenticationManager.current
    val coroutineScope = rememberCoroutineScope()
    var username: String? by remember { mutableStateOf(null)}
    var email: String? by remember {mutableStateOf(null)}
    var profilePicture: String? by remember { mutableStateOf(null) }
    if (authManager.loggedIn()){
        username = authManager.username
        email = authManager.email
    }
    // I would put the image fetch request here
    profilePicture = "https://upload.wikimedia.org/wikipedia/commons/a/a9/Grace_Abbott_1929.jpg"
    Column(Modifier
        .fillMaxWidth()
        ,horizontalAlignment = Alignment.CenterHorizontally) {

        Spacer(Modifier.size(10.dp))
        var yPosition by remember { mutableStateOf(0f) }
        var xPosition by remember {mutableStateOf(0f)}
        var inputSize by remember{mutableStateOf(0f)}
        var outputSize by remember{mutableStateOf(0f)}
        val painter = rememberAsyncImagePainter(model = profilePicture)
        //val painterState = painter.state//.collectAsState()
        Image(
            painter = painter,
            contentDescription = "User profile picture",
            modifier = Modifier
                .clip(RectangleShape)
//                .border(1.dp,Color.Red,RectangleShape)
             .customCrop(xPosition,yPosition,outputSize.toInt())
               // .border(1.dp,Color.Blue,RectangleShape)
               .clip(RectangleShape)
                .customScale(scalingFactor = inputSize)
                .scale(inputSize)

        )
        Slider(
            modifier = Modifier.semantics { contentDescription = "Y translate" },
            value = yPosition,
            onValueChange = { yPosition = it },
            valueRange = 0f..1f, )
        Text("Y Position")
        Slider(
            modifier = Modifier.semantics { contentDescription = "X translate" },
            value = xPosition,
            onValueChange = { xPosition = it },
            valueRange = 0f..1f, )
        Text("X Position")
        Slider(
            modifier = Modifier.semantics { contentDescription = "Input Size" },
            value = inputSize,
            onValueChange = { inputSize = it },
            valueRange = 0f..4f )
        Text("Input Size")
        Slider(
            modifier = Modifier.semantics { contentDescription = "Output Size" },
            value = outputSize,
            onValueChange = { outputSize = it },
            valueRange = 100f..500f, )
        Text("Output Size")



        username?.let { Text(it, style = MaterialTheme.typography.titleLarge) }
        email?.let { Text(it, style = MaterialTheme.typography.titleLarge)}

}

}
fun Modifier.customCrop(x:Float , y:Float, outputSize:Int) =
    layout{measurable,constraints ->
        val placeable = measurable.measure(constraints)
        //(x,y) should be the center point of the image
        // that means x value has to be greater than outputSize/2 and less than givenWidth - outputSize/2
        //same for y (just swap givenHeight for givenWidth)
        //place changes the location of top left corner, and moves that to be (x-outputsize/2, y-outputsize/2)
        val givenWidth = placeable.width
        val givenHeight = placeable.height
        val smallestSide =
            if(outputSize <givenHeight && outputSize < givenWidth){
                outputSize}
            else{
                if(givenHeight > givenWidth){
                    givenWidth
                }
                else{
                    givenHeight
                }
            }
        val castX = (givenWidth*x).toInt()
        val castY = (givenHeight*y).toInt()
        val originX = if(castX < outputSize/2){
            outputSize/2
        }else if (castX > givenWidth-outputSize/2) {
            givenWidth-outputSize/2
        }else{
            castX
        }
        val originY = if(castY < outputSize/2){
            outputSize/2
        }else if (castY> givenHeight-outputSize/2) {
            givenHeight-outputSize/2
        }else{
            castY
        }
        if(outputSize!=smallestSide){
            layout(width = smallestSide, height = smallestSide) {
                placeable.place(0,0)
            }
        }else{
        layout(width = smallestSide, height = smallestSide) {
            placeable.place(-(originX-outputSize/2), -(originY-outputSize/2))
        }}

    }
fun Modifier.customScale(scalingFactor:Float)=
    layout{measurable,constraints->
        val placeable = measurable.measure(constraints)
        val gwidth = placeable.width*scalingFactor
        val gheight = placeable.height*scalingFactor
        var originX:Float
        var originY:Float
        if(scalingFactor>1){
        originX = (gwidth - placeable.width)/2
        originY = (gheight - placeable.height)/2
        }else{
            originX = -(placeable.width-gwidth)/2
            originY = -(placeable.height-gheight)/2
        }
        layout(width =gwidth.toInt(), height = gheight.toInt()) {
            placeable.place(originX.toInt(),originY.toInt())
        }
    }

fun croppingScream(x:Int,y:Int,scalingFactor: Float,outputSize: Int):Modifier
{
   //I'm going to have to draw this to figure out the math - scream






    return Modifier.clip(RectangleShape)
        .border(1.dp,Color.Red,RectangleShape)
        .layout{measurable,constraints ->
            val placeable = measurable.measure(constraints)
            //(x,y) should be the center point of the image
            // that means x value has to be greater than outputSize/2 and less than givenWidth - outputSize/2
            //same for y (just swap givenHeight for givenWidth)
            //place changes the location of top left corner, and moves that to be (x-outputsize/2, y-outputsize/2)
            val givenWidth = placeable.width
            val givenHeight = placeable.height
            val smallestSide =
                if(outputSize <givenHeight && outputSize < givenWidth){
                    outputSize}
                else{
                    if(givenHeight > givenWidth){
                        givenWidth
                    }
                    else{
                        givenHeight
                    }
                }
            val castX = (givenWidth*x).toInt()
            val castY = (givenHeight*y).toInt()
            val originX = if(castX < outputSize/2){
                outputSize/2
            }else if (castX > givenWidth-outputSize/2) {
                givenWidth-outputSize/2
            }else{
                castX
            }
            val originY = if(castY < outputSize/2){
                outputSize/2
            }else if (castY> givenHeight-outputSize/2) {
                givenHeight-outputSize/2
            }else{
                castY
            }
            if(outputSize!=smallestSide){
                layout(width = smallestSide, height = smallestSide) {
                    placeable.place(0,0)
                }
            }else{
                layout(width = smallestSide, height = smallestSide) {
                    placeable.place(-(originX-outputSize/2), -(originY-outputSize/2))
                }}

        }
        .border(1.dp,Color.Blue,RectangleShape)
        .clip(RectangleShape)
        .layout{measurable,constraints->
            val placeable = measurable.measure(constraints)
            val gwidth = placeable.width*scalingFactor
            val gheight = placeable.height*scalingFactor
            var originX:Float
            var originY:Float
            if(scalingFactor>1){
                originX = (gwidth - placeable.width)/2
                originY = (gheight - placeable.height)/2
            }else{
                originX = -(placeable.width-gwidth)/2
                originY = -(placeable.height-gheight)/2
            }
            layout(width =gwidth.toInt(), height = gheight.toInt()) {
                placeable.place(originX.toInt(),originY.toInt())
            }
        }
        .scale(scalingFactor)

}










