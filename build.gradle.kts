plugins {
    id("java")
}

group = "org.example"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.javacord:javacord:3.8.0") // Version fixe conseillée
    implementation("com.moandjiezana.toml:toml4j:0.7.2")
    implementation("org.json:json:20231013")
    implementation("com.google.code.gson:gson:2.10.1")

}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "bot.discordBot.Main"
    }

    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })

    // C'EST CETTE PARTIE QUI MANQUAIT :
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}