package org.dotnet.app.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import org.dotnet.app.presentation.viewModels.CarRentalAppViewModel
import org.dotnet.app.presentation.viewModels.CarRentalUiState
import org.dotnet.app.utils.AppState

@Composable
fun FilteredCarsView(viewModel: CarRentalAppViewModel, uiState: CarRentalUiState, innerPadding: PaddingValues) {
    // Pagination state
    var currentPage by remember { mutableStateOf(1) }
    val carsPerPage = 5 // Number of cars to show per page
    val totalPages = (uiState.filteredCars.size + carsPerPage - 1) / carsPerPage // Calculate total pages

    // Cars for the current page
    val paginatedCars = uiState.filteredCars
        .drop((currentPage - 1) * carsPerPage)
        .take(carsPerPage)

    Column(
        Modifier
            .fillMaxSize()
            .padding(innerPadding)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        if (paginatedCars.isNotEmpty()) {
            paginatedCars.forEach { car ->
                CarDetailsCard(
                    car = car,
                    modifier = Modifier.fillMaxWidth(0.5f),
                    onClick = {
                        viewModel.changeAppState(AppState.CarDetails)
                        viewModel.updatedSelectedCar(car)
                    }
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            PaginationControls(
                currentPage = currentPage,
                totalPages = totalPages,
                onPageSelected = { newPage ->
                    currentPage = newPage
                }
            )
        } else {
            Text("Brak dostępnych samochodów z zastosowanymi filtrami")
        }

//        if (uiState.filteredCars.isNotEmpty()) {
//            uiState.filteredCars.forEach { car ->
//                CarDetailsCard(
//                    car = car,
//                    modifier = Modifier.fillMaxWidth(0.5f),
//                    onClick = {
//                        viewModel.changeAppState(AppState.CarDetails)
//                        viewModel.updatedSelectedCar(car)
//                    }
//                )
//                Spacer(modifier = Modifier.height(16.dp))
//            }
//        } else {
//            Text("Brak dostępnych samochodów z zastosowanymi filtrami")
//        }

        Footer()
    }
}

