package space.impact.impactresearch.proxy

import cpw.mods.fml.client.registry.ClientRegistry
import net.minecraft.item.Item
import net.minecraftforge.client.MinecraftForgeClient
import space.impact.impactresearch.ImpactResearch
import space.impact.impactresearch.client.render.CraftTableResearchItemRender
import space.impact.impactresearch.client.render.CraftTableResearchModel
import space.impact.impactresearch.client.render.CraftTableResearchTileRender
import space.impact.impactresearch.common.tiles.CraftTableResearchTile

class ClientProxy : CommonProxy() {

    override fun init() {
        MinecraftForgeClient.registerItemRenderer(
            Item.getItemFromBlock(ImpactResearch.craftingResearchBlock),
            CraftTableResearchItemRender(CraftTableResearchModel())
        )
        ClientRegistry.bindTileEntitySpecialRenderer(
            CraftTableResearchTile::class.java,
            CraftTableResearchTileRender(CraftTableResearchModel())
        )
    }
}