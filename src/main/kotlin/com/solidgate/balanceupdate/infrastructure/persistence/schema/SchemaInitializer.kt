package com.solidgate.balanceupdate.infrastructure.persistence.schema

import com.solidgate.balanceupdate.infrastructure.persistence.entities.UserBalances
import org.jetbrains.exposed.sql.SchemaUtils
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
@Transactional
class SchemaInitializer : ApplicationRunner {
    override fun run(args: ApplicationArguments?) {
        SchemaUtils.create(UserBalances)
    }
}
