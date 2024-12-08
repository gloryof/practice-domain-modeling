plugins {
    alias(libs.plugins.kotlin.jvm)
    application
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.michael-bull.kotlin-result:kotlin-result:2.0.0")

    testImplementation(libs.junit.jupiter)
    testImplementation("io.mockk:mockk:1.13.13")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

application {
    mainClass = "jp.glory.AppKt"
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}
