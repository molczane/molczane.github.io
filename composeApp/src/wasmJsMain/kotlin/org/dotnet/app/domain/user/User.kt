package org.dotnet.app.domain.user

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int? = null,
    val firstname: String? = null,
    val email: String? = null,
    val lastname: String? = null,
    val login: String? = null,
    val password: String? = null,
    val rentalService: String? = null,
    val birthday: String? = null,
    val driverLicenseReceiveDate: String? = null
)
