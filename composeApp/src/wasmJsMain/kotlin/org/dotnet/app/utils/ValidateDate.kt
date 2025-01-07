package org.dotnet.app.utils

fun validateDate(value: String, pattern: Regex): String? {
    return if (!pattern.matches(value)) {
        "Date must be in YYYY-MM-DD format"
    } else {
        null
    }
}