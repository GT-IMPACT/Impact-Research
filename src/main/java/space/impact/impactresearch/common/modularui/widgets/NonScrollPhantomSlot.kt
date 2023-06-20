package space.impact.impactresearch.common.modularui.widgets

import com.gtnewhorizons.modularui.api.forge.IItemHandlerModifiable
import com.gtnewhorizons.modularui.common.internal.wrapper.BaseSlot
import com.gtnewhorizons.modularui.common.widget.SlotWidget
import net.minecraft.item.ItemStack

class NonScrollPhantomSlot(handler: IItemHandlerModifiable, index: Int) : SlotWidget(BaseSlot.phantom(handler, index)) {
    override fun onMouseScroll(direction: Int): Boolean {
        return false
    }

    override fun phantomScroll(direction: Int) = Unit

    override fun phantomClick(clickData: ClickData) {
        val item = context.cursor.itemStack?.item?.let { ItemStack(it, 1) }
        if (mcSlot.isItemValidPhantom(item)) mcSlot.putStack(item) else mcSlot.putStack(null)
    }
}
