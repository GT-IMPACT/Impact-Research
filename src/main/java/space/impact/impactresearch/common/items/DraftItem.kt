package space.impact.impactresearch.common.items

import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.entity.player.EntityPlayerMP
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumChatFormatting
import net.minecraft.util.IIcon
import net.minecraft.util.StatCollector
import net.minecraft.world.World
import space.impact.impactresearch.MODID
import space.impact.impactresearch.draftlogic.ingame.DraftData
import space.impact.impactresearch.draftlogic.ingame.DraftManager
import space.impact.impactresearch.draftlogic.ingame.DraftManager.isConfirm
import space.impact.impactresearch.draftlogic.ingame.DraftManager.learnDraft
import space.impact.impactresearch.draftlogic.ingame.IDraftItem

class DraftItem : Item(), IDraftItem {

    init {
        setHasSubtypes(true)
        unlocalizedName = "draft_item"
    }

    private val icons = arrayOfNulls<IIcon>(2)

    override fun registerIcons(reg: IIconRegister) {
        icons[0] = reg.registerIcon("$MODID:draft_empty")
        icons[1] = reg.registerIcon("$MODID:draft_filled")
    }

    override fun getIconFromDamage(meta: Int): IIcon? {
        return icons[meta]
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        return super.getUnlocalizedName() + "." + stack.itemDamage
    }

    override fun getSubItems(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>) {
        list.add(ItemStack(item, 1, 0))
        list.add(ItemStack(item, 1, 1))
    }

    override fun writeDraft(draftData: DraftData?): ItemStack? {
        if (draftData != null) {
            val filled = ItemStack(this, 1, 1)
            DraftManager.writeDraft(filled, draftData)
            return filled
        }
        return null
    }

    override fun readDraft(draft: ItemStack?): DraftData? {
        return if (draft == null) null else DraftManager.readDraft(draft)
    }

    override fun addInformation(stack: ItemStack, player: EntityPlayer, info: MutableList<Any?>, f3: Boolean) {
        super.addInformation(stack, player, info, f3)
        when (stack.itemDamage) {
            1 -> getInfo(stack)?.also(info::add)
        }
        val data = readDraft(stack) ?: return
        if (player.isConfirm(data.id)) info.add("Already Learning")
    }

    private fun getInfo(stack: ItemStack): String? {
        return stack.tagCompound?.let { tag ->
            tag.getCompoundTag("DraftData")?.let { data ->
                val dResult = data.getCompoundTag("dResult")
                ItemStack.loadItemStackFromNBT(dResult)?.displayName?.let {
                    StatCollector.translateToLocalFormatted("draft_based_making") + "${EnumChatFormatting.BLUE} $it"
                }
            }
        }
    }

    override fun onItemUseFirst(stack: ItemStack, player: EntityPlayer, world: World?, x: Int, y: Int, z: Int, side: Int, hitX: Float, hitY: Float, hitZ: Float): Boolean {
        if (player is EntityPlayerMP) {
            readDraft(stack)?.also { draft ->
                player.learnDraft(draft.id)
            }
        }
        return super.onItemUseFirst(stack, player, world, x, y, z, side, hitX, hitY, hitZ)
    }
}
