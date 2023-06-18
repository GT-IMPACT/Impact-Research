package space.impact.impactresearch.network

import space.impact.packet_network.network.registerPacket

object NetworkPackets {
    val SyncClientDraftProgress = SyncClientDraftProgress(2000)
}

fun registerPackets() {
    registerPacket(NetworkPackets.SyncClientDraftProgress)
}