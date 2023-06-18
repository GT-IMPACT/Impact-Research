package space.impact.impactresearch.common.modularui.inventory

import com.gtnewhorizons.modularui.api.forge.IItemHandler
import net.minecraft.item.ItemStack

class ItemHandlerDelegate(val delegate: IItemHandler) : IItemHandler {
    override fun getSlots(): Int {
        return delegate.slots
    }

    override fun getStackInSlot(slot: Int): ItemStack {
        return delegate.getStackInSlot(slot)
    }

    override fun insertItem(slot: Int, stack: ItemStack, simulate: Boolean): ItemStack? {
        return delegate.insertItem(slot, stack, simulate)
    }

    override fun extractItem(slot: Int, amount: Int, simulate: Boolean): ItemStack? {
        return delegate.extractItem(slot, amount, simulate)
    }

    override fun getSlotLimit(slot: Int): Int {
        return delegate.getSlotLimit(slot)
    }
}
