val modName: String by project
val modId: String by project
val minecraftVersion: String by project
val forgeVersion: String by project

plugins {
    java
    eclipse
    `maven-publish`
    id("net.minecraftforge.gradle") version("5.1.+")
}

val archivesBaseName: String = "${modName}-forge-${minecraftVersion}"

minecraft {
    mappings(mapOf(
        "channel" to "official",
        "version" to minecraftVersion
    ))

    runs {
        create("client") {
            taskName("Client")
        }

        create("server") {
            taskName("Server")
        }

        create("data") {
            args("--mod", modId, "--all", "--output", file("src/generated/resources"), "--existing", file("src/main/resources"))
            taskName("Data")
        }

        configureEach {
            workingDirectory(file("run"))
            ideaModule("${rootProject.name}.${project.name}.main")
            property("forge.logging.markers", "REGISTRIES")
            property("forge.logging.console.level", "debug")
            mods {
                create(modId) {
                    source(sourceSets["main"])
                    source(project(":Xplat").sourceSets["main"])
                }
            }
        }
    }
}

sourceSets["main"].resources.srcDir("src/generated/resources")

dependencies {
    minecraft("net.minecraftforge:forge:${minecraftVersion}-${forgeVersion}")
    compileOnly(project(":Xplat"))
}

tasks {
    processResources {
        from(project(":Xplat").sourceSets["main"].resources)
    }

    withType(JavaCompile::class) {
        source(project(":Xplat").sourceSets["main"].allSource)
    }

    jar {
        finalizedBy("reobfJar")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = group.toString()
            artifactId = archivesBaseName
            version = project.version.toString()
            artifact(tasks.jar)
        }

        repositories {
            maven("file://${System.getenv("local_maven")}")
        }
    }
}
