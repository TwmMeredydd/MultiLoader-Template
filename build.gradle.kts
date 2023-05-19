import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

val mod_name: String by project
val mod_author: String by project
val minecraft_version: String by project

subprojects {
    apply(plugin = "java")

    configure<JavaPluginExtension> {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(17))
        }
        withSourcesJar()
        withJavadocJar()
    }

    tasks.named<Jar>("jar") {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_${mod_name}" }
        }

        manifest {
            attributes(mapOf(
                "Specification-Title" to mod_name,
                "Specification-Vendor" to mod_author,
                "Specification-Version" to archiveVersion,
                "Implementation-Title" to name,
                "Implementation-Vendor" to mod_author,
                "Implementation-Timestamp" to ZonedDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssZ")),
                "Timestamp" to System.currentTimeMillis(),
                "Built-On-Java" to "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})",
                "Built-On-Minecraft" to minecraft_version
            ))
        }
    }

    tasks.named<Jar>("sourcesJar") {
        from(rootProject.file("LICENSE")) {
            rename { "${it}_${mod_name}" }
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
        options.release.set(17)
    }

    // Disables Gradle's custom module metadata from being published to maven. The
    // metadata includes mapped dependencies which are not reasonably consumable by
    // other mod developers.
    tasks.withType(GenerateModuleMetadata::class) {
        enabled = false
    }
}
