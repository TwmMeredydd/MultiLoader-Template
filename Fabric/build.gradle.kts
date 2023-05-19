val mod_name: String by project
val minecraft_version: String by project
val fabric_loader_version: String by project
val fabric_version: String by project

plugins {
    idea
    `maven-publish`
    id("fabric-loom") version("1.0-SNAPSHOT")
}

val archivesBaseName: String = "${mod_name}-fabric-${minecraft_version}"

dependencies {
    minecraft("com.mojang:minecraft:${minecraft_version}")
    mappings(loom.officialMojangMappings())
    modImplementation("net.fabricmc:fabric-loader:${fabric_loader_version}")
    modImplementation("net.fabricmc.fabric-api:fabric-api:${fabric_version}")
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
    }
}

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
