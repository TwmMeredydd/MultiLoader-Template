val modName: String by project
val modId: String by project
val modVersion: String by project
val minecraftVersion: String by project
val fabricLoaderVersion: String by project
val fabricVersion: String by project

plugins {
    idea
    `maven-publish`
    id("fabric-loom") version("1.1-SNAPSHOT")
}

val version: String = "${minecraftVersion}-${modVersion}"

base {
    archivesName.set("$modId-fabric-$version")
}

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
            name("Fabric Client")
        }
        named("server") {
            name("Fabric Server")
        }

        create("data") {
            inherit(runs["client"])
            name("Fabric Datagen")
            vmArgs(
                "-Dfabric-api-datagen",
                "-Dfabric-api.datagen.output-dir=${file("src/generated/resources")}",
                "-Dfabric-api.datagen.modid=${modId}"
            )
        }

        configureEach {
            ideConfigGenerated(true)
            runDir("run")
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
            artifactId = "${modId}_fabric"
            version = project.version.toString()
            from(components["java"])
        }
    }

    repositories {
        maven("file://${System.getenv("local_maven")}")
    }
}
