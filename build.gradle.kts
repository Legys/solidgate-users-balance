plugins {
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
    kotlin("jvm") version "1.9.24"
    kotlin("plugin.spring") version "1.9.24"
    kotlin("plugin.serialization") version "1.8.0"
    application
}

group = "com.solidgate"
version = "1.0.0"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

val exposedVersion: String by project
val coroutinesVersion: String by project
val testcontainersVersion: String by project
val restAssuredVersion: String by project

dependencies {
    // Spring
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")

    // Tests
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers:3.3.1")
    implementation("io.rest-assured:rest-assured:$restAssuredVersion")
    testImplementation("io.rest-assured:spring-mock-mvc:$restAssuredVersion")
    testImplementation("io.rest-assured:spring-mock-mvc-kotlin-extensions:$restAssuredVersion")
    testImplementation("io.rest-assured:kotlin-extensions:$restAssuredVersion")

    testImplementation("org.testcontainers:junit-jupiter:$testcontainersVersion")
    testImplementation("org.testcontainers:postgresql:$testcontainersVersion")

    // Kotlin
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:$coroutinesVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:$coroutinesVersion")

    // Database
    implementation("org.jetbrains.exposed:exposed-spring-boot-starter:$exposedVersion")
    implementation("org.postgresql:postgresql:42.7.3")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
        exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
        showExceptions = true
        showCauses = true
        showStackTraces = true
    }
}

tasks.register<JavaExec>("seedUsersBalanceFile") {
    group = "seed"
    description = "Creates a file with 1 million records to use in a request"
    classpath = sourceSets["main"].runtimeClasspath
    mainClass.set("scripts.SeedUsersBalanceFileKt")
}

application {
    mainClass.set("com.solidgate.balanceupdate.SolidgateBalanceUpdateApplicationKt")
}
