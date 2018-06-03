package com.hoborific.cryo.frogsforging.anvil

import com.teamwizardry.librarianlib.features.container.ContainerBase
import com.teamwizardry.librarianlib.features.container.GuiHandler
import com.teamwizardry.librarianlib.features.container.InventoryWrapper
import com.teamwizardry.librarianlib.features.container.builtin.BaseWrappers
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.ResourceLocation

/**
 *
 */
class ContainerImprovedAnvil(player: EntityPlayer, internal val anvilTile: TileEntityImprovedAnvil) :
    ContainerBase(player) {
    val invPlayer = BaseWrappers.player(player)
    val invBlock = InventoryWrapperImprovedAnvil(anvilTile)

    init {
        addSlots(invPlayer)
        addSlots(invBlock)

        transferRule().from(invPlayer.main).deposit(invBlock.main)
        transferRule().from(invBlock.main).deposit(invPlayer.main)
    }

    companion object {
        val NAME = ResourceLocation("frogsforging:container")

        init {
            GuiHandler.registerBasicContainer(
                NAME,
                { player, _, tile ->
                    ContainerImprovedAnvil(
                        player,
                        tile as TileEntityImprovedAnvil
                    )
                },
                { _, container -> GuiContainerImprovedAnvil(container) })
        }
    }
}

/**
 * Simple wrapper for the Forge Anvil tile entity that allows other objects to manipulate its inventory.
 */
class InventoryWrapperImprovedAnvil(anvilTile: TileEntityImprovedAnvil) : InventoryWrapper(anvilTile) {
    val main = slots[0..2]
}