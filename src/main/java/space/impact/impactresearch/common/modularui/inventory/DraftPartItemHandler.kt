package space.impact.impactresearch.common.modularui.inventory

import com.gtnewhorizons.modularui.api.forge.ItemStackHandler
import net.minecraft.item.ItemStack

class DraftPartItemHandler : ItemStackHandler(9) {

    override fun setStackInSlot(slot: Int, stack: ItemStack?) {
        if (stack == null || stack.itemDamage == slot) {
            super.setStackInSlot(slot, stack)
        }
    }
}
