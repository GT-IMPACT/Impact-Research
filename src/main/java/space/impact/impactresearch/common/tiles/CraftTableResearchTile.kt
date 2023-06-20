package space.impact.impactresearch.common.tiles

import com.gtnewhorizons.modularui.api.ModularUITextures
import com.gtnewhorizons.modularui.api.drawable.AdaptableUITexture
import com.gtnewhorizons.modularui.api.drawable.UITexture
import com.gtnewhorizons.modularui.api.forge.ItemStackHandler
import com.gtnewhorizons.modularui.api.math.Size
import com.gtnewhorizons.modularui.api.screen.ITileWithModularUI
import com.gtnewhorizons.modularui.api.screen.ModularWindow
import com.gtnewhorizons.modularui.api.screen.UIBuildContext
import com.gtnewhorizons.modularui.common.widget.ProgressBar
import com.gtnewhorizons.modularui.common.widget.SlotWidget
import com.gtnewhorizons.modularui.common.widget.VanillaButtonWidget
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.item.ItemStack
import net.minecraft.nbt.NBTTagCompound
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.AxisAlignedBB
import software.bernie.geckolib3.core.IAnimatable
import software.bernie.geckolib3.core.manager.AnimationData
import software.bernie.geckolib3.core.manager.AnimationFactory
import software.bernie.geckolib3.util.GeckoLibUtil
import space.impact.impactresearch.MODID
import space.impact.impactresearch.common.items.DraftPartItem
import space.impact.impactresearch.common.modularui.inventory.SingleItemStackHandler
import space.impact.impactresearch.common.modularui.widgets.NonScrollPhantomSlot
import space.impact.impactresearch.draftlogic.ingame.*
import space.impact.impactresearch.network.DraftNetworkPackets
import space.impact.packet_network.network.NetworkHandler.sendToAllAround

class CraftTableResearchTile : TileEntity(), ITileWithModularUI, IAnimatable {

    companion object {
        private val DRAFT_SLOT = AdaptableUITexture.of(MODID, "gui/slots/draft_slot", 16, 16, 0)
        private val DRAFT_SHAPE_SLOT = AdaptableUITexture.of(MODID, "gui/slots/draft_slot_shape", 16, 16, 0)
        private val RESEARCH_SLOT = AdaptableUITexture.of(MODID, "gui/slots/research_slot", 16, 16, 0)
        private val PROGRESS_BAR_DRAFT = UITexture.fullImage(MODID, "gui/progress/draft_progress")
        private val PROGRESS_RESEARCH_DRAFT = UITexture.fullImage(MODID, "gui/progress/research_progress")
        private const val START_TOP_PART = 17
    }

    private val internalInventory = ItemStackHandler(9)
    private val craftingGrid = ItemStackHandler(9)
    private val draftSlot = ItemStackHandler(1)
    private val researchSlot = SingleItemStackHandler(1)
    private val resultSlotInventory = SingleItemStackHandler(1)

    private var isStart: Boolean = false

    private val duration = 10 * 20
    private var progressLast = 0
    private var progress = 0
    private var findResearch: ItemStack? = null

    override fun createWindow(ctx: UIBuildContext): ModularWindow {
        val builder = ModularWindow.builder(Size(176, 272))
        builder
            .setBackground(ModularUITextures.VANILLA_BACKGROUND)
            .bindPlayerInventory(ctx.player)
        for (i in 0..2) {
            for (j in 0..2) {
                builder.widget(SlotWidget.phantom(craftingGrid, j + i * 3)
                    .disableInteraction()
                    .setPos(START_TOP_PART + 7 + j * 18, 17 + i * 18))
            }
        }
        builder.widget(
            SlotWidget(draftSlot, 0)
                .setFilter { it.isDraftEmpty() }
                .setBackground(DRAFT_SLOT)
                .setPos(START_TOP_PART + 79, 17)
        )
        builder.widget(
            ProgressBar()
                .setProgress { progress * 1f / duration }
                .setDirection(ProgressBar.Direction.RIGHT)
                .setTexture(PROGRESS_BAR_DRAFT, 36)
                .setSynced(false, false)
                .setSize(36, 18)
                .setPos(START_TOP_PART + 98, 18)
        )
        builder.widget(
            ProgressBar()
                .setProgress { progress * 1f / duration }
                .setDirection(ProgressBar.Direction.RIGHT)
                .setTexture(PROGRESS_RESEARCH_DRAFT, 36)
                .setSynced(false, false)
                .setSize(36, 18)
                .setPos(START_TOP_PART + 98, 53)
        )
        builder.widget(
            NonScrollPhantomSlot(researchSlot, 0)
                .setFilter { research ->
                    findResearch = DraftManager.findDraftByItem(research, ::setRequiredResearch)
                    findResearch != null
                }
                .setChangeListener { widget ->
                    if (widget.mcSlot.stack == null) {
                        for (i in 0 until craftingGrid.slots) craftingGrid.setStackInSlot(i, null)
                        findResearch = null
                    }
                }
                .setBackground(RESEARCH_SLOT)
                .setPos(START_TOP_PART + 79, 53)
        )
        builder.widget(
            SlotWidget(resultSlotInventory, 0)
                .setPos(START_TOP_PART + 115, 35)
        )
        for (j in 0..8) {
            builder.widget(
                SlotWidget(internalInventory, j)
                    .setFilter { it.isValidDraftShape(j) }
                    .setBackground(DRAFT_SHAPE_SLOT)
                    .setPos(7 + j * 18, 98)
            )
        }
        builder.widget(
            VanillaButtonWidget()
                .setDisplayString("Start")
                .setOnClick { _, w ->
                    if (!w.isClient) findResult()
                }
                .setSize(50, 16)
                .setPos(70, 150)
        )

        return builder.build()
    }

