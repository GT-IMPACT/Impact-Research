package space.impact.impactresearch.draftlogic.ingame

import net.minecraft.item.ItemStack

class DraftData(
    val result: ItemStack,
    val inputs: Array<ItemStack?>,
    val id: Int,
)
