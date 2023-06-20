package space.impact.impactresearch.events

import cpw.mods.fml.common.FMLCommonHandler
import cpw.mods.fml.common.eventhandler.SubscribeEvent
import cpw.mods.fml.common.gameevent.PlayerEvent
import net.minecraftforge.common.MinecraftForge
import space.impact.impactresearch.config.Config
import space.impact.impactresearch.draftlogic.database.DraftPlayerProgress
import space.impact.impactresearch.draftlogic.ingame.DraftManager
import space.impact.impactresearch.network.DraftNetworkPackets
import space.impact.packet_network.network.NetworkHandler.sendToPlayer

class PlayerJoinEvent {

    init {
        FMLCommonHandler.instance().bus().register(this)
        MinecraftForge.EVENT_BUS.register(this)
    }

    @SubscribeEvent
    fun playerJoin(e: PlayerEvent.PlayerLoggedInEvent) {
        e.player?.also {
            val keyPlayer = if (Config.usePlayerNameOrUUID) e.player.gameProfile.id.toString() else e.player.gameProfile.name
            val progress = DraftManager.DRAFT_PROGRESS[keyPlayer] ?: DraftPlayerProgress()
            it.sendToPlayer(DraftNetworkPackets.SyncClientDraftProgress.transaction(progress))
        }
    }
}
