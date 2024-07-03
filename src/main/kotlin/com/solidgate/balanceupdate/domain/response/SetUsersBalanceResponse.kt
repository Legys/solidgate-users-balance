package com.solidgate.balanceupdate.domain.response

data class SetUsersBalanceResponse(
    val message: String,
    val errors: List<UserBalanceErrorResponse>,
)
