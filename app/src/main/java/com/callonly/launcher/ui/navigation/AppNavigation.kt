package com.callonly.launcher.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.callonly.launcher.ui.admin.AdminScreen
import com.callonly.launcher.ui.home.HomeScreen
import com.callonly.launcher.data.model.Contact

@Composable
fun CallOnlyNavGraph(
    onCall: (Contact) -> Unit // Callback to handle actual calling
) {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(
                onContactClick = onCall,
                onAdminClick = { navController.navigate("admin") }
            )
        }
        composable("admin") {
            AdminScreen(
                onExit = { navController.popBackStack() }
            )
        }
    }
}
