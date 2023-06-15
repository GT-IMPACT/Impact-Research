
plugins {
    alias(libs.plugins.buildconfig)
    groovy
    id("minecraft")
    id("publish")
}

repositories {
    maven("https://maven.accident.space/repository/maven-public/")
    maven("http://jenkins.usrv.eu:8081/nexus/content/groups/public/") { isAllowInsecureProtocol = true }
    maven("https://jitpack.io")
    maven("https://maven.ic2.player.to/") { metadataSources { mavenPom(); artifact() } }
    mavenCentral()
    mavenLocal()
}

val modId: String by extra
val modName: String by extra
val modGroup: String by extra

buildConfig {
    packageName("space.impact.$modId")
    buildConfigField("String", "MODID", "\"${modId}\"")
    buildConfigField("String", "MODNAME", "\"${modName}\"")
    buildConfigField("String", "VERSION", "\"${project.version}\"")
    buildConfigField("String", "GROUPNAME", "\"${modGroup}\"")
    useKotlinOutput { topLevelConstants = true }
}
