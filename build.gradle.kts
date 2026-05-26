plugins {
    java
    application
}

group = "org.example"
version = "1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.moandjiezana.toml:toml4j:0.7.2")
    implementation("org.json:json:20231013")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("net.dv8tion:JDA:5.0.0-beta.24")
}

application {
    mainClass.set("bot.discordBot.Main") // Définit la classe à lancer
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "bot.discordBot.Main"
    }
    from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
    exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
