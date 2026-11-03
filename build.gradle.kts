plugins {
    id("org.springframework.boot") version "4.1.2" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
    id("com.google.protobuf") version "0.10.0" apply false
    id("org.cyclonedx.bom") version "3.5.1" apply false
}

allprojects {
    group = "dev.keeptogether"
    version = "0.0.1-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "org.cyclonedx.bom")

    tasks.withType<Test>().configureEach {
        useJUnitPlatform()
    }
}
