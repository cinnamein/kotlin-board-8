plugins {
    kotlin("jvm") version "2.2.0"
    kotlin("plugin.jpa") version "2.2.0"
    application
}

group = "cinnamein"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    // Jackson for JSON
    implementation("com.fasterxml.jackson.core:jackson-databind:2.16.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.16.1")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.16.1")

    // JPA & Hibernate
    implementation("org.hibernate:hibernate-core:6.4.1.Final")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")

    // HikariCP
    implementation("com.zaxxer:HikariCP:5.1.0")

    // H2 Database
    runtimeOnly("com.h2database:h2:2.2.224")

    // Logging
    implementation("ch.qos.logback:logback-classic:1.4.14")
    implementation("org.slf4j:slf4j-api:2.0.11")

    // test
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}
application {
    mainClass.set("board.ApplicationKt")
}
