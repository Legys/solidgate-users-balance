package com.solidgate.balanceupdate

import org.jetbrains.exposed.spring.autoconfigure.ExposedAutoConfiguration
import org.springframework.boot.autoconfigure.ImportAutoConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
@ImportAutoConfiguration(ExposedAutoConfiguration::class)
class SolidgateBalanceUpdateApplication

fun main(args: Array<String>) {
    runApplication<SolidgateBalanceUpdateApplication>(*args)
}
