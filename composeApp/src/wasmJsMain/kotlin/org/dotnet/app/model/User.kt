package org.dotnet.app.model

import kotlinx.serialization.Serializable
import org.w3c.dom.DOMStringMap

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
