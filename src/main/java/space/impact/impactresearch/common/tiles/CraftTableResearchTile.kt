package space.impact.impactresearch.common.tiles

import com.gtnewhorizons.modularui.api.ModularUITextures
import com.gtnewhorizons.modularui.api.forge.ItemStackHandler
import com.gtnewhorizons.modularui.api.math.Size
import com.gtnewhorizons.modularui.api.screen.ITileWithModularUI
import com.gtnewhorizons.modularui.api.screen.ModularWindow
import com.gtnewhorizons.modularui.api.screen.UIBuildContext
import com.gtnewhorizons.modularui.common.internal.wrapper.BaseSlot
import com.gtnewhorizons.modularui.common.widget.SlotWidget
import com.gtnewhorizons.modularui.common.widget.VanillaButtonWidget
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.AxisAlignedBB
import software.bernie.geckolib3.core.IAnimatable
import software.bernie.geckolib3.core.builder.AnimationBuilder
import software.bernie.geckolib3.core.manager.AnimationData
import software.bernie.geckolib3.core.manager.AnimationFactory
import software.bernie.geckolib3.util.GeckoLibUtil
import space.impact.impactresearch.common.modularui.inventory.SingleItemStackHandler
import space.impact.impactresearch.draftlogic.ingame.DraftManager
import space.impact.impactresearch.draftlogic.ingame.IDraftItem


class CraftTableResearchTile : TileEntity(), ITileWithModularUI, IAnimatable {

    companion object {
        protected val DEPLOY = AnimationBuilder()
    }

    private val internalInventory = ItemStackHandler(27)
    private val craftingGrid = SingleItemStackHandler(9)
    private val draftSlotInventory = SingleItemStackHandler(1)
    private val resultSlotInventory = SingleItemStackHandler(1)

    override fun createWindow(ctx: UIBuildContext): ModularWindow {
        val builder = ModularWindow.builder(Size(176, 272))
        builder
            .setBackground(ModularUITextures.VANILLA_BACKGROUND)
            .bindPlayerInventory(ctx.player)

        builder.widget(SlotWidget(draftSlotInventory, 0).setChangeListener(Runnable {
            checkResult()
        }).setPos(124, 35 - 18))
        builder.widget(SlotWidget(
            BaseSlot(resultSlotInventory, 0).setAccess(true, false)
        ).setChangeListener { w ->
            if (w.mcSlot.hasStack) craftingGrid.extractItem(0, 1, false)
        }.setPos(124, 35)
        )

        for (i in 0..2) {
            for (j in 0..2) {
                builder.widget(SlotWidget.phantom(craftingGrid, j + i * 3).setPos(30 + j * 18, 17 + i * 18))
            }
        }

        for (i in 0..2) {
            for (j in 0..8) {
                builder.widget(SlotWidget(internalInventory, j + i * 9).setPos(7 + j * 18, 98 + i * 18))
            }
        }

        builder.widget(
            VanillaButtonWidget()
                .setDisplayString("Start")
                .setOnClick { _, w ->
                    if (!w.isClient) checkResult()
                }
                .setSize(50, 16)
                .setPos(70, 55)
        )

        return builder.build()
    }

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        data.setTag("InternalInventory", internalInventory.serializeNBT())
        data.setTag("CraftingGridInventory", craftingGrid.serializeNBT())

    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        internalInventory.deserializeNBT(data.getCompoundTag("InternalInventory"))
        craftingGrid.deserializeNBT(data.getCompoundTag("CraftingGridInventory"))

    }

    fun checkResult() {
        val stack = craftingGrid.getStackInSlot(0)
        if (stack != null && stack.item is IDraftItem) {

            val keys = 1..55
            val draft = DraftManager.getDraft(keys.random())
            val draftFilled = (stack.item as IDraftItem).writeDraft(draft)

            resultSlotInventory.setStackInSlot(0, draftFilled)
        }
    }

    override fun registerControllers(data: AnimationData) {
    }

    private val factory = GeckoLibUtil.createFactory(this)

    override fun getFactory(): AnimationFactory {
        return factory
    }

    @SideOnly(Side.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        return AxisAlignedBB.getBoundingBox(xCoord.toDouble(), yCoord.toDouble(), zCoord.toDouble(), xCoord.toDouble() + 1, yCoord.toDouble() + 1, zCoord.toDouble() + 1)
    }

    override fun canUpdate(): Boolean {
        return false
    }
}
