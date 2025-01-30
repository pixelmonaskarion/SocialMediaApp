package com.chrissytopher.socialmedia

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ProvidableCompositionLocal
import androidx.compose.runtime.State
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.chrissytopher.socialmedia.navigation.NavigationController
import com.chrissytopher.socialmedia.navigation.NavigationStack
import org.jetbrains.compose.ui.tooling.preview.Preview

val LocalPlatform: ProvidableCompositionLocal<Platform> = compositionLocalOf { error("no platform provided") }
val LocalAuthenticationManager: ProvidableCompositionLocal<AuthenticationManager> = compositionLocalOf { error("no authentication manager provided") }
val LocalNavHost = compositionLocalOf<NavigationStack<NavScreen>> { error("no nav host provided") }

enum class NavScreen(val selectedIcon: ImageVector, val unselectedIcon: ImageVector, val showInNavBar: Boolean = true, val hideNavBar: Boolean = false) {
    Home(Icons.Filled.Home, Icons.Outlined.Home),
    Login(Icons.Filled.Home, Icons.Outlined.Home, showInNavBar = false, hideNavBar = true),
    Settings(Icons.Filled.Settings, Icons.Outlined.Settings),
    CreatePost(Icons.Filled.Create, Icons.Outlined.Create),
}

@Composable
fun AppBottomBar(currentScreenState: State<NavScreen>, select: (NavScreen) -> Unit) {
    val currentScreen by currentScreenState
    NavigationBar {
        NavScreen.entries.filter { it.showInNavBar }.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        if (currentScreen == item) item.selectedIcon else item.unselectedIcon,
                        contentDescription = item.name
                    )
                },
                label = { Text(item.name) },
                selected = currentScreen == item,
                onClick = { select(item) }
            )
        }
    }
}

@Composable
@Preview
fun App() {
    val authManager = LocalAuthenticationManager.current
    val navigationStack : NavigationStack<NavScreen> = remember { NavigationStack(if (authManager.loggedIn()) NavScreen.Home else NavScreen.Login) }
    CompositionLocalProvider(LocalNavHost provides navigationStack) {
        Scaffold(
            bottomBar = {
                val currentNav by navigationStack.routeState
                if (!currentNav.hideNavBar) {
                    AppBottomBar(
                        navigationStack.routeState
                    ) {
                        navigationStack.clearStack(NavScreen.Home)
                        if (it != NavScreen.Home) {
                            navigationStack.navigateTo(it)
                        }
                    }
                }
            }
        ) { paddingValues ->
            NavigationController(
                navigationStack = navigationStack,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .imePadding()

            ) {
                composable(route = NavScreen.Home) {
                    HomeScreen()
                }
                composable(route = NavScreen.Settings) {
//                    SettingsScreen(currentTheme = currentTheme, onThemeChange = {newTheme -> currentTheme = newTheme})
                }
                composable(route = NavScreen.Login) {
                    Login() {
                        navigationStack.clearStack(NavScreen.Home)
                    }
                }
                composable(route = NavScreen.CreatePost) {
                    CreatePostScreen()
                }
            }
        }
    }
}