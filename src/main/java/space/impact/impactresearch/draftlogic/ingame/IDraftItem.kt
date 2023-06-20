package space.impact.impactresearch.draftlogic.ingame

import net.minecraft.item.ItemStack

interface IDraftItem {
    fun writeDraft(draftData: DraftData?): ItemStack?
    fun readDraft(draft: ItemStack?): DraftData?
    fun isEmpty(draft: ItemStack?): Boolean
}

fun ItemStack?.isDraftEmpty(): Boolean {
    return (this?.item as? IDraftItem)?.isEmpty(this) ?: false
}
