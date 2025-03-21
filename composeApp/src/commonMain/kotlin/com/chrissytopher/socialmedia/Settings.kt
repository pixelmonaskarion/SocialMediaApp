package com.chrissytopher.socialmedia

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Forward
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.outlined.ToggleOff
import androidx.compose.material.icons.outlined.ToggleOn
import androidx.compose.material.icons.sharp.Forward
import androidx.compose.material.icons.sharp.ThumbUp
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.chrissytopher.socialmedia.theme.darkScheme

val likeIcons = listOf(
    Pair(Icons.Outlined.FavoriteBorder, Icons.Outlined.Favorite),
    Pair(Icons.Outlined.ThumbUp, Icons.Sharp.ThumbUp),
    Pair(Icons.Outlined.Forward, Icons.Sharp.Forward)
)

@Composable
fun Settings (viewModel: AppViewModel) {
    if (viewModel.settingFormat.value == 0) {
        val selectedLikeIcon by viewModel.likeIcon
        val quag by viewModel.quag
        val darkMode by viewModel.darkMode
        Column {
            Text(
                "Like Icon:",
                Modifier.padding(10.dp, 0.dp),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Row {
                for (i in 0..2) {
                    ElevatedFilterChip(
                        selected = selectedLikeIcon == i,
                        onClick = { viewModel.setLikeIcon(i) },
                        label = {
                            Icon(
                                if (selectedLikeIcon != i) {
                                    likeIcons[i].first
                                } else {
                                    likeIcons[i].second
                                }, null
                            )
                        })
                }
            }


            settingToggle(1, "Quag Toggle ", quag) {
                viewModel.toggleQuag()
            }

            val systemDarkTheme = isSystemInDarkTheme()
            settingToggle(1, "Dark Mode ", darkMode ?: systemDarkTheme) {
                viewModel.setDarkMode(darkMode?.not() ?: !systemDarkTheme)
            }

        }
    } else {
        viewModel.setSettingFormat(1)
    }
}

@Composable
fun settingToggle(type: Int, key: String, setting: Boolean, func: () -> Unit) {
    val typeModifier = when (type) {
        1 -> Modifier.fillMaxWidth()
        else -> Modifier
    }
    Row(Modifier.padding(5.dp).height(30.dp).then(typeModifier)) {
        Text(
            key,
            Modifier.align(Alignment.CenterVertically),
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.onSurface
        )
        Switch(checked = setting, onCheckedChange = { func() })
    }
}

