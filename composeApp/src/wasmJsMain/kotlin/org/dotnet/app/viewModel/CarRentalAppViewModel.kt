package org.dotnet.app.viewModel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.client.plugins.auth.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.dotnet.app.model.AuthResponse
import org.dotnet.app.model.Car
import org.dotnet.app.model.Offer
import org.dotnet.app.model.User
import org.w3c.dom.HTMLScriptElement

data class CarRentalAppUiState(
    val listOfCars: List<Car> = emptyList(),
) {
    val producers = listOfCars.map { it.model }.toSet()
}


class CarRentalAppViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CarRentalAppUiState())
    val uiState = _uiState.asStateFlow()

    var user: User? = null
    val isUserLoggedIn = MutableStateFlow(false)

    init {
        //updateCars()
    }

    // In CarRentalAppViewModel
    fun exchangeGoogleAuthCode(
        code: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        // Implement server-side token exchange
        // This method would typically call your backend API to:
        // 1. Exchange the authorization code for an access token
        // 2. Verify the token
        // 3. Create or log in the user
        // 4. Return a session token or user information
    }

    private val httpClient = HttpClient(Js) {
        install(ContentNegotiation) {
            json()
        }
        install(Auth) {
            // Configure authentication
        }
    }

    val rentedCar : Car? = null

    fun updateCars(){
        viewModelScope.launch {
            val cars = getAllCars()
            _uiState.update {
                it.copy(listOfCars = cars)
            }
        }
    }

    private suspend fun getAllCars(): List<Car> {
        return try {
            println("Fetching cars...")

            val carsResponse = httpClient
                .get("http://webapplication2-dev.eba-sstwvfur.us-east-1.elasticbeanstalk.com/api/cars/getAllCars")
                .body<List<Car>>()
            println("Cars loaded: $carsResponse")
            carsResponse
        } catch (e: Exception) {
            println("Error fetching cars: ${e.message}")
            emptyList()
        }
    }

    fun resetValuationResult() {
        _valuationResult.value = null
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

                        println("Logged in as: ${user?.firstname} ${user?.lastname}")

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

    fun requestRent( car: Car, user: User, startDate: String, endDate: String, onRent: (Boolean) -> Unit ) {
        viewModelScope.launch {
            println("Sending rent request...")

            val jsonBody = """
            {
                "startDate": "$startDate",
                "endDate": "$endDate",
                "car": {
                    "id": ${car.id}
                },
                "user": {
                    "id": ${user.id}
                }
            }
            """.trimIndent()

            try {
                val response: HttpResponse = httpClient.post("http://webapplication2-dev.eba-sstwvfur.us-east-1.elasticbeanstalk.com/api/cars/rent") {
                    contentType(ContentType.Application.Json)
                    setBody(jsonBody)
                }

                if (response.status.isSuccess()) {
                    onRent(true)
                }
            } catch (e: Exception) {
                throw e
            }

        }
    }

    //TODO("Zrobić config file")

    private val _valuationResult = MutableStateFlow<Offer?>(null)
    val valuationResult: StateFlow<Offer?> = _valuationResult

    fun requestValuation(startDate: String, endDate: String, car: Car) {
        viewModelScope.launch {
            println("Sending valuation request...")

            val jsonBody = """
            {
                "startDate": "$startDate",
                "endDate": "$endDate",
                "car": {
                    "id": ${car.id},
                    "rentalService": "${car.rentalService}",
                    "producer": "${car.producer}",
                    "model": "${car.model}",
                    "type": "${car.type}",
                    "yearOfProduction": "${car.yearOfProduction}",
                    "numberOfSeats": ${car.numberOfSeats},
                    "isAvailable": ${car.isAvailable},
                    "location": "${car.location}"
                }
            }
            """.trimIndent()

            try {
                val response: HttpResponse = httpClient.post("http://webapplication2-dev.eba-sstwvfur.us-east-1.elasticbeanstalk.com/api/cars/getOffer") {
                    contentType(ContentType.Application.Json)
                    setBody(jsonBody)
                }

                if (response.status.isSuccess()) {
                    _valuationResult.value = response.body() // Zakładamy, że serwer zwraca wycenę jako json
                }
            } catch (e: Exception) {
                throw e
            }
        }
    }

    private val _authResponse = MutableStateFlow<AuthResponse?>(null)
    val authResponse: StateFlow<AuthResponse?> = _authResponse

    fun sendAuthCodeToBackend(authCode: String) {
        viewModelScope.launch {
            try {
                val response: HttpResponse = httpClient
                    .post("http://webapplication2-dev.eba-sstwvfur.us-east-1.elasticbeanstalk.com/api/auth/google") {
                        contentType(ContentType.Application.Json)
                        setBody(
                            mapOf(
                                "authCode" to authCode,
                                "redirectUri" to "https://molczane.github.io/"
                            )
                        )
                    }

                if (response.status.isSuccess()) {
                    _authResponse.value = response.body() // Zakładamy, że serwer zwraca wycenę jako json
                }
                // Zapisanie tokenu sesji

                // Zapisanie informacji o użytkowniku

                // Przekierowanie lub zmiana stanu aplikacji
//                if (authResponse.isNewUser) {
//                    navigateToProfileCompletion()
//                } else {
//                    navigateToDashboard()
//                }
            } catch (e: Exception) {
                // Obsługa błędów logowania
                //handleLoginError(e)
                throw e
            }
        }
    }
}