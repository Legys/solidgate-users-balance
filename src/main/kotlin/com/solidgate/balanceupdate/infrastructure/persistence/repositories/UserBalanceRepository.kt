package com.solidgate.balanceupdate.infrastructure.persistence.repositories

import com.solidgate.balanceupdate.domain.request.UserBalanceRequest
import com.solidgate.balanceupdate.infrastructure.persistence.entities.UserBalances
import org.jetbrains.exposed.sql.batchUpsert
import org.jetbrains.exposed.sql.transactions.experimental.newSuspendedTransaction
import org.springframework.stereotype.Repository

interface IUserBalanceRepository {
    suspend fun updateAll(chunk: List<UserBalanceRequest>)
}

@Repository
class UserBalanceRepository : IUserBalanceRepository {
    override suspend fun updateAll(chunk: List<UserBalanceRequest>) {
        newSuspendedTransaction {
            UserBalances.batchUpsert(chunk, shouldReturnGeneratedValues = false) { balance ->
                this[UserBalances.balance] = balance.balance
            }
        }
    }
}
