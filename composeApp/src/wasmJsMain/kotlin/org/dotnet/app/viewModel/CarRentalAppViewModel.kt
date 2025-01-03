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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.dotnet.app.model.*

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

    val areCarsLoaded = MutableStateFlow(false)

    val isDuringServerCheck = MutableStateFlow(false)

    val pagesCount = MutableStateFlow(0)

    private var _config: AppConfig? = null
    val config: AppConfig
        get() = _config ?: throw IllegalStateException("Config not loaded")



    private val httpClient = HttpClient(Js) {
        install(ContentNegotiation) {
            json()
        }
        install(Auth) {
            // Configure authentication
        }
    }

    val currentPageNumber = MutableStateFlow(1)

//    val currentCarPage = MutableStateFlow(List<Car>(
//        size = 5,
//        init = { index -> Car(0, "RentalService", "Producer", "Model", "Type", "YearOfProduction", 5, 1, "Location") }
//    ))

    val currentCarPage = MutableStateFlow(emptyList<Car>())

//    lateinit var currentCarPage: MutableStateFlow<List<Car>>

//    init {
//        //updateCars()
//        viewModelScope.launch {
//            pagesCount.value = getPageCount()
//            println("Pages count: ${pagesCount.value}")
//            println("fetching first page")
//            currentCarPage.value = getPage(currentPageNumber.value)
//        }
//    }

    init {
        viewModelScope.launch {
            try {
                // First get the page count
                pagesCount.value = getPageCount()
                println("Pages count: ${pagesCount.value}")

                // Then fetch the first page
                println("Fetching first page")
                currentCarPage.value = fetchPage(currentPageNumber.value)
                areCarsLoaded.value = true
            } catch (e: Exception) {
                println("Error in initialization: ${e.message}")
                areCarsLoaded.value = false
            }
        }
        viewModelScope.launch {
            try {
                _config = loadConfig()
                println("Config loaded: ${_config?.redirectUri}")
            } catch (e: Exception) {
                println("Error loading config: ${e.message}")
            }
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

    fun updatePageNumber(pageNumber: Int) {
        currentPageNumber.value = pageNumber
    }

    // Changed to suspend function that returns the cars directly
    private suspend fun fetchPage(page: Int): List<Car> {
        return try {
            val response: HttpResponse = httpClient
                .post("https://user-api-dotnet.azurewebsites.net/api/cars/getPage") {
                    contentType(ContentType.Application.Json)
                    setBody(
                        mapOf(
                            "Page" to page
                        )
                    )
                }

            if (response.status.isSuccess()) {
                println("Fetched car page successfully!")
                println("Response: ${response.body() as String}")
                response.body()
            } else {
                println("Error fetching car page: ${response.status.value}")
                emptyList()
            }
        } catch (e: Exception) {
            println("Exception while fetching car page: ${e.message}")
            emptyList()
        }
    }

    // Function to be called when changing pages
    fun getPage(page: Int) {
        viewModelScope.launch {
            currentCarPage.value = fetchPage(page)
        }
    }

    private suspend fun getPageCount(): Int {
        return try {
            println("Fetching page count...")

            val pageCountResponse = httpClient
                .get("https://user-api-dotnet.azurewebsites.net/api/cars/getCountPages")
                .body<Int>()
            println("Page count loaded: $pageCountResponse")
            pageCountResponse
        } catch (e: Exception) {
            println("Error fetching page count: ${e.message}")
            0
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