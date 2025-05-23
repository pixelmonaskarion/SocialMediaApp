package com.chrissytopher.socialmedia

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Create
import androidx.compose.material.icons.filled.Crop
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Create
import androidx.compose.material.icons.outlined.Crop
import androidx.compose.material.icons.outlined.Face
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
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
//val LocalAuthenticationManager: ProvidableCompositionLocal<AuthenticationManager> = compositionLocalOf { error("no authentication manager provided") }
//val LocalCacheManager: ProvidableCompositionLocal<CacheManager> = compositionLocalOf { error("no cache manager provided") }
val LocalNavHost = compositionLocalOf<NavigationStack<NavScreen>> { error("no nav host provided") }
//val LocalPermissionsController = compositionLocalOf<PermissionsController>{ error("no permissions controller provided") }
val LocalSnackbarState = compositionLocalOf { SnackbarHostState() }

enum class NavScreen(val selectedIcon: ImageVector, val unselectedIcon: ImageVector, val showInNavBar: Boolean = true, val hideNavBar: Boolean = false) {
    Home(Icons.Filled.Home, Icons.Outlined.Home),
    Login(Icons.Filled.Home, Icons.Outlined.Home, showInNavBar = false, hideNavBar = true),
    Settings(Icons.Filled.Settings, Icons.Outlined.Settings),
    Account(Icons.Filled.AccountCircle, Icons.Outlined.AccountCircle),
    CreatePost(Icons.Filled.Create, Icons.Outlined.Create),
    CropScreen(Icons.Filled.Crop, Icons.Outlined.Crop,showInNavBar = false),
    IconSelect(Icons.Filled.Face, Icons.Outlined.Face, showInNavBar = false)
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
fun App(viewModel: AppViewModel) {
    val navigationStack : NavigationStack<NavScreen> = remember { NavigationStack(if (viewModel.authenticationManager.loggedIn()) NavScreen.Home else NavScreen.Login) }
    val darkMode by viewModel.darkMode
    LocalPlatform.current.AppTheme(darkTheme = darkMode) {
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
                },
                snackbarHost = { SnackbarHost(LocalSnackbarState.current) }
            ) { paddingValues ->
                NavigationController(
                    navigationStack = navigationStack,
                    modifier = Modifier
                        .fillMaxSize()
                        .imePadding()

                ) {
                    composable(route = NavScreen.Home) {
                        HomeScreen(viewModel, paddingValues)
                    }
                    composable(route = NavScreen.Settings) {
                        Box(Modifier.padding(paddingValues)) {
                            Settings(viewModel)
                        }
                    }
                    composable(route = NavScreen.Login) {
                        Box(Modifier.padding(paddingValues)) {
                            Login(viewModel) {
                                navigationStack.clearStack(NavScreen.Home)
                            }
                        }
                    }
                    composable(route = NavScreen.Account) {
                        Box(Modifier.padding(paddingValues)) {
                            AccountSettingScreen(viewModel, navigationStack)
                        }
                    }
                    composable(route = NavScreen.CreatePost) {
                        Box(Modifier.padding(paddingValues)) {
                            CreatePostScreen(viewModel, navigationStack)
                        }
                    }
                    composable(route = NavScreen.CropScreen){
                        Box(Modifier.padding(paddingValues)) {
                            CropScreen(viewModel, navigationStack)
                        }
                    }
                    composable(route = NavScreen.IconSelect){
                        pickIconScreen(viewModel, navigationStack)
                    }
                }
            }
        }
    }
}