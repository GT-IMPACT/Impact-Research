package space.impact.modid

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.event.FMLPostInitializationEvent

@Mod(
    modid = MODID,
    name = MODNAME,
    version = VERSION,
    acceptedMinecraftVersions = "[1.7.10]",
    modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter"
)
object Mod {
    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent?) {

    }
}
