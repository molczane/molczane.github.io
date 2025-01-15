package org.dotnet.app.data.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.browser.localStorage
import org.dotnet.app.domain.authentication.AfterRegisterResponse
import org.dotnet.app.domain.config.AppConfig
import org.dotnet.app.domain.authentication.AuthResponse
import org.dotnet.app.domain.cars.Car
import org.dotnet.app.domain.offer.Offer
import org.dotnet.app.domain.offer.OfferRequest
import org.dotnet.app.domain.user.User

class ApiServiceImpl(private val appConfig: AppConfig) : ApiService {
    private val httpClient = HttpClientProvider.httpClient

    override suspend fun fetchPage(page: Int): List<Car> {
        return try {
            val url = appConfig.getCarsFromPageUrl
            val response: HttpResponse = httpClient
                .post(url) {
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

    override suspend fun getPageCount(): Int {
        return try {
            println("Fetching page count...")

            val url = appConfig.getNumberOfPagesUrl

            val pageCountResponse = httpClient
                .get(url)
                .body<Int>()
            println("Page count loaded: $pageCountResponse")

            pageCountResponse
        } catch (e: Exception) {
            println("Error fetching page count: ${e.message}")
            0
        }
    }

    override suspend fun authenticate(authCode: String): AuthResponse {
        val response: HttpResponse = httpClient.post(appConfig.authWithServerUrl) {
            contentType(ContentType.Application.Json)
            setBody(mapOf("Code" to authCode, "RedirectUri" to appConfig.redirectUri))
        }
        if (response.status.isSuccess()) {
            println("Authenticated user successfully!")
            return response.body()
        }
        else {
            println("Error authenticating user: ${response.status.value}")
            return AuthResponse()
        }
    }

    override suspend fun sendMissingData(user: User): AfterRegisterResponse {
        val token = localStorage.getItem("auth_token")

        val response: HttpResponse = httpClient.post(appConfig.sendMissingDataUrl) {
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "login" to user.login,
                "Email" to user.email,
                "firstname" to user.firstname,
                "lastname" to user.lastname,
                "birthday" to user.birthday,
                "driverLicenseReceiveDate" to user.driverLicenseReceiveDate,
            ))
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        return response.body()
    }

     override suspend fun validateToken(): HttpResponse {
         val token = localStorage.getItem("auth_token") // Retrieve token from localStorage

         println("Token: $token")

         return try {
             if (token.isNullOrBlank()) {
                 throw IllegalStateException("No token found. Please log in first.")
             }

             val response: HttpResponse = httpClient.get(appConfig.checkTokenUrl) {
                 headers {
                     append(HttpHeaders.Authorization, "Bearer $token")
                 }
             }

             response
             // Result.success(response)
         } catch (e: Exception) {
             println("Error validating token: ${e.message}")
             throw e
         }

     }

    override suspend fun getDistinctBrands(): List<String> {
        return try {
            val response = httpClient.get(appConfig.distinctBrandsUrl)
            println("Raw Response: ${response.bodyAsText()}")
            response.body()
        } catch (e: Exception) {
            println("Error fetching distinct brands: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getDistinctYears(): List<Int> {
        return try {
            httpClient.get(appConfig.distinctYearsUrl).body()
        } catch (e: Exception) {
            println("Error fetching distinct years: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getDistinctTypes(): List<String> {
        return try {
            httpClient.get(appConfig.distinctTypesUrl).body()
        } catch (e: Exception) {
            println("Error fetching distinct types: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getDistinctLocations(): List<String> {
        return try {
            httpClient.get(appConfig.distinctLocationsUrl).body()
        } catch (e: Exception) {
            println("Error fetching distinct locations: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getFilteredCars(
        producer: String?,
        model: String?,
        yearOfProduction: String?,
        type: String?,
        location: String?
    ): List<Car> {
        return try {
            val params = mapOf(
                "producer" to producer,
                "model" to model,
                "yearOfProduction" to yearOfProduction,
                "type" to type,
                "location" to location
            ).filterValues { it != null }

            httpClient.get(appConfig.getFilteredCarsUrl) {
                params.forEach { (key, value) ->
                    parameter(key, value)
                }
            }.body()
        } catch (e: Exception) {
            println("Error fetching filtered cars: ${e.message}")
            emptyList()
        }
    }

    override suspend fun getOffer(offerRequest: OfferRequest): Offer {
        val token = localStorage.getItem("auth_token")

        println("[API SERVICE] Sending offer request: $offerRequest")
        println("[API SERVICE] Token: $token")
        println(mapOf(
            "CarId" to offerRequest.CarId.toString(),
            "CustomerId" to offerRequest.CustomerId.toString(),
            "PlannedStartDate" to offerRequest.PlannedStartDate,
            "PlannedEndDate" to offerRequest.PlannedEndDate,
        ))
        val response: HttpResponse = httpClient.post(appConfig.getOfferUrl) {
            contentType(ContentType.Application.Json)
            setBody(mapOf(
                "CarId" to offerRequest.CarId.toString(),
                "CustomerId" to offerRequest.CustomerId.toString(),
                "PlannedStartDate" to offerRequest.PlannedStartDate,
                "PlannedEndDate" to offerRequest.PlannedEndDate,
            ))
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        println("[API SERVICE] Response: $response]")

        return response.body()
    }

    override suspend fun getUserDetails(id: Int): User {
        val token = localStorage.getItem("auth_token")

        val response: HttpResponse = httpClient.get(appConfig.userInfoByIdUrl + "$id") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }

        println("[API SERVICE] Response: ${response.bodyAsText()}")

        return response.body()
    }


}