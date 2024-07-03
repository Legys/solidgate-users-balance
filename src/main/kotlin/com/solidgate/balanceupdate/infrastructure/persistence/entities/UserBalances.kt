package com.solidgate.balanceupdate.infrastructure.persistence.entities

import org.jetbrains.exposed.sql.Table

object UserBalances : Table() {
    val userId = integer("user_id").uniqueIndex().autoIncrement()
    val balance = integer("balance")
    override val primaryKey = PrimaryKey(userId)
}
