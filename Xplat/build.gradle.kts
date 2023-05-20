val modName: String by project
val modId: String by project
val minecraftVersion: String by project
val modVersion: String by project

plugins {
    java
    `maven-publish`
    id("org.spongepowered.gradle.vanilla") version("0.2.1-SNAPSHOT")
}

val version: String = "${minecraftVersion}-${modVersion}"

base {
    archivesName.set("${rootProject.name}-xplat-$version")
}

minecraft {
    version(minecraftVersion)
    runs {
        client("Xplat Client") {
            workingDirectory(file("run"))
        }
        server("Xplat Server") {
            workingDirectory(file("run"))
        }
    }
}

dependencies {
    compileOnly(group = "org.spongepowered", name = "mixin", version = "0.8.5")
    implementation(group = "com.google.code.findbugs", name = "jsr305", version = "3.0.1")
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = project.group.toString()
            artifactId = "${modId}_xplat"
            version = project.version.toString()
            from(components["java"])
        }
    }

    repositories {
        maven("file://${System.getenv("local_maven")}")
    }
}
