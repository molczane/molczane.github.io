package org.dotnet.app

import androidx.compose.runtime.*
import kotlinx.browser.window
import org.dotnet.app.view.LoginScreen
import org.dotnet.app.view.RentCarScreen

@Composable
fun CarRentalApp() {
    val viewModel = CarRentalAppViewModel()
    RentCarScreen(viewModel)
}