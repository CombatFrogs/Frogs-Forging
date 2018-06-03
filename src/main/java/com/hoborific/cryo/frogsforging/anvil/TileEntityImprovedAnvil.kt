package com.hoborific.cryo.frogsforging.anvil

import com.hoborific.cryo.frogsforging.FrogsForgingMod
import com.hoborific.cryo.frogsforging.registry.SmithingRegistry
import com.hoborific.cryo.frogsforging.smithing.WorkingTechnique
import com.teamwizardry.librarianlib.features.autoregister.TileRegister
import com.teamwizardry.librarianlib.features.base.block.tile.BlockModContainer
import com.teamwizardry.librarianlib.features.base.block.tile.TileModInventory
import com.teamwizardry.librarianlib.features.container.GuiHandler
import com.teamwizardry.librarianlib.features.kotlin.toNonnullList
import com.teamwizardry.librarianlib.features.saving.Save
import net.minecraft.block.material.Material
import net.minecraft.block.state.IBlockState
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.item.ItemStack
import net.minecraft.tileentity.TileEntity
import net.minecraft.util.EnumFacing
import net.minecraft.util.EnumHand
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import java.util.UUID

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
    private val anvilTechniqueHistory: ArrayList<Byte> = arrayListOf(-1, -1, -1)

    @Save
    private var lastPlayerWorked: UUID? = null

    internal val publicAnvilHistory get() = anvilTechniqueHistory.toNonnullList()

    internal fun handleTechniqueUsed(player: EntityPlayer, techniqueId: Int) {
        val itemBeingWorked = getStackInSlot(0)
        if (itemBeingWorked.isEmpty) return

        if (techniqueId !in WorkingTechnique.LIGHT_HIT.ordinal..WorkingTechnique.SHRINK.ordinal) {
            FrogsForgingMod.logger?.debug(
                "Anvil techniqueId packet specified techniqueId out of range. Sent by player %s at (%d, %d, %d)".format(
                    player.name,
                    pos.x,
                    pos.y,
                    pos.z
                )
            )
        }

        val technique = WorkingTechnique.values()[techniqueId]
        anvilTechniqueHistory.add(0, techniqueId.toByte())
        while (anvilTechniqueHistory.size > 3) anvilTechniqueHistory.removeAt(anvilTechniqueHistory.size - 1)

        anvilWorkingValue -= technique.recipeModifier
        lastPlayerWorked = player.uniqueID

        markDirty()

        val smithingRegistry = SmithingRegistry.getInstance()
        if (!smithingRegistry.hasRecipesForInput(itemBeingWorked)) return

        println(
            "Working value: %d, Last techniqueId: %s".format(
                anvilWorkingValue,
                technique.toString()
            )
        )

        if (anvilWorkingValue !in 0..100) {
            setStackInSlot(0, ItemStack.EMPTY)
            return
        }

        // TODO: return if anvilWorkingValue is not in range of target value i.e. in 50 - tolerance .. 50 + tolerance
        if (anvilWorkingValue != 50) return

        val stableHistory = anvilTechniqueHistory.toNonnullList()
        if (stableHistory.size < 3) {
            FrogsForgingMod.logger?.error(
                "Anvil NBT for entity at (%d, %d, %d) corrupted; expected history of 3 but found %d.".format(
                    pos.x,
                    pos.y,
                    pos.z,
                    stableHistory.size
                )
            )
        }

        val replacementItemStack: ItemStack? = smithingRegistry.getOutputForConfigurationOrNull(
            itemBeingWorked,
            stableHistory[2],
            stableHistory[1],
            stableHistory[0]
        )

        if (replacementItemStack != null) {
            setStackInSlot(0, replacementItemStack)
        }
    }

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