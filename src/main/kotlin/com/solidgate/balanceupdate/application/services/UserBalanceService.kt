package com.solidgate.balanceupdate.application.services

import com.solidgate.balanceupdate.domain.request.UserBalanceRequest
import com.solidgate.balanceupdate.domain.response.UserBalanceErrorResponse
import com.solidgate.balanceupdate.infrastructure.persistence.repositories.UserBalanceRepository
import com.solidgate.balanceupdate.infrastructure.utils.ExecutionLogger
import com.solidgate.balanceupdate.infrastructure.utils.MemoryLogger
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

interface IUserBalanceService {
    suspend fun processFile(file: MultipartFile): Result<List<UserBalanceErrorResponse>>
}

@Service
class UserBalanceService(
    val userBalanceRepository: UserBalanceRepository,
) : IUserBalanceService {
    @Transactional
    override suspend fun processFile(file: MultipartFile): Result<List<UserBalanceErrorResponse>> =
        runCatching {
            val collectedErrors = mutableListOf<UserBalanceErrorResponse>()

            MemoryLogger.logMemoryUsage("Before processing stream")
            file.inputStream.reader().useLines { lines ->
                lines
                    .chunked(10000)
                    .map { linesChunk ->
                        linesChunk.mapNotNull { line ->
                            val x =
                                parseLine(line).onFailure { e ->
                                    collectedErrors.add(UserBalanceErrorResponse(line, e.message.toString()))
                                }

                            x.getOrNull()
                        }
                    }.forEach { dataChunk ->
                        processChunk(dataChunk)
                    }
            }
            return Result.success(collectedErrors.toList())
        }

    private fun parseLine(line: String): Result<UserBalanceRequest> =
        runCatching {
            val (idStr, balanceStr) = line.split(":")
            val id = idStr.trim().toInt()
            val balance = balanceStr.trim().toInt()

            return Result.success(UserBalanceRequest(id, balance))
        }

    private suspend fun processChunk(chunk: List<UserBalanceRequest>) {
        MemoryLogger.logMemoryUsage("Processing chunk")
        ExecutionLogger.logExecutionTime("Persisting the chunk") {
            userBalanceRepository.updateAll(chunk)
        }
    }
}
