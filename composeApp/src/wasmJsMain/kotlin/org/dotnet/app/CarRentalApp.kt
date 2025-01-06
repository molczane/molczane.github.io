package org.dotnet.app

import androidx.compose.runtime.*
import org.dotnet.app.presentation.screens.MainAppScreen
import org.dotnet.app.presentation.viewModels.CarRentalAppViewModel

@Composable
fun CarRentalApp() {
    val viewModel = CarRentalAppViewModel()
    MainAppScreen(viewModel)
}