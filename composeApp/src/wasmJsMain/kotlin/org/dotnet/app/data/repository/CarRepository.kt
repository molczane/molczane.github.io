package org.dotnet.app.data.repository

import org.dotnet.app.data.api.ApiService
import org.dotnet.app.domain.Car

class CarRepository(private val apiService: ApiService) {
    suspend fun getCarsPage(page: Int) : List<Car> = apiService.fetchPage(page)
    suspend fun getPageCount() : Int = apiService.getPageCount()
}
