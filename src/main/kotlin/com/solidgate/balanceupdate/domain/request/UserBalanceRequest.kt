package com.solidgate.balanceupdate.domain.request

data class UserBalanceRequest(
    val userId: Int,
    val balance: Int,
)
