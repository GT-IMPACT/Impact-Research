package space.impact.impactresearch.draftlogic.ingame

import net.minecraft.item.ItemStack

class DraftData(
    val result: ItemStack,
    val inputs: List<ItemStack>,
    val id: Int,
    val type: DraftManager.DraftType,
    val sizeType: Int,
)
