package org.dotnet.app.domain

data class RegisterCredential(
    val firstname: String,
    val email: String,
    val lastname: String,
    val password: String,
    val rentalServices: String,
    val birthday: String,
    val driverLicenseReveiveDate: String
)
