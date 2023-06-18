package space.impact.impactresearch.draftlogic.ingame

import net.minecraft.item.ItemStack

class DraftBuilder {
    private val inputs: Array<ItemStack?> = arrayOfNulls(9)
    private var id: Int = -1

    fun addKey(id: Int): DraftBuilder {
        this.id = id
        return this
    }

    fun addIndexStack(index: Int, stack: ItemStack): DraftBuilder {
        inputs[index] = stack
        return this
    }

    fun addResult(result: ItemStack) {
        if (id > 0 && inputs.isNotEmpty()) {
            val data = DraftData(
                result = result,
                inputs = inputs,
                id = id,
            )
            DraftManager.registerDraft(data)
        }
    }
}
