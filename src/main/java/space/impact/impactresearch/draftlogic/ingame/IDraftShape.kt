package space.impact.impactresearch.draftlogic.ingame

import net.minecraft.item.ItemStack

interface IDraftShape {
    fun getShape(slot: Int, stack: ItemStack): Boolean
}

internal fun ItemStack?.isValidDraftShape(slotId: Int): Boolean {
    return (this?.item as? IDraftShape)?.getShape(slotId, this) ?: false
}
