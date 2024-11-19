plugins {
    kotlin("jvm") version "1.9.22"
}

group = "com.microtik"
version = "0.0.1"

repositories {
    mavenCentral()
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "com.microtik.MainKt"
    }
    configurations["compileClasspath"].forEach { file: File ->
        from(zipTree(file.absoluteFile))
    }
    duplicatesStrategy = DuplicatesStrategy.INCLUDE
}

dependencies {
    testImplementation("org.jetbrains.kotlin:kotlin-test")
    implementation("commons-cli:commons-cli:1.9.0")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}