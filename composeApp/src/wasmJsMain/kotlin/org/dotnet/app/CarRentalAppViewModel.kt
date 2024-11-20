package org.dotnet.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import org.dotnet.app.model.Car
import org.dotnet.app.model.User

data class CarRentalAppUiState(
    val listOfCars: List<Car> = emptyList(),
) {
    val brands = listOfCars.map { it.brand }.toSet()
}


class CarRentalAppViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CarRentalAppUiState())
    val uiState = _uiState.asStateFlow()

    private val user: User? = null

    init {
        //updateCars()
    }

    private val httpClient = HttpClient {
        install(ContentNegotiation) {
            json()
        }
    }

    private fun updateCars(){
        viewModelScope.launch {
            val cars = getAllCars()
            _uiState.update {
                it.copy(listOfCars = cars)
            }
        }
    }

    private suspend fun getAllCars(): List<Car> {
        val carsResponse =  httpClient
            .get("http://webapplication2-dev.eba-sstwvfur.us-east-1.elasticbeanstalk.com/api/getAllCars")
            .body<List<Car>>()

        return carsResponse
    }

}