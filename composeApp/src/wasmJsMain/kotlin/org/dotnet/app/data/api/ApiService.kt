package org.dotnet.app.data.api

import org.dotnet.app.domain.AuthResponse
import org.dotnet.app.domain.Car

interface ApiService {
    suspend fun fetchPage(page: Int): List<Car>
    suspend fun getPageCount(): Int
    suspend fun authenticate(authCode: String): AuthResponse

}