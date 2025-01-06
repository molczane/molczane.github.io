package org.dotnet.app.presentation.viewModels

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
import org.dotnet.app.domain.authentication.AuthResponse
import org.dotnet.app.domain.cars.Car
import org.dotnet.app.domain.cars.CarFilters
import org.dotnet.app.domain.config.AppConfig
import org.dotnet.app.domain.config.loadConfig
import org.dotnet.app.domain.offer.Offer
import org.dotnet.app.domain.user.User
import org.dotnet.app.utils.AppState

data class CarRentalUiState(
    val currentCarPage: List<Car> = emptyList(),
    val currentPageNumber: Int = 1,
    val totalPages: Int = 0,
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    val selectedCar: Car? = null,
    val isLoginDialogShown: Boolean = false,
    val loginResult: String? = null,
    val isValuationDialogShown: Boolean = false,

    /* DATA FOR FILTERING */
    val distinctBrands: List<String> = emptyList(),
    val modelsByBrand: List<String> = emptyList(),
    val distinctYears: List<Int> = emptyList(),
    val distinctTypes: List<String> = emptyList(),
    val distinctLocations: List<String> = emptyList(),

    /* FILTERING STATE */
    val selectedBrand: String? = null,
    val selectedModel: String? = null,
    val selectedYear: String? = null,
    val selectedType: String? = null,
    val selectedLocation: String? = null,

    /* FILTERING RESULTS */
    val areCarsFiltered: Boolean = false,
    val filteredCars: List<Car> = emptyList(),

    /* USER RELATED STUFF */
    val isUserScreenShown: Boolean = false,
    val isUserLoggedIn: Boolean = false,
    val user: User? = null,
    val isUserFullyRegistered: Boolean = false,

    /* APP STATE */
    val appState: AppState = AppState.Default
)

class CarRentalAppViewModel : ViewModel() {
    // UI State
    private val _uiState = MutableStateFlow(CarRentalUiState())
    val uiState : StateFlow<CarRentalUiState> = _uiState.asStateFlow()

    // app config
    private var _config: AppConfig? = null
    private val config: AppConfig
        get() = _config ?: throw IllegalStateException("Config not loaded")

    // if app config is loaded
    private val _isConfigLoaded = MutableStateFlow(false)
    val isConfigLoaded: StateFlow<Boolean> = _isConfigLoaded.asStateFlow()

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
                _isConfigLoaded.value = true
                // Initialize ApiService and CarRepository
                apiService = ApiServiceImpl(config)
                carRepository = CarRepository(apiService)

                // Fetch initial data after initialization
                loadInitialData()

