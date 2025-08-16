plugins {
    kotlin("jvm") version "1.8.0"
}

group = "com.dizibox"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation("org.jsoup:jsoup:1.15.3") // HTML parsing için
}
