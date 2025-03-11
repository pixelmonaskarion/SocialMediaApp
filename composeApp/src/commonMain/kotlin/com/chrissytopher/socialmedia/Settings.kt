package com.chrissytopher.socialmedia

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp

val likeIcons = listOf(
    Pair(Icons.Outlined.FavoriteBorder, Icons.Outlined.Favorite),
    Pair(Icons.Outlined.ThumbUp, Icons.Sharp.ThumbUp),
    Pair(Icons.Outlined.Forward, Icons.Sharp.Forward)
)

@Composable
fun Settings (viewModel: AppViewModel) {
    val selectedLikeIcon by viewModel.likeIcon
    Column {
        Text("Like Icon:", Modifier.padding(10.dp, 0.dp))
        Row{
            for (i in 0..2) {
                ElevatedFilterChip(selected = selectedLikeIcon == i, onClick = { viewModel.setLikeIcon(i) }, label = { Icon(if (selectedLikeIcon != i) {
                    likeIcons[i].first} else {
                    likeIcons[i].second}, null)})
            }
        }
    }
}
