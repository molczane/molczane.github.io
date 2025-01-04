package org.dotnet.app.data.api

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import org.dotnet.app.domain.AppConfig
import org.dotnet.app.domain.AuthResponse
import org.dotnet.app.domain.Car

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
        val response: HttpResponse = httpClient.post(appConfig.googleAuthUrl) {
            contentType(ContentType.Application.Json)
            setBody(mapOf("Code" to authCode, "RedirectUri" to appConfig.redirectUri))
        }
        return response.body()
    }
}