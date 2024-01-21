plugins {
    id("antlr")
    kotlin("jvm") version "1.9.21"
    application
}

group = "ru.dfrolovd"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    antlr("org.antlr:antlr4:4.10.1")
    implementation("org.antlr:antlr4-runtime:4.10.1")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.1.0")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}


application {
    mainClass.set("MainKt")
}

tasks.compileKotlin {
    dependsOn("generateGrammarSource")
}
