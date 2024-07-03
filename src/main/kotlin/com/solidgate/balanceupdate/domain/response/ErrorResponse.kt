package com.solidgate.balanceupdate.domain.response

// Generic error response in case of non 200 status code
data class ErrorResponse(
    val errors: List<String>,
)