    override fun writeToNBT(data: NBTTagCompound) {
        super.writeToNBT(data)
        data.setTag("internalInventory", internalInventory.serializeNBT())
        data.setTag("craftingGrid", craftingGrid.serializeNBT())
        data.setTag("draftSlot", draftSlot.serializeNBT())
        data.setTag("researchSlot", researchSlot.serializeNBT())
        data.setTag("resultSlotInventory", resultSlotInventory.serializeNBT())

        val result = NBTTagCompound()
        findResearch?.writeToNBT(result)
        data.setTag("result", result)

        data.setInteger("progressLast", progressLast)
        data.setInteger("progress", progress)
        data.setBoolean("isStart", isStart)
    }

    override fun readFromNBT(data: NBTTagCompound) {
        super.readFromNBT(data)
        internalInventory.deserializeNBT(data.getCompoundTag("internalInventory"))
        craftingGrid.deserializeNBT(data.getCompoundTag("craftingGrid"))
        draftSlot.deserializeNBT(data.getCompoundTag("draftSlot"))
        researchSlot.deserializeNBT(data.getCompoundTag("researchSlot"))
        resultSlotInventory.deserializeNBT(data.getCompoundTag("resultSlotInventory"))

        findResearch = ItemStack.loadItemStackFromNBT(data.getCompoundTag("result"))

        progressLast = data.getInteger("progressLast")
        progress = data.getInteger("progress")
        isStart = data.getBoolean("isStart")
    }

    private fun setRequiredResearch(data: DraftData) {
        if (data.type.type > 0) {
            for (i in 0 until data.type.type) {
                craftingGrid.setStackInSlot(i, ItemStack(DraftPartItem.INSTANCE, data.sizeType, i))
            }
        }
    }

    private fun findResult() {
        if (!worldObj.isRemote && !isStart && findResearch != null) {

            var hasStart = false

            loop@for (i in 0 until craftingGrid.slots) {
                val partShape = craftingGrid.getStackInSlot(i)?.copy()
                val internalStack = internalInventory.getStackInSlot(i)?.copy()
                hasStart = partShape == null || internalStack != null && internalStack.stackSize >= partShape.stackSize
                if (!hasStart) break@loop
            }

            if (hasStart) {
                for (i in 0 until craftingGrid.slots) {
                    craftingGrid.getStackInSlot(i)?.also { stack ->
                        internalInventory.extractItem(i, stack.stackSize, false)
                    }
                }
            }
            isStart = hasStart
        }
    }

    override fun updateEntity() {
        if (!worldObj.isRemote && isStart) {
            ++progress
            if (progress == duration) confirmDraft()
            syncClient()
        }
    }

    private fun confirmDraft() {
        progress = 0
        isStart = false
        researchSlot.extractItem(0, 1, true)?.also { stack ->
            DraftManager.findDraftByItem(stack) {
                (draftSlot.getStackInSlot(0).item as? IDraftItem)?.writeDraft(it)?.also { result ->
                    draftSlot.extractItem(0, 1, false)
                    resultSlotInventory.setStackInSlot(0, result)
                }
            }
        }
    }

    private fun syncClient() {
        if (progressLast != progress) {
            sendToAllAround(DraftNetworkPackets.SyncClientProgress.transaction(progress), 16)
            progressLast = progress
        }
    }

    @SideOnly(Side.CLIENT)
    override fun getRenderBoundingBox(): AxisAlignedBB {
        return AxisAlignedBB.getBoundingBox(xCoord.toDouble(), yCoord.toDouble(), zCoord.toDouble(), xCoord.toDouble() + 1, yCoord.toDouble() + 1, zCoord.toDouble() + 1)
    }

    @SideOnly(Side.CLIENT)
    fun updateClient(progress: Int) {
        this.progress = progress
    }

    private val factory = GeckoLibUtil.createFactory(this)
    override fun getFactory(): AnimationFactory = factory
    override fun registerControllers(data: AnimationData) = Unit
}
