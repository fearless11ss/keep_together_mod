plugins {
    application
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
}

application {
    mainClass = "dev.keeptogether.relay.RelayMain"
}

tasks.register<JavaExec>("issueM1Tickets") {
    group = "application"
    description = "Issues one short-lived owner/guest ticket pair for the M1 tunnel spike."
    classpath = sourceSets.main.get().runtimeClasspath
    mainClass = "dev.keeptogether.relay.RelayTicketTool"
}

dependencies {
    implementation(project(":protocol"))
    implementation(platform(libs.netty.bom))
    implementation(libs.netty.codec)
    implementation(libs.netty.common)
    implementation(libs.netty.handler)
    implementation(libs.netty.transport)

    testImplementation(platform(libs.junit.bom))
    testImplementation(project(":tunnel"))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.assertj.core)
    testRuntimeOnly(libs.junit.platform.launcher)
}

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8"
    options.release = 25
    options.compilerArgs.add("-Xlint:deprecation")
}
