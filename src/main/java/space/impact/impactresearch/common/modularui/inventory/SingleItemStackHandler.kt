package space.impact.impactresearch.common.modularui.inventory

import com.gtnewhorizons.modularui.api.forge.ItemStackHandler
import net.minecraft.item.ItemStack

open class SingleItemStackHandler(size: Int) : ItemStackHandler(size) {

    constructor(itemStack: ItemStack) : this(1) {
        setStackInSlot(0, itemStack)
    }

    override fun getSlotLimit(slot: Int) = 1
}
