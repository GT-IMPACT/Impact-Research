package space.impact.impactresearch.common.blocks

import com.gtnewhorizons.modularui.api.UIInfos
import cpw.mods.fml.relauncher.Side
import cpw.mods.fml.relauncher.SideOnly
import net.minecraft.block.BlockContainer
import net.minecraft.block.material.Material
import net.minecraft.client.renderer.texture.IIconRegister
import net.minecraft.entity.EntityLivingBase
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.AxisAlignedBB
import net.minecraft.util.IIcon
import net.minecraft.util.MathHelper
import net.minecraft.util.Vec3
import net.minecraft.world.World
import space.impact.impactresearch.common.tiles.CraftTableResearchTile

class CraftTableResearchBlock : BlockContainer(Material.wood) {

    init {
        setHarvestLevel("axe", 0)
        setHardness(2.0f)
        setResistance(6.0f)
    }

    @SideOnly(Side.CLIENT)
    override fun registerBlockIcons(reg: IIconRegister) {
        blockIcon = reg.registerIcon("crafting_table_side")
    }

    @SideOnly(Side.CLIENT)
    override fun getIcon(side: Int, meta: Int): IIcon {
        return blockIcon
    }

    override fun createNewTileEntity(world: World, meta: Int): TileEntity {
        return CraftTableResearchTile()
    }

    override fun onBlockActivated(
        world: World, x: Int, y: Int, z: Int,
        player: EntityPlayer, side: Int,
        hintX: Float, hintY: Float, hintZ: Float
    ): Boolean {
        if (!world.isRemote) UIInfos.TILE_MODULAR_UI
            .open(player, world, Vec3.createVectorHelper(x.toDouble(), y.toDouble(), z.toDouble()))
        return true
    }

    override fun renderAsNormalBlock(): Boolean {
        return false
    }

    override fun isOpaqueCube(): Boolean {
        return false
    }

    @SideOnly(Side.CLIENT)
    override fun getSelectedBoundingBoxFromPool(world: World, x: Int, y: Int, z: Int): AxisAlignedBB {
        return AxisAlignedBB.getBoundingBox(0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
    }

    override fun getMobilityFlag(): Int {
        return 2
    }

    override fun getRenderType(): Int {
        return -1
    }

    override fun onBlockPlacedBy(world: World, x: Int, y: Int, z: Int, placer: EntityLivingBase, stack: ItemStack) {
        when (MathHelper.floor_double(placer.rotationYaw.toDouble() * 4.0 / 360.0 + 0.5) and 3) {
            0 -> world.setBlockMetadataWithNotify(x, y, z, 2, 2)
            1 -> world.setBlockMetadataWithNotify(x, y, z, 5, 2)
            2 -> world.setBlockMetadataWithNotify(x, y, z, 3, 2)
            3 -> world.setBlockMetadataWithNotify(x, y, z, 4, 2)
        }
    }
}
