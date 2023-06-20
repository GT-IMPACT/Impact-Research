package space.impact.impactresearch.draftlogic.ingame

import net.minecraft.item.ItemStack

class DraftBuilder {
    private val inputs: MutableList<ItemStack> = mutableListOf()
    private var id: Int = -1
    private var partDraftType: DraftManager.DraftType = DraftManager.DraftType.NOTHING
    private var sizePartDraft: Int = 1

    fun addKey(id: Int): DraftBuilder {
        this.id = id
        return this
    }

    fun addRecipe(vararg stacks: ItemStack): DraftBuilder {
        inputs += stacks
        return this
    }

    fun addType(type: DraftManager.DraftType, size: Int): DraftBuilder {
        partDraftType = type
        sizePartDraft = size
        return this
    }

    fun addResult(result: ItemStack) {
        if (id > 0 && inputs.isNotEmpty()) {
            val data = DraftData(
                result = result,
                inputs = inputs,
                id = id,
                type = partDraftType,
                sizeType = sizePartDraft,
            )
            DraftManager.registerDraft(data)
        }
    }
}
