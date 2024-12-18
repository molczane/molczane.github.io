package org.dotnet.app.model

import kotlinx.serialization.Serializable

@Serializable
data class AuthResponse(
    var Token: String,
    var User: UserDTO,
    val IsNewUser: Boolean
)
