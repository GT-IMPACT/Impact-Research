package space.impact.impactresearch.network

import com.google.common.io.ByteArrayDataInput
import com.google.common.io.ByteArrayDataOutput
import com.google.common.io.ByteStreams
import io.netty.buffer.ByteBufOutputStream
import net.minecraft.client.Minecraft
import net.minecraft.world.IBlockAccess
import space.impact.impactresearch.config.Config
import space.impact.impactresearch.draftlogic.database.DraftPlayerProgress
import space.impact.impactresearch.draftlogic.database.DraftProgress
import space.impact.impactresearch.draftlogic.database.DraftProgressList
import space.impact.impactresearch.draftlogic.ingame.DraftManager
import space.impact.packet_network.network.packets.ImpactPacket

class SyncClientDraftProgress(
    packetId: Int,
    private val write: ByteArrayDataOutput? = null,
    private val read: ByteArrayDataInput? = null,
) : ImpactPacket(packetId) {

    fun transaction(progress: DraftPlayerProgress): SyncClientDraftProgress {
        val write = ByteStreams.newDataOutput()
        transactionLogic(progress, write)
        println(write.toByteArray().size)
        return SyncClientDraftProgress(packetId = getPacketId(), write = write)
    }

    override fun decode(input: ByteArrayDataInput): SyncClientDraftProgress {
        return SyncClientDraftProgress(getPacketId(), read = input)
    }

    override fun encode(output: ByteBufOutputStream) {
        write?.toByteArray()?.also { output.write(it) }
    }

    private fun transactionLogic(progress: DraftPlayerProgress, write: ByteArrayDataOutput) {
        write.writeInt(progress.line.data.size) //size list
        for (draft in progress.line.data) {
            write.writeShort(draft.id)
        }
    }

    override fun processClient(mc: Minecraft, world: IBlockAccess) {
        read?.also { data ->
            val listDrafts = mutableListOf<DraftProgress>()
            repeat(data.readInt()) {
                listDrafts += DraftProgress(data.readShort().toInt())
            }
            val progress = DraftPlayerProgress(
                line = DraftProgressList(data = listDrafts)
            )
            val playerName = if (Config.usePlayerNameOrUUID) mc.thePlayer.gameProfile.id.toString() else mc.thePlayer.gameProfile.name
            DraftManager.DRAFT_PROGRESS[playerName] = progress
        }
    }
}
