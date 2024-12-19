package org.dotnet.app.model

data class AppConfig(
    val clientId: String,
    val redirectUri: String,
    val getNumberOfPagesUrl: String,
    val getCarsFromPageUrl: String,
    val googleAuthUrl: String,
    val authWithServerUrl: String
)
