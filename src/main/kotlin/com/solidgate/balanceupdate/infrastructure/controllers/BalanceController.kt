package com.solidgate.balanceupdate.infrastructure.controllers

import com.solidgate.balanceupdate.application.services.UserBalanceService
import com.solidgate.balanceupdate.domain.response.ErrorResponse
import com.solidgate.balanceupdate.domain.response.SetUsersBalanceResponse
import com.solidgate.balanceupdate.infrastructure.utils.MemoryLogger
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile

@RestController
class BalanceController(
    private val userBalanceService: UserBalanceService,
) {
    @GetMapping(
        "/test",
    )
    fun test(): String = "The service is running"

    @PostMapping(
        "/set-users-balance",
    )
    suspend fun setUsersBalance(
        @RequestParam("file") file: MultipartFile,
    ): ResponseEntity<*> {
        MemoryLogger.logMemoryUsage("Before processing file")
        val result =
            userBalanceService.processFile(file).map { possibleErrors ->
                when {
                    possibleErrors.isEmpty() -> {
                        SetUsersBalanceResponse("File processed successfully", emptyList())
                    }
                    else -> {
                        SetUsersBalanceResponse("File processed with errors", possibleErrors)
                    }
                }
            }
            MemoryLogger.logMemoryUsage("After processing file")

        return result.fold(
            onSuccess = {
                ResponseEntity
                    .ok()
                    .body(it)
            },
            onFailure = {
                ResponseEntity.badRequest().body(
                    ErrorResponse(
                        listOf(it.message ?: "Something has happened"),
                    ),
                )
            },
        )
    }
}
