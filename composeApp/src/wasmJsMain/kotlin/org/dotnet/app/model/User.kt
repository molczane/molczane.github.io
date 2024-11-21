package org.dotnet.app.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val id: Int,
    val first_Name: String,
    val email: String,
    val last_Name: String,
    val login: String,
    val password: String
)
