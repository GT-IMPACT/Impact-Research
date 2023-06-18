package space.impact.impactresearch.client.render

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.ResourceLocation
import software.bernie.geckolib3.core.IAnimatable
import software.bernie.geckolib3.model.AnimatedGeoModel
import software.bernie.geckolib3.renderers.geo.GeoBlockRenderer
import software.bernie.geckolib3.util.MatrixStack
import space.impact.impactresearch.MODID
import space.impact.impactresearch.common.tiles.CraftTableResearchTile

@Suppress("UNCHECKED_CAST")
@SideOnly(Side.CLIENT)
class CraftTableResearchTileRender<T>(provider: AnimatedGeoModel<T>) : GeoBlockRenderer<T>(provider) where T : TileEntity, T : IAnimatable {

    override fun renderEarly(animatable: T, poseStack: MatrixStack, partialTick: Float, red: Float, green: Float, blue: Float, alpha: Float) {
        when (animatable.getBlockMetadata()) {
            2 -> rotateBlock(EnumFacing.NORTH, poseStack)
            3 -> rotateBlock(EnumFacing.SOUTH, poseStack)
            4 -> rotateBlock(EnumFacing.WEST, poseStack)
            5 -> rotateBlock(EnumFacing.EAST, poseStack)
        }
        super.renderEarly(animatable, poseStack, partialTick, red, green, blue, alpha)
    }

    override fun renderTileEntityAt(te: TileEntity, x: Double, y: Double, z: Double, partialTick: Float) {
        this.render(te as T, MATRIX_STACK, x.toFloat(), y.toFloat(), z.toFloat(), partialTick)
    }
}

@SideOnly(Side.CLIENT)
class CraftTableResearchModel<T : IAnimatable> : AnimatedGeoModel<T>() {

    companion object {
        private val modelResource = ResourceLocation(MODID, "models/draft_workbench.geo.json")
        private val textureResource = ResourceLocation(MODID, "textures/blocks/draft_workbench.png")
        private val animationResource = ResourceLocation(MODID, "animations/nothing.geo.json")
    }

    override fun getModelLocation(p0: T): ResourceLocation {
        return modelResource
    }

    override fun getTextureLocation(p0: T): ResourceLocation {
        return textureResource
    }

    override fun getAnimationFileLocation(p0: T): ResourceLocation {
        return animationResource
    }
}
