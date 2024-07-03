package com.solidgate.balanceupdate.infrastructure.utils

import org.slf4j.LoggerFactory
import kotlin.system.measureTimeMillis

object ExecutionLogger {
    val logger: org.slf4j.Logger = LoggerFactory.getLogger(ExecutionLogger::class.java)

    inline fun <T> logExecutionTime(
        functionName: String,
        function: () -> T,
    ): T {
        var result: T
        val timeTaken =
            measureTimeMillis {
                result = function()
            }
        logger.info("Execution of $functionName took $timeTaken ms")
        return result
    }
}
