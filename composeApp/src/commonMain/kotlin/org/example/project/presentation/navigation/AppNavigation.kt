package org.example.project.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.LocalDining
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import org.example.project.data.remote.GeminiService
import org.example.project.data.remote.createHttpClient
import org.example.project.domain.repository.AiRepositoryImpl
import org.example.project.presentation.chat.ChatScreen
import org.example.project.presentation.chat.ChatViewModel
import org.example.project.presentation.chat.NutritionScreen
import androidx.compose.foundation.layout.Box
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.padding

/** Destinasi navigasi aplikasi */
sealed class Screen(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    data object Chat      : Screen("chat",      "Chat AI",  Icons.Filled.Chat)
    data object Nutrition : Screen("nutrition", "Nutrisi",  Icons.Filled.LocalDining)
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    val httpClient    = remember { createHttpClient() }
    val geminiService = remember { GeminiService(httpClient) }
    val repository    = remember { AiRepositoryImpl(geminiService) }
    val viewModel     = remember { ChatViewModel(repository) }

    val screens = listOf(Screen.Chat, Screen.Nutrition)

    // SESUDAH - jangan pass innerPadding, biarkan NavHost pakai padding sendiri
    Scaffold(
        bottomBar = { BottomNavBar(navController, screens) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {  // ← Box pakai padding
            NavHost(
                navController    = navController,
                startDestination = Screen.Chat.route
            ) {
                composable(Screen.Chat.route) {
                    ChatScreen(viewModel = viewModel)  // ← hapus bottomPadding
                }
                composable(Screen.Nutrition.route) {
                    NutritionScreen(viewModel = viewModel)  // ← hapus bottomPadding
                }
            }
        }
    }
}

@Composable
private fun BottomNavBar(navController: NavController, screens: List<Screen>) {
    val backStack    by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route

    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor   = MaterialTheme.colorScheme.primary
    ) {
        screens.forEach { screen ->
            val selected = currentRoute == screen.route
            NavigationBarItem(
                selected = selected,
                onClick  = {
                    navController.navigate(screen.route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState    = true
                    }
                },
                label = { Text(screen.title) },
                icon  = {
                    Icon(
                        imageVector        = screen.icon,
                        contentDescription = screen.title
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor   = MaterialTheme.colorScheme.primary,
                    selectedTextColor   = MaterialTheme.colorScheme.primary,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    indicatorColor      = MaterialTheme.colorScheme.primaryContainer
                )
            )
        }
    }
}