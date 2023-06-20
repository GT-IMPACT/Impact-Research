package space.impact.impactresearch.network

import space.impact.impactresearch.common.tiles.CraftTableResearchTile
import space.impact.packet_network.network.packets.createPacketStream
import space.impact.packet_network.network.registerPacket

object DraftNetworkPackets {
    val SyncClientDraftProgress = SyncClientDraftProgress(2000)
    val SyncClientProgress = createPacketStream(2001) { isServer, data ->
        if (!isServer) {
            (tileEntity as? CraftTableResearchTile)?.updateClient(data.readInt())
        }
    }
}

fun registerPackets() {
    registerPacket(DraftNetworkPackets.SyncClientDraftProgress)
    registerPacket(DraftNetworkPackets.SyncClientProgress)
}
