package org.dotnet.app.domain

data class CarFilters(
    val producer: String? = null,
    val model: String? = null,
    val yearOfProduction: String? = null,
    val type: String? = null,
    val location: String? = null
)
