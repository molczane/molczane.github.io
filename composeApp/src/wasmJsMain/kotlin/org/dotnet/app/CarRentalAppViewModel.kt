package org.dotnet.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.dotnet.app.model.Car
import org.dotnet.app.model.Offer
import org.dotnet.app.model.User

data class CarRentalAppUiState(
    val listOfCars: List<Car> = emptyList(),
) {
    val producers = listOfCars.map { it.model }.toSet()
}


class CarRentalAppViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CarRentalAppUiState())
    val uiState = _uiState.asStateFlow()

    private var user: User? = null
    val isUserLoggedIn = MutableStateFlow(false)

    init {
        updateCars()
    }

    private val httpClient = HttpClient(Js) {
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
            .get("http://webapplication2-dev.eba-sstwvfur.us-east-1.elasticbeanstalk.com/api/cars/getAllCars")
            .body<List<Car>>()

        return carsResponse
    }

    fun signIn(login: String, password: String, onLoginResultChange: (String?) -> Unit, onIsLoadingChange: (isLoading: Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                println("Logging...")

                val response: HttpResponse = httpClient
                    .post("http://webapplication2-dev.eba-sstwvfur.us-east-1.elasticbeanstalk.com/api/users/signIn") {
                        contentType(ContentType.Application.Json)
                        setBody(
                            mapOf(
                                "login" to login,
                                "password" to password
                            )
                        )
                    }

                withContext(Dispatchers.Main) {
                    if (response.status.value == 200) {
                        user = response.body()
                        isUserLoggedIn.value = true

                        println("Logged in as: ${user?.first_Name} ${user?.last_Name}")

                        onLoginResultChange("Login successful!")
                    } else {
                        onLoginResultChange("Login failed: ${response.status.value}")
                    }
                    onIsLoadingChange(false)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    onLoginResultChange("Error: ${e.message}")
                    onIsLoadingChange(false)
                }
            }
        }
    }

    private val _valuationResult = MutableStateFlow<Offer?>(null)
    val valuationResult: StateFlow<Offer?> = _valuationResult

    fun requestValuation(startDate: String, endDate: String, car: Car) {
        viewModelScope.launch {
            try {
                val response: HttpResponse = httpClient.post("http://webapplication2-dev.eba-sstwvfur.us-east-1.elasticbeanstalk.com/api/cars/getOffer") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        mapOf(
                            "startDate" to startDate,
                            "endDate" to endDate,
                            "car" to car
                        )
                    )
                }

                if (response.status.isSuccess()) {
                    _valuationResult.value = response.body() // Zakładamy, że serwer zwraca wycenę jako json
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }
}