val mod_name: String by project
val mod_id: String by project
val minecraft_version: String by project
val forge_version: String by project

plugins {
    java
    eclipse
    `maven-publish`
    id("net.minecraftforge.gradle") version("5.1.+")
}

val archivesBaseName: String = "${mod_name}-forge-${minecraft_version}"

minecraft {
    mappings(mapOf(
        "channel" to "official",
        "version" to minecraft_version
    ))

    runs {
        create("client") {
            workingDirectory(file("run"))
            ideaModule("${rootProject.name}.${project.name}.main")
            taskName("Client")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "$projectDir/build/createSrgToMcp/output.srg")
            mods {
                create(mod_id) {
                    source(sourceSets["main"])
                    source(project(":Xplat").sourceSets["main"])
                }
            }
        }

        create("server") {
            workingDirectory(file("run"))
            ideaModule("${rootProject.name}.${project.name}.main")
            taskName("Server")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "$projectDir/build/createSrgToMcp/output.srg")
            mods {
                create(mod_id) {
                    source(sourceSets["main"])
                    source(project(":Xplat").sourceSets["main"])
                }
            }
        }

        create("data") {
            workingDirectory(file("run"))
            ideaModule("${rootProject.name}.${project.name}.main")
            args("--mod", mod_id, "--all", "--output", file("src/generated/resources"), "--existing", file("src/main/resources"))
            taskName("Data")
            property("mixin.env.remapRefMap", "true")
            property("mixin.env.refMapRemappingFile", "$projectDir/build/createSrgToMcp/output.srg")
            mods {
                create(mod_id) {
                    source(sourceSets["main"])
                    source(project(":Xplat").sourceSets["main"])
                }
            }
        }
    }
}

sourceSets["main"].resources.srcDir("src/generated/resources")

dependencies {
    minecraft("net.minecraftforge:forge:${minecraft_version}-${forge_version}")
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
