package org.dotnet.app.domain.authentication

import kotlinx.serialization.Serializable
import org.dotnet.app.domain.user.UserDTO

@Serializable
data class AuthResponse(
    var token: String? = null,
    var user: UserDTO? = null,
    val isNewUser: Boolean? = null,
)
