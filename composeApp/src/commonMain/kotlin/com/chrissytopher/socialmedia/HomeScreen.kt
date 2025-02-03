package com.chrissytopher.socialmedia

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.rounded.AccountCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen() {
    Box(Modifier.fillMaxSize().padding(10.dp)) {
        Column {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Social Media App", style = MaterialTheme.typography.titleLarge)
                Row(
                    Modifier.width(130.dp).height(50.dp).padding(20.dp, 0.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(Modifier.size(45.dp).clip(CircleShape).selectable(false) {

                    }) {
                        Icon(Icons.Outlined.FavoriteBorder, null, Modifier.align(Alignment.Center))
                    }
                    Box(Modifier.size(45.dp).clip(CircleShape).selectable(false) {

                    }) {
                        Icon(
                            Icons.Outlined.ChatBubbleOutline,
                            null,
                            Modifier.align(Alignment.Center)
                        )
                    }
                }
            }
            Row(Modifier.height(100.dp).horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.SpaceBetween) {
                for(i in 1..5) {
                    Icon(Icons.Rounded.AccountCircle, null, Modifier.size(100.dp))
                }
            }
        }
    }
}