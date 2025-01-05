package org.dotnet.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.dotnet.app.presentation.viewModels.CarRentalAppViewModel
import org.dotnet.app.presentation.viewModels.CarRentalUiState

@Composable
fun StandardCarsView(viewModel: CarRentalAppViewModel , uiState: CarRentalUiState, innerPadding: PaddingValues) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        if (uiState.currentCarPage.isNotEmpty()) {
            uiState.currentCarPage.forEach { car ->
                CarDetailsCard(
                    car = car,
                    modifier = Modifier.fillMaxWidth(0.5f)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            PaginationControls(
                currentPage = uiState.currentPageNumber,
                totalPages = uiState.totalPages,
                onPageSelected = { newPage ->
                    viewModel.updatePageNumber(newPage)
                    viewModel.getPage(newPage)
                }
            )
        } else {
            Text("Brak dostępnych samochodów")
        }

        // Footer at the bottom
        Footer()
    }
}