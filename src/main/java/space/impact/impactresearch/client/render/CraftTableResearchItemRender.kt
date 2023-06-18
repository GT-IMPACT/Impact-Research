package space.impact.impactresearch.client.render

import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.client.renderer.OpenGlHelper
import net.minecraft.item.Item
import org.lwjgl.opengl.GL11
import software.bernie.geckolib3.core.IAnimatable
import software.bernie.geckolib3.model.AnimatedGeoModel
import software.bernie.geckolib3.renderers.geo.GeoItemRenderer
import software.bernie.geckolib3.util.MatrixStack

@SideOnly(Side.CLIENT)
class CraftTableResearchItemRender<T>(provider: AnimatedGeoModel<T>) : GeoItemRenderer<T>(provider) where T : IAnimatable, T : Item {

    override fun renderEarly(animatable: T, poseStack: MatrixStack, partialTick: Float, red: Float, green: Float, blue: Float, alpha: Float) {
        GL11.glEnable(GL11.GL_BLEND)
        OpenGlHelper.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, 1, 0)
        super.renderEarly(animatable, poseStack, partialTick, red, green, blue, alpha)
    }
}
