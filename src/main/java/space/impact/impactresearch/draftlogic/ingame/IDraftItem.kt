package space.impact.impactresearch.draftlogic.ingame

import net.minecraft.item.ItemStack

interface IDraftItem {
    fun writeDraft(draftData: DraftData?): ItemStack?
    fun readDraft(draft: ItemStack?): DraftData?
}
