val modName: String by project
val minecraftVersion: String by project

plugins {
    java
    `maven-publish`
    id("org.spongepowered.gradle.vanilla") version("0.2.1-SNAPSHOT")
}

val archivesBaseName: String = "${modName}-xplat-${minecraftVersion}"

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

tasks.processResources {
    val buildProps = project.properties.toMap()

    filesMatching("pack.mcmeta") {
        expand(buildProps)
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            groupId = group.toString()
            artifactId = archivesBaseName
            version = this.version
            from(components["java"])
        }
    }

    repositories {
        maven("file://${System.getenv("local_maven")}")
    }
}
