package org.dotnet.app.domain.authentication

import kotlinx.serialization.Serializable
import org.dotnet.app.domain.user.User

@Serializable
data class AfterRegisterResponse(
    val user: User,
    val isProfileComplete: Boolean
)