package org.dotnet.app.domain

import io.ktor.client.fetch.*
import kotlinx.browser.window
import kotlinx.coroutines.await
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

@Serializable
data class AppConfig(
    val clientId: String,
    val redirectUri: String,
    val getNumberOfPagesUrl: String,
    val getCarsFromPageUrl: String,
    val googleAuthUrl: String,
    val authWithServerUrl: String,
    val distinctBrandsUrl: String,
    val modelsByBrandUrl: String,
    val distinctYearsUrl: String,
    val distinctTypesUrl: String,
    val distinctLocationsUrl: String,
    val getFilteredCarsUrl: String
)

suspend fun loadConfig(): AppConfig {
    try {
        // Wczytaj plik konfiguracyjny używając fetch API
        val response: Response = window
            .fetch("/config.dev.json")
            .await()

        // PROD
//        val response: Response = window
//            .fetch("/config.prod.json")
//            .await()

        if (!response.ok) {
            throw Exception("Nie udało się załadować pliku konfiguracyjnego: ${response.statusText}")
        }
        else {
            println("Udało się załadować plik konfiguracyjny: ${response.status}")
        }

        val configText : String = response.text().await<JsString>().toString()

        println("configText: $configText")

        // Dekoduj JSON do obiektu AppConfig
        return Json.decodeFromString<AppConfig>(configText)
    } catch (e: Exception) {
        throw Exception("Błąd podczas ładowania konfiguracji: ${e.message}")
    }
}
