import java.net.URI

plugins {
    kotlin("jvm") version "1.9.22"
}

val miskaEnvIsDev: String by project

group = "com.miska"
version = "0.0.1-${if (miskaEnvIsDev.toBoolean()) "debug" else ""}"

repositories {
    mavenCentral()
    maven { url = URI("https://jitpack.io") }

}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.miska.MainKt"
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    doFirst {
        System.setProperty("APP_ENV_DEV", miskaEnvIsDev)
    }
}

dependencies {
    implementation("io.github.kotlin-telegram-bot.kotlin-telegram-bot:telegram:6.0.0")

    /// ниже не трогать
    implementation("io.ktor:ktor-server-core:2.3.7")
    implementation("io.ktor:ktor-server-netty:2.3.7")
    implementation("io.ktor:ktor-server-content-negotiation:2.3.7")
    implementation("io.ktor:ktor-serialization-gson:2.3.7")
    implementation("ch.qos.logback:logback-classic:1.4.5")
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("commons-cli:commons-cli:1.9.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation(kotlin("reflect"))
    testImplementation("org.jetbrains.kotlin:kotlin-test")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}