package com.solidgate.balanceupdate.infrastructure.utils

import org.slf4j.LoggerFactory
import java.lang.management.ManagementFactory

object MemoryLogger {
    private val logger = LoggerFactory.getLogger(MemoryLogger::class.java)

    fun logMemoryUsage(message: String) {
        val memoryMXBean = ManagementFactory.getMemoryMXBean()
        val heapMemoryUsage = memoryMXBean.heapMemoryUsage
        val nonHeapMemoryUsage = memoryMXBean.nonHeapMemoryUsage

        val heapUsed = heapMemoryUsage.used
        val heapCommitted = heapMemoryUsage.committed
        val nonHeapUsed = nonHeapMemoryUsage.used
        val nonHeapCommitted = nonHeapMemoryUsage.committed

        logger.info(
            "$message - Memory Usage: Heap: used=${bytesToMegabytes(
                heapUsed,
            )} MB, committed=${bytesToMegabytes(
                heapCommitted,
            )} MB; Non-Heap: used=${bytesToMegabytes(nonHeapUsed)} MB, committed=${bytesToMegabytes(nonHeapCommitted)} MB",
        )
    }

    private fun bytesToMegabytes(bytes: Long): Long = bytes / (1024 * 1024)
}
