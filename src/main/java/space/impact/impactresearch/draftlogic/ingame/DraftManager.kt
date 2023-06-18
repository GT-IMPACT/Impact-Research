package space.impact.impactresearch.draftlogic.ingame

import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.nbt.NBTTagList
import net.minecraft.util.ChatComponentText
import net.minecraft.util.EnumChatFormatting
import space.impact.impactresearch.ImpactResearch
import space.impact.impactresearch.common.items.DraftItem
import space.impact.impactresearch.config.Config
import space.impact.impactresearch.draftlogic.database.*
import space.impact.impactresearch.draftlogic.interactor.DraftInteractor.saveSingleDraft
import space.impact.impactresearch.network.NetworkPackets
import space.impact.packet_network.network.NetworkHandler.sendToPlayer
import java.util.*

object DraftManager {

    internal val DRAFT_RECIPES = HashMap<Int, DraftData>()
    internal val DRAFT_PROGRESS = HashMap<String, DraftPlayerProgress>()

    fun registerDraft(data: DraftData) {
        if (data.id > Short.MAX_VALUE)
            throw IndexOutOfBoundsException("Max limit of drafts ${Short.MAX_VALUE}")
        if (DRAFT_RECIPES.containsKey(data.id))
            throw IllegalArgumentException("Draft with key ${data.id} already registered")
        DRAFT_RECIPES[data.id] = data
    }

    fun getDraft(id: Int): DraftData? {
        return DRAFT_RECIPES[id]
    }

    fun unRegisterDraft(id: Int) {
        if (DRAFT_RECIPES.containsKey(id)) {
            DRAFT_RECIPES.remove(id)
        }
    }

    fun EntityPlayer.isConfirm(id: Int): Boolean {
        val keyPlayer = if (Config.usePlayerNameOrUUID) gameProfile.id.toString() else gameProfile.name
        return DRAFT_PROGRESS[keyPlayer].isConfirm(id)
    }

    fun EntityPlayer.learnDraft(id: Int) {
        val keyPlayer = if (Config.usePlayerNameOrUUID) gameProfile.id.toString() else gameProfile.name
        val progress = DRAFT_PROGRESS[keyPlayer]
        if (!progress.isConfirm(id)) {
            val updateProgress = progress?.copy(
                line = progress.line.copy(
                    data = progress.line.data + DraftProgress(id = id)
                )
            ) ?: DraftPlayerProgress(
                line = DraftProgressList(
                    data = listOf(
                        DraftProgress(id = id)
                    )
                )
            )
            DRAFT_PROGRESS[keyPlayer] = updateProgress
            sendToPlayer(NetworkPackets.SyncClientDraftProgress.transaction(updateProgress))
            saveSingleDraft(keyPlayer, updateProgress)
            addChatMessage(ChatComponentText(EnumChatFormatting.GOLD.toString() + " " + id))
        } else addChatMessage(ChatComponentText(EnumChatFormatting.GOLD.toString() + "Already Learning"))
    }

    fun writeDraft(draft: ItemStack, data: DraftData) {
        try {
            if (draft.item is DraftItem) {
                val draftDataTag = NBTTagCompound()
                val inputsTag = NBTTagList()
                for ((index, stack) in data.inputs.withIndex()) {
                    if (stack != null) {
                        val itemTag = NBTTagCompound()
                        itemTag.setInteger("dInput", index)
                        stack.writeToNBT(itemTag)
                        inputsTag.appendTag(itemTag)
                    }
                }
                draftDataTag.setTag("dInputs", inputsTag)

                val resultTag = NBTTagCompound()
                data.result.writeToNBT(resultTag)
                draftDataTag.setTag("dResult", resultTag)

                draftDataTag.setInteger("dKey", data.id)

                val draftTag = NBTTagCompound()
                draftTag.setTag("DraftData", draftDataTag)
                draft.tagCompound = draftTag
            }
        } catch (e: Exception) {
            if (ImpactResearch.IS_DEBUG) e.printStackTrace()
        }
    }

    fun readDraft(draft: ItemStack): DraftData? {
        try {
            if (draft.item is DraftItem) {
                val draftTag = draft.tagCompound ?: return null
                val draftDataTag = draftTag.getCompoundTag("DraftData") ?: return null


                val draftKey = draftDataTag.getInteger("dKey")

                val dResult = draftDataTag.getCompoundTag("dResult")
                val draftResult = ItemStack.loadItemStackFromNBT(dResult)

                val tagList = draftDataTag.getTagList("Items", 9)

                val draftInputs = arrayOfNulls<ItemStack>(9)
                for (i in 0..tagList.tagCount()) {
                    val itemTags = tagList.getCompoundTagAt(i)
                    val slot = itemTags.getInteger("Slot")
                    if (slot in 0..9) {
                        draftInputs[slot] = ItemStack.loadItemStackFromNBT(itemTags)
                    }
                }

                return DraftData(
                    id = draftKey,
                    inputs = draftInputs,
                    result = draftResult
                )
            }
            return null
        } catch (e: Exception) {
            if (ImpactResearch.IS_DEBUG) e.printStackTrace()
            return null
        }
    }
}
