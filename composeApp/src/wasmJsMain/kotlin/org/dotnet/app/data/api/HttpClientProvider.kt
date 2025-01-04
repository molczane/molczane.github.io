package org.dotnet.app.data.api

import io.ktor.client.*
import io.ktor.client.engine.js.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*

object HttpClientProvider {
    val httpClient: HttpClient by lazy {
        HttpClient(Js) {
            install(ContentNegotiation) {
                json()
            }
        }
    }
}