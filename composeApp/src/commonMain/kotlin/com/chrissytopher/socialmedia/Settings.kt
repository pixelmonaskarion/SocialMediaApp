package com.chrissytopher.socialmedia

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Forward
import androidx.compose.material.icons.outlined.ThumbUp
import androidx.compose.material.icons.sharp.Forward
import androidx.compose.material.icons.sharp.ThumbUp
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

val likeIcons = listOf(
    Pair(Icons.Outlined.FavoriteBorder, Icons.Outlined.Favorite),
    Pair(Icons.Outlined.ThumbUp, Icons.Sharp.ThumbUp),
    Pair(Icons.Outlined.Forward, Icons.Sharp.Forward)
)

@Composable
fun Settings (viewModel: AppViewModel) {
    val settingFormat by viewModel.settingFormat
    if (settingFormat != 0) {
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


            settingToggle(settingFormat, "Quag Toggle ", quag) {
                viewModel.toggleQuag()
            }

            val systemDarkTheme = isSystemInDarkTheme()
            settingToggle(settingFormat, "Dark Mode ", darkMode ?: systemDarkTheme) {
                viewModel.setDarkMode(darkMode?.not() ?: !systemDarkTheme)
            }

            settingToggle(settingFormat, "Setting format", (settingFormat == 1)) {
                viewModel.setSettingFormat(if (settingFormat == 1) 2 else 1)
            }

        }
    } else {
        settingFormatPicker(viewModel)
    }
}

@Composable
fun settingToggle(type: Int, key: String, setting: Boolean, func: () -> Unit) {
    val typeModifier =
        when (type) {
            1 -> Pair(Modifier.fillMaxWidth(), Arrangement.SpaceBetween)
            else -> Pair(Modifier, Arrangement.Start)
        }

    Row(Modifier.padding(5.dp).height(30.dp).then(typeModifier.first), horizontalArrangement = typeModifier.second) {
        Text(key, Modifier.align(Alignment.CenterVertically), style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface)
        Switch(checked = setting, onCheckedChange = { func() })
    }
}

@Composable
fun settingFormatPicker(viewModel: AppViewModel) {
    viewModel.setSettingFormat(1)
//    Column {
//        Text("Select A format", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(20.dp))
//        Row {
//            Card(Modifier.weight(1f).padding(5.dp)) {
//                Column {
//                    for (n in 1..3) {
//                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
//                            Text("setting ")
//                            Icon(Icons.Outlined.ToggleOn, null)
//                        }
//                    }
//                }
//            }
//            Card(Modifier.weight(1f).padding(5.dp)) {
//                Column {
//                    for (n in 1..3) {
//                        Row() {
//                            Text("setting " + "n".repeat(n%3))
//                            Icon(Icons.Outlined.ToggleOn, null)
//                        }
//                    }
//                }
//            }
//        }
//    }
}

