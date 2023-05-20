val modName: String by project
val modId: String by project
val minecraftVersion: String by project
val fabricLoaderVersion: String by project
val fabricVersion: String by project

plugins {
    idea
    `maven-publish`
    id("fabric-loom") version("1.1-SNAPSHOT")
}

val archivesBaseName: String = "${modName}-fabric-${minecraftVersion}"

dependencies {
    minecraft("com.mojang:minecraft:${minecraftVersion}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${fabricLoaderVersion}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabricVersion}")
    implementation(group = "com.google.code.findbugs", name = "jsr305", version = "3.0.1")
    implementation(project(":Xplat"))
}

loom {
    runs {
        named("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("run")
        }
        named("server") {
            server()
            configName = "Fabric Server"
            ideConfigGenerated(true)
            runDir("run")
        }
        create("data") {
            inherit(runs["client"])
            configName = "Fabric Data"
            vmArgs(
                "-Dfabric-api-datagen",
                "-Dfabric-api.datagen.output-dir=${file("src/generated/resources")}",
                "-Dfabric-api.datagen.modid=${modId}"
            )

            runDir("build/datagen")
        }
    }
}

sourceSets["main"].resources.srcDir("src/generated/resources")

tasks {
    processResources {
        from(project(":Xplat").sourceSets["main"].resources)
        inputs.property("version", version)

        filesMatching("fabric.mod.json") {
            expand(mapOf("version" to version))
        }
    }

    withType(JavaCompile::class) {
        source(project(":Xplat").sourceSets["main"].allSource)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = group.toString()
            artifactId = archivesBaseName
            version = project.version.toString()
            from(components["java"])
        }
    }

    repositories {
        maven("file://${System.getenv("local_maven")}")
    }
}
