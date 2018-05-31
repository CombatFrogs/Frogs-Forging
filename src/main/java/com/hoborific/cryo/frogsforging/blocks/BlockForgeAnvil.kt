package com.hoborific.cryo.frogsforging.blocks

import com.hoborific.cryo.frogsforging.FrogsForgingMod
import com.hoborific.cryo.frogsforging.tileentities.TileEntityForgeAnvil
import net.minecraft.block.Block
import net.minecraft.block.ITileEntityProvider
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.Item
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class BlockForgeAnvil : Block(Material.IRON), ITileEntityProvider {
    init {
        unlocalizedName = FrogsForgingMod.MODID + ".forge_anvil"
        setRegistryName("forge_anvil")
    }

    @SideOnly(Side.CLIENT)
    fun initModel() {
        ModelLoader.setCustomModelResourceLocation(
            Item.getItemFromBlock(this),
            0,
            ModelResourceLocation(registryName!!, "inventory")
        )
    }

    override fun createNewTileEntity(worldIn: World, meta: Int): TileEntity? {
        return TileEntityForgeAnvil()
    }

    override fun onBlockActivated(
        world: World?,
        pos: BlockPos?,
        state: IBlockState?,
        player: EntityPlayer?,
        hand: EnumHand?,
        side: EnumFacing?,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        if (world!!.isRemote) return true
        if (world.getTileEntity(pos!!) !is TileEntityForgeAnvil) return false

        player!!.openGui(
            FrogsForgingMod.instance!!,
            GUI_ID, world, pos.x, pos.y, pos.z
        )

        return true
    }

    companion object {
        val GUI_ID = 1
    }
}