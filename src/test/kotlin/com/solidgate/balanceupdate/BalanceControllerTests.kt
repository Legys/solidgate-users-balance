package com.solidgate.balanceupdate

import io.restassured.module.mockmvc.RestAssuredMockMvc
import io.restassured.module.mockmvc.kotlin.extensions.Given
import io.restassured.module.mockmvc.kotlin.extensions.Then
import io.restassured.module.mockmvc.kotlin.extensions.When
import org.hamcrest.Matchers
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.testcontainers.containers.PostgreSQLContainer
import scripts.FileType
import java.io.File

@AutoConfigureMockMvc
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class BalanceControllerTests {
    companion object {
        val postgres = PostgreSQLContainer("postgres:16.3")

        @BeforeAll
        @JvmStatic
        fun startDBContainer() {
            postgres.start()
        }

        @AfterAll
        @JvmStatic
        fun stopDBContainer() {
            postgres.stop()
        }

        @DynamicPropertySource
        @JvmStatic
        fun registerDBContainer(registry: DynamicPropertyRegistry) {
            registry.add("spring.datasource.url", postgres::getJdbcUrl)
            registry.add("spring.datasource.username", postgres::getUsername)
            registry.add("spring.datasource.password", postgres::getPassword)
        }
    }


    @Autowired
    private lateinit var mockMvc: MockMvc

    @BeforeEach
    fun setup() {
        RestAssuredMockMvc.mockMvc(mockMvc)
    }

    @Test
    fun `dbContainer is running`() {
        Assertions.assertTrue(postgres.isRunning)
    }

    @Test
    fun `GET 'test' returns a message`() {
        Given {
            mockMvc(mockMvc)
        } When {
            get("/test")
        } Then {
            statusCode(200)
            body(Matchers.equalTo("The service is running"))
        }
    }

    @Test
    fun `POST 'set-users-balance' processes the file successfully`() {
        val file = File("artifacts/${FileType.SmallFile.fileName}")

        Given {
            mockMvc(mockMvc)
            multiPart(file)
        } When {
            async().post("/set-users-balance")
        } Then {
            statusCode(200)
            body(
                "message",
                Matchers.equalTo(
                    "File processed successfully",
                ),
                "errors",
                Matchers.empty<List<String>>(),
            )
        }
    }

    @Test
    fun `POST 'set-users-balance' processes the file with corrupted data`() {
        val file = File("artifacts/${FileType.CorruptedFile.fileName}")

        Given {
            mockMvc(mockMvc)
            multiPart(file)
        } When {
            async().post("/set-users-balance")
        } Then {
            statusCode(200)
            body(
                "message",
                Matchers.equalTo("File processed with errors"),
                "errors",
                Matchers.hasSize<Int>(2),
            )
        }
    }

    @Test
    fun `POST 'set-users-balance' processes the file with 1 million records`() {
        val file = File("artifacts/${FileType.LargeFile.fileName}")

        Given {
            mockMvc(mockMvc)
            multiPart(file)
        } When {
            async().post("/set-users-balance")
        } Then {
            statusCode(200)
            body(
                "message",
                Matchers.equalTo("File processed successfully"),
                "errors",
                Matchers.empty<List<String>>(),
            )
        }
    }
}
