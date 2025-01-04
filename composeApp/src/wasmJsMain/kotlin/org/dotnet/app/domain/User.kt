package org.dotnet.app.domain

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val firstname: String,
    val email: String,
    val lastname: String,
    val login: String,
    val password: String,
    val rentalService: String?,
    val birthday: String?,
    val driverLicenseReceiveDate: String?
)
