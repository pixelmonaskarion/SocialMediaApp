package com.chrissytopher.socialmedia

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Forward
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.sharp.Forward
import androidx.compose.material.icons.sharp.ThumbUp
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate

@Composable
fun Settings () {
    var selectedLikeIcon by remember { mutableStateOf(1) }
    Column {
        Text("Like Icon:")
        Row{
            ElevatedFilterChip(selected = selectedLikeIcon == 1, onClick = { selectedLikeIcon = 1 }, label = { Icon(if (selectedLikeIcon != 1) {Icons.Outlined.FavoriteBorder} else {Icons.Outlined.Favorite}, null) })
            ElevatedFilterChip(selected = selectedLikeIcon == 2, onClick = { selectedLikeIcon = 2 }, label = { Icon(if (selectedLikeIcon != 2) {Icons.Outlined.ThumbUp} else {Icons.Sharp.ThumbUp}, null) })
            ElevatedFilterChip(selected = selectedLikeIcon == 3, onClick = { selectedLikeIcon = 3 }, label = { Icon(if (selectedLikeIcon != 3) {Icons.Outlined.Forward} else {Icons.Sharp.Forward}, null, Modifier.rotate(-90.0F)) })
        }
    }
}
