package org.dotnet.app

import androidx.compose.runtime.*
import org.dotnet.app.view.RentCarScreen
import org.dotnet.app.viewModel.CarRentalAppViewModel

@Composable
fun CarRentalApp() {
    val viewModel = CarRentalAppViewModel()
    RentCarScreen(viewModel)
}