import net.darkhax.curseforgegradle.TaskPublishCurseForge
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  val kotlinVersion: String by System.getProperties()
  kotlin("jvm").version(kotlinVersion)

  id("fabric-loom") version "1.1-SNAPSHOT"
  id("io.github.juuxel.loom-quiltflower") version "1.8.0"
  id("maven-publish")
  id("signing")
  id("com.modrinth.minotaur") version "2.+"
  id("net.darkhax.curseforgegradle") version "1.0.11"
}

val modVersion: String by project
val mavenGroup: String by project

val minecraftVersion: String by project
val minecraftTargetVersion: String by project
val yarnMappings: String by project
val loaderVersion: String by project
val fabricKotlinVersion: String by project
val fabricVersion: String by project

val ccVersion: String by project
val ccMcVersion: String by project
val ccTargetVersion: String by project

val configurateVersion: String by project
val clothConfigVersion: String by project
val clothApiVersion: String by project
val modMenuVersion: String by project

val trinketsVersion: String by project
val cardinalComponentsVersion: String by project

val scLibraryVersion: String by project

// ===========================
// Third party mod integration
// ===========================
val scPeripheralsVersion: String by project

val archivesBaseName = "plethora"
version = modVersion
group = mavenGroup

tasks.withType<KotlinCompile>().configureEach {
  kotlinOptions {
    jvmTarget = "17"
    apiVersion = "1.7"
    languageVersion = "1.7"
  }
}

repositories {
  mavenLocal {
    content {
      includeModule("io.sc3", "sc-library")
      includeModule("io.sc3", "sc-peripherals")
    }
  }

  maven {
    url = uri("https://repo.lem.sh/releases")
    content {
      includeGroup("io.sc3")
    }
  }

  maven("https://squiddev.cc/maven") {
    content {
      includeGroup("cc.tweaked")
      includeModule("org.squiddev", "Cobalt")
    }
  }
  maven("https://maven.shedaniel.me") // cloth-config
  maven("https://maven.terraformersmc.com/releases") // Mod Menu
  maven("https://maven.terraformersmc.com/") // Trinkets
  maven("https://ladysnake.jfrog.io/artifactory/mods") // Trinkets
}

dependencies {
  minecraft("com.mojang", "minecraft", minecraftVersion)
  mappings("net.fabricmc", "yarn", yarnMappings, null, "v2")
  modImplementation("net.fabricmc", "fabric-loader", loaderVersion)
  modImplementation("net.fabricmc.fabric-api", "fabric-api", fabricVersion) {
    exclude("net.fabricmc.fabric-api", "fabric-gametest-api-v1")
  }
  modImplementation("net.fabricmc", "fabric-language-kotlin", fabricKotlinVersion)

  modImplementation(include("io.sc3", "sc-library", scLibraryVersion))

  // CC: Restitched
  modApi("cc.tweaked:cc-tweaked-$ccMcVersion-fabric:$ccVersion") {
    exclude("net.fabricmc.fabric-api", "fabric-gametest-api-v1")
    exclude("fuzs.forgeconfigapiport", "forgeconfigapiport-fabric")
  }

  modImplementation("dev.emi:trinkets:${trinketsVersion}")

  // Fixes @Nonnull and @Nullable annotations
  compileOnly("com.google.code.findbugs:jsr305:3.0.2")

  implementation(include("org.spongepowered", "configurate-core", configurateVersion))
  implementation(include("org.spongepowered", "configurate-hocon", configurateVersion))
  implementation(include("io.leangen.geantyref", "geantyref", "1.3.13"))
  implementation(include("com.typesafe", "config", "1.4.2"))

  modApi("me.shedaniel.cloth:cloth-config-fabric:$clothConfigVersion") {
    exclude("net.fabricmc.fabric-api")
  }
  include("me.shedaniel.cloth", "cloth-config-fabric", clothConfigVersion)
  modImplementation(include("me.shedaniel.cloth.api", "cloth-utils-v1", clothApiVersion))

  modImplementation(include("com.terraformersmc", "modmenu", modMenuVersion))

  modImplementation(include("dev.onyxstudios.cardinal-components-api", "cardinal-components-base", cardinalComponentsVersion))
  modImplementation(include("dev.onyxstudios.cardinal-components-api", "cardinal-components-entity", cardinalComponentsVersion))

  // ===========================
  // Third party mod integration
  // ===========================
  // sc-peripherals
  modCompileOnly("io.sc3:sc-peripherals:${scPeripheralsVersion}")
}