                // Fetch data for filtering
                loadFilterData()
            } catch (e: Exception) {
                println("Error initializing data layer: ${e.message}")
            }
        }
    }

    private suspend fun loadInitialData() {
        updateUiState { it.copy(isLoading = true) }

        try {
            val pageCount = carRepository.getPageCount() - 1
            println("Pages count: $pageCount")
            //pagesCount.value = pageCount

            val firstPage = carRepository.getCarsPage(1)
            println("First page loaded: $firstPage")

            updateUiState {
                it.copy(
                    currentCarPage = firstPage,
                    currentPageNumber = 1,
                    totalPages = pageCount,
                    isLoading = false,
                    errorMessage = null
                )
            }
        } catch (e: Exception) {
            println("Error loading initial data: ${e.message}")
            // If there's an error, you might want areCarsLoaded.value = false
            updateUiStateWithError("Error loading initial data: ${e.message}")
        }
    }

    // Function to be called when changing pages
    fun getPage(page: Int) {
        viewModelScope.launch {
            try {
                updateUiState { it.copy(isLoading = true) }

                val cars = carRepository.getCarsPage(page)
                updateUiState {
                    it.copy(
                        currentCarPage = cars,
                        currentPageNumber = page,
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (e: Exception) {
                updateUiStateWithError("Error fetching page $page: ${e.message}")
            }
        }
    }

    private fun loadFilterData() {
        viewModelScope.launch {
            try {
                updateUiState { it.copy(isLoading = true) }

                val brands = apiService.getDistinctBrands()
                val years = apiService.getDistinctYears()
                val types = apiService.getDistinctTypes()
                val locations = apiService.getDistinctLocations()

                updateUiState {
                    it.copy(
                        distinctBrands = brands,
                        distinctYears = years,
                        distinctTypes = types,
                        distinctLocations = locations,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                updateUiState {
                    it.copy(
                        isLoading = false,
                        errorMessage = "Error loading filter data: ${e.message}"
                    )
                }
            }
        }
    }
    /* =================================== Updating UI State functions =================================== */
    private fun updateUiState(update: (CarRentalUiState) -> CarRentalUiState) {
        _uiState.value = update(_uiState.value)
    }

    private fun updateUiStateWithError(error: String) {
        updateUiState { it.copy(isLoading = false, errorMessage = error) }
        println(error)
    }

    fun updateFilter(
        brand: String? = uiState.value.selectedBrand,
        model: String? = uiState.value.selectedModel,
        year: String? = uiState.value.selectedYear,
        type: String? = uiState.value.selectedType,
        location: String? = uiState.value.selectedLocation
    ) {
        _uiState.value = uiState.value.copy(
            selectedBrand = brand,
            selectedModel = model,
            selectedYear = year,
            selectedType = type,
            selectedLocation = location
        )
    }

    fun updatePageNumber(pageNumber: Int) {
        // currentPageNumber.value = pageNumber
        _uiState.value = uiState.value.copy(
            currentPageNumber = pageNumber
        )
    }

    fun updateSelectedBrand(brand: String?) {
        updateUiState {
            it.copy(
                selectedBrand = brand,
                selectedModel = null // Reset model when brand changes
            )
        }
    }

    fun updateSelectedModel(model: String?) {
        updateUiState { it.copy(selectedModel = model) }
    }

    fun updateSelectedYear(year: String?) {
        updateUiState { it.copy(selectedYear = year) }
    }

    fun updateSelectedType(type: String?) {
        updateUiState { it.copy(selectedType = type) }
    }

    fun updateSelectedLocation(location: String?) {
        updateUiState { it.copy(selectedLocation = location) }
    }

    fun resetFilters() {
        updateUiState {
            it.copy(
                selectedBrand = null,
                selectedModel = null,
                selectedYear = null,
                selectedType = null,
                selectedLocation = null,
                areCarsFiltered = false,
                filteredCars = emptyList()
            )
        }
    }

    // Function to be called when fetching filtered cars
    fun getFilteredCars(carFilters: CarFilters) {
        viewModelScope.launch {
            try {
                updateUiState { it.copy(isLoading = true) }

                val filteredCars = carRepository.getCarsFiltered(carFilters)

                if(uiState.value.errorMessage == null) {
                    println("Successfully fetched filtered cars!")
                    updateUiState { it.copy(
                        isLoading = false,
                        areCarsFiltered = true,
                        filteredCars = filteredCars
                    ) }
                    println("Filtered cars: $filteredCars")
                }
            } catch (e: Exception) {
                updateUiStateWithError("Error fetching filtered cars: ${e.message}")
            }
        }
    }

    fun changeAppState(appState: AppState) {
        updateUiState { it.copy(appState = appState) }
    }
    /* ================================================================================================== */

    /* ================================== Google Sign In related stuff ================================== */
    fun initiateGoogleSignIn() {
        val scope = "openid%20email%20profile"
        val googleOAuthUrl = buildString {
            append("https://accounts.google.com/o/oauth2/v2/auth")
            append("?client_id=${config.clientId}")
            append("&redirect_uri=${config.redirectUri}")
            append("&response_type=code")
            append("&scope=$scope")
            append("&state=${generateRandomState()}")
        }
        kotlinx.browser.window.location.href = googleOAuthUrl
    }

    fun handleLoginResult(authCode: String, onLoginSuccess: () -> Unit) {
        viewModelScope.launch {
            updateUiState { it.copy(isLoading = true, loginResult = null) }
            try {
                val response = apiService.authenticate(authCode)
                updateUiState {
                    it.copy(
                        isLoading = false,
                        isUserLoggedIn = true,
                        isLoginDialogShown = false,
                        loginResult = null // Clear any previous errors
                    )
                }
                onLoginSuccess()
            } catch (e: Exception) {
                updateUiState {
                    it.copy(
                        isLoading = false,
                        loginResult = "Login error: ${e.message}"
                    )
                }
            }
        }
    }

    private fun generateRandomState(): String {
        return (1..32)
            .map { ('a'..'z') + ('A'..'Z') + ('0'..'9') }
            .map { it.random() }
            .joinToString("")
    }

    fun toggleLoginDialog(show: Boolean) {
        updateUiState { it.copy(isLoginDialogShown = show) }
    }

    fun toggleUserScreen(show: Boolean) {
        updateUiState { it.copy(isUserScreenShown = show) }
    }

    private val _authResponse = MutableStateFlow<AuthResponse?>(null)
    val authResponse: StateFlow<AuthResponse?> = _authResponse

    fun sendAuthCodeToBackend(authCode: String) {
        if (!_isConfigLoaded.value) {
            println("Config not loaded, delaying authentication.")
            return
        }

        viewModelScope.launch {
            try {
                val response: HttpResponse = httpClient
                    .post(config.authWithServerUrl) {
                        contentType(ContentType.Application.Json)
                        setBody(
                            mapOf(
                                "Code" to authCode,
                                "RedirectUri" to config.redirectUri
                            )
                        )
                    }

                //val response :  = apiService.authenticate(authCode)

                if (response.status.isSuccess()) {
                    _authResponse.value = response.body() // Zakładamy, że serwer zwraca wycenę jako json

                    val newUserDTO = _authResponse.value?.user
                    val newUser = User(
                        id = newUserDTO?.id ?: 0,
                        firstname = newUserDTO?.name.orEmpty(),
                        email = newUserDTO?.email.orEmpty()
                    )

                    updateUiState {
                        it.copy(
                            isUserLoggedIn = true,
                            user = newUser,
                            isUserFullyRegistered = !(_authResponse.value?.isNewUser ?: false)
                        )
                    }

                    // Store the token after successful login
                    _authResponse.value?.token?.let { token ->
                        storeAuthToken(token)
                        println("Stored token: $token")
                    }
                }

                println("Auth response (name): ${_authResponse.value?.user!!.name}")
                println("Auth response (email): ${_authResponse.value?.user!!.email}")
                println("Auth response: ${_authResponse.value}")
            } catch (e: Exception) {
                // Obsługa błędów logowania
                throw e
            }
        }
    }

    fun logout() {
        updateUiState {
            it.copy(
                isUserLoggedIn = false,
                user = null
            )
        }
        user = null
        localStorage.removeItem("auth_token")
        _authResponse.value = null

        println("Logged out successfully")
    }

    fun storeAuthToken(token: String) {
        localStorage.setItem("auth_token", token)
    }

    fun getStoredToken(): String? {
        return localStorage.getItem("auth_token")
    }
    /* ================================================================================================== */

    /* ==================================== Code to be refactored ======================================= */
    var user: User? = null

    private val httpClient = HttpClient(Js) {
        install(ContentNegotiation) {
            json()
        }
        install(Auth) {
            // Configure authentication
        }
    }

    val rentedCar : Car? = null

    fun resetValuationResult() {
        _valuationResult.value = null
    }

    fun requestRent(car: Car, user: User, startDate: String, endDate: String, onRent: (Boolean) -> Unit ) {
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

    fun updateUser(updatedUser: User) {
        updateUiState {
            it.copy(
                user = updatedUser
            )
        }
    }
    /* ================================================================================================== */
}