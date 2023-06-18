package space.impact.impactresearch

import cpw.mods.fml.common.Mod
import cpw.mods.fml.common.SidedProxy
import cpw.mods.fml.common.event.FMLPostInitializationEvent
import cpw.mods.fml.common.event.FMLServerAboutToStartEvent
import cpw.mods.fml.common.event.FMLServerStoppedEvent
import cpw.mods.fml.common.registry.GameRegistry
import net.minecraft.block.Block
import net.minecraft.init.Items
import net.minecraft.item.ItemStack
import software.bernie.geckolib3.GeckoLib
import software.bernie.geckolib3.item.GeoItemBlock
import space.impact.impactresearch.common.blocks.CraftTableResearchBlock
import space.impact.impactresearch.common.items.DraftItem
import space.impact.impactresearch.common.tiles.CraftTableResearchTile
import space.impact.impactresearch.config.Config
import space.impact.impactresearch.draftlogic.ingame.DraftBuilder
import space.impact.impactresearch.draftlogic.interactor.DraftInteractor
import space.impact.impactresearch.events.PlayerJoinEvent
import space.impact.impactresearch.network.registerPackets
import space.impact.impactresearch.proxy.CommonProxy

@Mod(
    modid = MODID,
    name = MODNAME,
    version = VERSION,
    acceptedMinecraftVersions = "[1.7.10]",
    modLanguageAdapter = "net.shadowfacts.forgelin.KotlinAdapter"
)
object ImpactResearch {

    @SidedProxy(
        clientSide = "$GROUPNAME.$MODID.proxy.ClientProxy",
        serverSide = "$GROUPNAME.$MODID.proxy.CommonProxy",
    )
    lateinit var proxy: CommonProxy

    val IS_DEBUG = true/* System.getenv("IMPACT_DEBUG") != null*/

    lateinit var craftingResearchBlock: Block
    lateinit var draftItem: DraftItem

    init {
        Config.init()
        registerPackets()
    }

    @Mod.EventHandler
    fun postInit(event: FMLPostInitializationEvent) {
        GeckoLib.initialize( event.side.isClient)
        repeat(55) {
            DraftBuilder()
                .addKey(it)
                .addIndexStack(0, ItemStack(Items.coal))
                .addResult(ItemStack(Items.diamond))
        }

        craftingResearchBlock = CraftTableResearchBlock()
            .setBlockName("crafting_table_block")
            .setBlockTextureName("crafting_table")
        GameRegistry.registerBlock(craftingResearchBlock, GeoItemBlock::class.java,"crafting_table_block")
        GameRegistry.registerTileEntity(CraftTableResearchTile::class.java, "CraftTableResearchTile")

        proxy.init()

        draftItem = DraftItem()
        GameRegistry.registerItem(draftItem, "draft_item")


        PlayerJoinEvent()
    }

    @Mod.EventHandler
    fun serverAboutToStart(event: FMLServerAboutToStartEvent) {
        DraftInteractor.serverAboutToStart()
    }

    @Mod.EventHandler
    private fun serverStopped(event: FMLServerStoppedEvent) {
        DraftInteractor.serverStopped()
    }
}