tasks {
  processResources {
    inputs.property("version", project.version)

    filesMatching("fabric.mod.json") { expand(mutableMapOf(
      "version" to project.version,
      "minecraft_target_version" to minecraftTargetVersion,
      "fabric_kotlin_version" to fabricKotlinVersion,
      "loader_version" to loaderVersion,
      "cc_target_version" to ccTargetVersion,
    )) }
  }

  jar {
    from("LICENSE") {
      rename { "${it}_${archivesBaseName}" }
    }

    duplicatesStrategy = DuplicatesStrategy.INCLUDE

    exclude("META-INF/INDEX.LIST", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "module-info.class")
  }

  remapJar {
    exclude("META-INF/INDEX.LIST", "META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA", "module-info.class")
    destinationDirectory.set(file("${rootDir}/build/final"))
  }

  loom {
    accessWidenerPath.set(file("src/main/resources/plethora.accesswidener"))

    sourceSets {
      main {
        resources {
          srcDir("src/generated/resources")
          exclude("src/generated/resources/.cache")
        }
      }
    }

    runs {
      configureEach {
        property("fabric.debug.disableModShuffle") // Make sure Plethora loads after CC.
      }
      create("datagen") {
        client()
        name("Data Generation")
        vmArgs(
          "-Dfabric-api.datagen",
          "-Dfabric-api.datagen.output-dir=${file("src/generated/resources")}",
          "-Dfabric-api.datagen.modid=${archivesBaseName}"
        )
        runDir("build/datagen")
      }
    }
  }
}

modrinth {
  token.set(findProperty("modrinthApiKey") as String? ?: "")
  projectId.set("LDfFdCXe")
  versionNumber.set("$minecraftVersion-$modVersion")
  versionName.set(modVersion)
  versionType.set("release")
  uploadFile.set(tasks.remapJar as Any)
  changelog.set("Release notes can be found on the [GitHub repository](https://github.com/SwitchCraftCC/Plethora-Fabric/commits/$minecraftVersion).")
  gameVersions.add(minecraftVersion)
  loaders.add("fabric")

  syncBodyFrom.set(provider { file("README.md").readText() })

  dependencies {
    required.project("fabric-api")
    required.project("fabric-language-kotlin")
    required.project("cc-tweaked")
    required.project("trinkets")
  }
}

tasks.modrinth { dependsOn(tasks.modrinthSyncBody) }
tasks.publish { dependsOn(tasks.modrinth) }

val publishCurseForge by tasks.registering(TaskPublishCurseForge::class) {
  group = PublishingPlugin.PUBLISH_TASK_GROUP
  description = "Upload artifacts to CurseForge"

  apiToken = findProperty("curseForgeApiKey") as String? ?: ""
  enabled = apiToken != ""

  val mainFile = upload("248425", tasks.remapJar.get().archiveFile)
  dependsOn(tasks.remapJar)
  mainFile.releaseType = "release"
  mainFile.changelog = "Release notes can be found on the [GitHub repository](https://github.com/SwitchCraftCC/Plethora-Fabric/commits/$minecraftVersion)."
  mainFile.changelogType = "markdown"
  mainFile.addGameVersion(minecraftVersion)
  mainFile.addRequirement("fabric-api")
  mainFile.addRequirement("fabric-language-kotlin")
  mainFile.addRequirement("cc-tweaked")
  mainFile.addRequirement("trinkets")
}

tasks.publish { dependsOn(publishCurseForge) }

publishing {
  publications {
    register("mavenJava", MavenPublication::class) {
      from(components["java"])
    }
  }

  repositories {
    maven {
      name = "lemmmyRepo"
      url = uri("https://repo.lem.sh/releases")

      if (!System.getenv("MAVEN_USERNAME").isNullOrEmpty()) {
        credentials {
          username = System.getenv("MAVEN_USERNAME")
          password = System.getenv("MAVEN_PASSWORD")
        }
      } else {
        credentials(PasswordCredentials::class)
      }

      authentication {
        create<BasicAuthentication>("basic")
      }
    }
  }
}

