package org.dotnet.app.data.repository

import org.dotnet.app.data.api.ApiService
import org.dotnet.app.domain.cars.Car
import org.dotnet.app.domain.cars.CarFilters

class CarRepository(private val apiService: ApiService) {
    suspend fun getCarsPage(page: Int) : List<Car> = apiService.fetchPage(page)
    suspend fun getPageCount() : Int = apiService.getPageCount()
    suspend fun getCarsFiltered(carFilters: CarFilters) : List<Car> = apiService.getFilteredCars(
        producer = carFilters.producer,
        model = carFilters.model,
        yearOfProduction = carFilters.yearOfProduction,
        type = carFilters.type,
        location = carFilters.location
    )
}
