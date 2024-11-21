package org.dotnet.app

import androidx.compose.runtime.*
import org.dotnet.app.view.RentCarScreen

@Composable
fun CarRentalApp() {
    val viewModel = CarRentalAppViewModel()
    RentCarScreen(viewModel)
}