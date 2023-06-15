@file:Suppress("UnstableApiUsage")

import net.minecraftforge.gradle.common.BaseExtension
import org.gradle.internal.impldep.org.apache.commons.io.FileUtils.getFile

plugins {
    id("forge")
    kotlin("jvm")
}

configure<BaseExtension> {
    commonMinecraft(project)
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

val modName: String by extra
val modId: String by extra
val modGroup: String by extra

val useMixins: String by extra
val forceUseMixins: String by extra

val mixinPlugin: String by extra
val mixinsPackage: String by extra
val mixingConfigRefMap = "mixins.$modId.refmap.json"
val mixinTmpDir = buildDir.path + File.separator + "tmp" + File.separator + "mixins"
val refMap = mixinTmpDir + File.separator + mixingConfigRefMap

tasks.processResources.configure {
    inputs.property("version", project.version)
    inputs.property("mcversion", "1.7.10")
    filesMatching("mcmod.info") {
        expand(
            "minecraftVersion" to "1.7.10",
            "modId" to modId,
            "modVersion" to project.version,
            "modName" to modName,
        )
    }
    if (useMixins.toBoolean()) {
        from(refMap)
        dependsOn(tasks["compileJava"])
    }
}

tasks.create("generateAssets") {
    onlyIf { useMixins.toBoolean() }
    doLast {
        val mixinConfigFile = layout.projectDirectory.file("/src/main/resources/mixins.$modId.json").asFile
        if (!mixinConfigFile.exists()) {
            var mixinPluginLine = ""
            if (mixinPlugin.isNotEmpty()) {
                mixinPluginLine += "  \"plugin\": \"${modGroup}.${modId}.${mixinPlugin}\","
            }
            val jsonMixins = buildString {
                append("{\n")
                append("  \"required\": true,\n")
                append("  \"minVersion\": \"0.8.5-GTNH\",\n")
                append("  \"package\": \"${modGroup}.${modId}.${mixinsPackage}\",\n")
                if (mixinPluginLine.isNotEmpty()) append("$mixinPluginLine\n")
                append("  \"refmap\": \"$mixingConfigRefMap\",\n")
                append("  \"target\": \"@env(DEFAULT)\",\n")
                append("  \"compatibilityLevel\": \"JAVA_8\",\n")
                append("  \"mixins\": [],\n")
                append("  \"client\": [],\n")
                append("  \"server\": []\n")
                append("}")
            }
            mixinConfigFile.writeText(jsonMixins)
        }
    }
}

fun getManifestAttributes(): Map<String, Any> {
    val manifestAttributes = mutableMapOf<String, Any>()
    if (useMixins.toBoolean()) {
        manifestAttributes["FMLCorePluginContainsFMLMod"] = true
        manifestAttributes["TweakClass"] = "org.spongepowered.asm.launch.MixinTweaker"
        manifestAttributes["MixinConfigs"] = "mixins.$modId.json"
        manifestAttributes["ForceLoadAsMod"] = true
    }
    return manifestAttributes
}

tasks.register("devJar", Jar::class) {
    from(sourceSets["main"].output)
    archiveClassifier.set("dev")
    manifest { attributes(getManifestAttributes()) }
    dependsOn(tasks["generateAssets"])
}

tasks.named<Jar>("jar").configure {
    manifest { attributes(getManifestAttributes()) }
    dependsOn(tasks["devJar"])
}

dependencies {
    implementation(libs.forgelin)
    if (useMixins.toBoolean()) {
        annotationProcessor(libs.processor.asmdebug)
        annotationProcessor(libs.processor.guava)
        annotationProcessor(libs.processor.gson)
        val uniMixins = "io.github.legacymoddingmc:unimixins:0.1.7.1:dev"
        annotationProcessor(uniMixins)
        implementation(uniMixins)
    }
}

if (useMixins.toBoolean() || forceUseMixins.toBoolean()) {
    apply(from = "runConf.gradle")
}
