package space.impact.impactresearch.common.items

import cpw.mods.fml.common.registry.GameRegistry
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.creativetab.CreativeTabs
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.item.ItemStack
import net.minecraft.util.IIcon
import space.impact.impactresearch.MODID
import space.impact.impactresearch.draftlogic.ingame.IDraftShape

class DraftPartItem : Item(), IDraftShape {

    companion object {
        private const val SIZE = 9
        @JvmStatic
        var INSTANCE: DraftPartItem = DraftPartItem()
            private set
    }

    init {
        setHasSubtypes(true)
        unlocalizedName = "draft_item_part"
        GameRegistry.registerItem(this, unlocalizedName)
    }

    private val icons = arrayOfNulls<IIcon>(9)

    @SideOnly(Side.CLIENT)
    override fun registerIcons(reg: IIconRegister) {
        for (i in 0 until SIZE) {
            icons[i] = reg.registerIcon("$MODID:draft_part_$i")
        }
    }

    @SideOnly(Side.CLIENT)
    override fun getIconFromDamage(meta: Int): IIcon? {
        return icons[meta]
    }

    override fun getUnlocalizedName(stack: ItemStack): String {
        return super.getUnlocalizedName() + "." + stack.itemDamage
    }

    @SideOnly(Side.CLIENT)
    override fun getSubItems(item: Item?, tab: CreativeTabs?, list: MutableList<Any?>) {
        for (i in 0 until SIZE) {
            list.add(ItemStack(item, 1, i))
        }
    }

    @SideOnly(Side.CLIENT)
    override fun addInformation(stack: ItemStack, player: EntityPlayer, info: MutableList<Any?>, f3: Boolean) {
        super.addInformation(stack, player, info, f3)
    }

    override fun getShape(slot: Int, stack: ItemStack): Boolean {
        return slot == stack.itemDamage
    }
}
