package org.dotnet.app.data.api

import io.ktor.client.statement.*
import org.dotnet.app.domain.authentication.AfterRegisterResponse
import org.dotnet.app.domain.authentication.AuthResponse
import org.dotnet.app.domain.cars.Car
import org.dotnet.app.domain.offer.Offer
import org.dotnet.app.domain.offer.OfferRequest
import org.dotnet.app.domain.user.User

interface ApiService {
    suspend fun fetchPage(page: Int): List<Car>
    suspend fun getPageCount(): Int
    suspend fun authenticate(authCode: String): AuthResponse
    suspend fun sendMissingData(user: User): AfterRegisterResponse
    suspend fun validateToken(): HttpResponse
    suspend fun getDistinctBrands(): List<String>
    suspend fun getDistinctYears(): List<Int>
    suspend fun getDistinctTypes(): List<String>
    suspend fun getDistinctLocations(): List<String>
    suspend fun getFilteredCars(
        producer: String? = null,
        model: String? = null,
        yearOfProduction: String? = null,
        type: String? = null,
        location: String? = null
    ): List<Car>
    suspend fun getOffer(offerRequest: OfferRequest): Offer
    suspend fun getUserDetails(id: Int): User
}