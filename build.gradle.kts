plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.jpa") version "1.9.25"
}

group = "cinnamein"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.25")
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.9.25")

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
    implementation("ch.qos.logback:logback-classic:1.5.20")
    implementation("ch.qos.logback:logback-core:1.5.20")

    // test
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

kotlin {
    jvmToolchain(21)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Jar> {
    manifest.attributes["Main-Class"] = "board.ApplicationKt"
    from(
        configurations.getByName("runtimeClasspath").map {
            if (it.isDirectory) it else zipTree(it)
        }
    )
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

tasks.register<JavaExec>("bootJar") {
    dependsOn("build")
    val jarTask = tasks.named<Jar>("jar")
    classpath = files(jarTask.flatMap { it.archiveFile })
    mainClass.set("board.ApplicationKt")
}
