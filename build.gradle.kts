import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

val modName: String by project
val modAuthor: String by project
val minecraftVersion: String by project
val modJavaVersion: String by project

subprojects {
    apply(plugin = "java")

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(modJavaVersion))
        }
        withSourcesJar()
        withJavadocJar()
    }

    tasks.named<Jar>("jar") {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_${modName}" }
        }

        manifest {
            attributes(mapOf(
                "Specification-Title" to modName,
                "Specification-Vendor" to modAuthor,
                "Specification-Version" to archiveVersion,
                "Implementation-Title" to name,
                "Implementation-Vendor" to modAuthor,
                "Implementation-Timestamp" to ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")),
                "Timestamp" to System.currentTimeMillis(),
                "Built-On-Java" to "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})",
                "Built-On-Minecraft" to minecraftVersion
            ))
        }
    }

    tasks.named<Jar>("sourcesJar") {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_${modName}" }
        }
    }

    repositories {
        mavenCentral()

        maven("https://repo.spongepowered.org/repository/maven-public/") {
            name = "Sponge / Mixin"
        }
    }

    tasks.withType(JavaCompile::class).configureEach {
        options.encoding = "UTF-8"
        options.release.set(modJavaVersion.toInt())
    }

    // Disables Gradle's custom module metadata from being published to maven. The
    // metadata includes mapped dependencies which are not reasonably consumable by
    // other mod developers.
    tasks.withType(GenerateModuleMetadata::class) {
        enabled = false
    }
}
