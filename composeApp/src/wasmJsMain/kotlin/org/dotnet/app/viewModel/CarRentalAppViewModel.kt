package org.dotnet.app.viewModel

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
import kotlinx.browser.localStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.dotnet.app.data.api.ApiService
import org.dotnet.app.data.api.ApiServiceImpl
import org.dotnet.app.data.repository.CarRepository
import org.dotnet.app.domain.*

data class CarRentalAppUiState(
    val listOfCars: List<Car> = emptyList(),
) {
    val producers = listOfCars.map { it.model }.toSet()
}

class CarRentalAppViewModel : ViewModel() {

    // app config
    private var _config: AppConfig? = null
    private val config: AppConfig
        get() = _config ?: throw IllegalStateException("Config not loaded")

    // data layer
    private lateinit var apiService : ApiService
    private lateinit var carRepository : CarRepository

    init {
        initializeDataLayer()
    }

    private fun initializeDataLayer() {
        viewModelScope.launch {
            try {
                // Load configuration first
                _config = loadConfig()
                println("Config loaded: ${_config?.redirectUri}")

                // Initialize ApiService and CarRepository
                apiService = ApiServiceImpl(config)
                carRepository = CarRepository(apiService)

                // Fetch initial data after initialization
                 loadInitialData()
            } catch (e: Exception) {
                println("Error initializing data layer: ${e.message}")
            }
        }
    }


    private suspend fun loadInitialData() {
        try {
            // First get the page count
            val pageCount = carRepository.getPageCount()
            println("Pages count: $pageCount")

            // Then fetch the first page
            val firstPage = carRepository.getCarsPage(1)
            println("First page loaded: $firstPage")
        } catch (e: Exception) {
            println("Error loading initial data: ${e.message}")
        }
    }

    private val _uiState = MutableStateFlow(CarRentalAppUiState())
    val uiState = _uiState.asStateFlow()

    var user: User? = null
    val isUserLoggedIn = MutableStateFlow(false)

    val areCarsLoaded = MutableStateFlow(false)

    val isDuringServerCheck = MutableStateFlow(false)

    val pagesCount = MutableStateFlow(0)

    private val httpClient = HttpClient(Js) {
        install(ContentNegotiation) {
            json()
        }
        install(Auth) {
            // Configure authentication
        }
    }

    val currentPageNumber = MutableStateFlow(1)

    val currentCarPage = MutableStateFlow(emptyList<Car>())

    val rentedCar : Car? = null

    fun updatePageNumber(pageNumber: Int) {
        currentPageNumber.value = pageNumber
    }

    // Function to be called when changing pages
    fun getPage(page: Int) {
        viewModelScope.launch {
            currentCarPage.value = carRepository.getCarsPage(page)
        }
    }

    fun resetValuationResult() {
        _valuationResult.value = null
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

    fun storeAuthToken(token: String) {
        localStorage.setItem("auth_token", token)
    }

    fun getStoredToken(): String? {
        return localStorage.getItem("auth_token")
    }

    private val _authResponse = MutableStateFlow<AuthResponse?>(null)
    val authResponse: StateFlow<AuthResponse?> = _authResponse

    fun logout() {
        isUserLoggedIn.value = false
        user = null
        localStorage.removeItem("auth_token")
        _authResponse.value = null

        println("Logged out successfully")
    }

    fun sendAuthCodeToBackend(authCode: String) {
        viewModelScope.launch {
            try {
                val response: HttpResponse = httpClient
                    .post("https://user-api-dotnet.azurewebsites.net/api/users/google") {
                        contentType(ContentType.Application.Json)
                        setBody(
                            mapOf(
                                "Code" to authCode,
                                "RedirectUri" to "https://molczane.github.io/"
                            )
                        )
                    }

                if (response.status.isSuccess()) {
                    _authResponse.value = response.body() // Zakładamy, że serwer zwraca wycenę jako json
                    isUserLoggedIn.value = true

                    // Store the token after successful login
                    _authResponse.value?.token?.let { token ->
                        storeAuthToken(token)
                    }

                    isDuringServerCheck.value = false
                }

                println("Auth response: ${_authResponse.value?.user!!.name}")
                println("Auth response: ${_authResponse.value?.user!!.email}")
                // Zapisanie tokenu sesji

                // Zapisanie informacji o użytkowniku

                // Przekierowanie lub zmiana stanu aplikacji

            } catch (e: Exception) {
                // Obsługa błędów logowania
                throw e
            }
        }
    }
}