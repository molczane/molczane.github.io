package org.dotnet.app.data.api

import org.dotnet.app.model.Car

interface ApiService {
    suspend fun fetchPage(page: Int): List<Car>
    suspend fun getPageCount(): Int
}