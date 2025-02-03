package com.chrissytopher.socialmedia

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Forward
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable

@Composable
fun Settings () {
    var selectedLikeIcon = 0
    Column {
        Row{
            Text("Like Icon:")
        ElevatedFilterChip(selected = selectedLikeIcon == 1, onClick = { selectedLikeIcon = 1 }, label = { Icon(Icons.Outlined.Favorite, null) })
        ElevatedFilterChip(selected = selectedLikeIcon == 2, onClick = { selectedLikeIcon = 2 }, label = { Icon(Icons.Outlined.ThumbUp, null) })
            ElevatedFilterChip(
                selected = selectedLikeIcon == 3,
                onClick = { selectedLikeIcon = 3 },
                label = { Icon(Icons.Outlined.Forward, null) })
        }
    }
}
