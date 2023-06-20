package space.impact.impactresearch.config

import net.minecraftforge.common.config.Configuration
import net.minecraftforge.common.config.Property
import space.impact.impactresearch.ImpactResearch
import java.io.File
import kotlin.properties.Delegates

object Config {

    private const val GENERAL_CATEGORY = "GENERAL"

    private var loadConfig = false
    private var config: Configuration? = null

    internal fun init() {
        if (!loadConfig) {
            config = Configuration(File("config/IMPACT/draftresearch.cfg"))
            syncConfig()
        }
    }

    private fun syncConfig() {
        val general = ArrayList<String>()

        config?.also { config ->
            try {
                if (!config.isChild) config.load()

                configParams(config, general)
                config.setCategoryPropertyOrder("GENERAL", general)

                if (config.hasChanged()) config.save()
            } catch (e: Exception) {
                if (ImpactResearch.IS_DEBUG) e.printStackTrace()
            }
        }
    }

    var usePlayerNameOrUUID by Delegates.notNull<Boolean>()

    private fun configParams(config: Configuration, params: ArrayList<String>) {

        //GENERAL
        val cfg: Property = config.get(GENERAL_CATEGORY, "useUUIDOrPlayerName", true)
        cfg.comment = "UUID - true, PlayerName - false. [Default: true]"
        usePlayerNameOrUUID = cfg.getBoolean(true)
        params.add(cfg.name)

    }
}
