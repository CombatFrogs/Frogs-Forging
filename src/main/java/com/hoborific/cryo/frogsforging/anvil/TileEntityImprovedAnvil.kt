package com.hoborific.cryo.frogsforging.anvil

import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer
import com.teamwizardry.librarianlib.features.base.block.tile.TileModInventory
import com.teamwizardry.librarianlib.features.container.GuiHandler
import com.teamwizardry.librarianlib.features.saving.Save
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World

/**
 * Tile Entity bound in-world to the Anvil block that stores persistent metadata about the state of the tile, including
 * the current item forging progress, a partial history of applied techniques to perform recipe matching, and (TBA) the
 * selected item to match crafting with, to prevent accidentally finalizing an item into an undesired shape.
 */
@TileRegister(improvedAnvilRegistryName)
class TileEntityImprovedAnvil : TileModInventory(anvilInventorySize) {
    @Save
    private var anvilWorkingValue: Int = 0

    @Save
    private val anvilTechniqueHistory: List<Byte> = arrayListOf(-1, -1, -1)

    companion object {
        private const val anvilInventorySize = 3
    }
}

/**
 * It's a block. You weren't actually expecting documentation, were you?
 */
class BlockImprovedAnvil : BlockModContainer(improvedAnvilRegistryName, Material.IRON) {

    override fun createTileEntity(world: World, state: IBlockState): TileEntity? {
        return TileEntityImprovedAnvil()
    }

    override fun onBlockActivated(
        worldIn: World,
        pos: BlockPos,
        state: IBlockState,
        playerIn: EntityPlayer,
        hand: EnumHand,
        facing: EnumFacing,
        hitX: Float,
        hitY: Float,
        hitZ: Float
    ): Boolean {
        GuiHandler.open(ContainerImprovedAnvil.NAME, playerIn, pos)
        return true
    }
}