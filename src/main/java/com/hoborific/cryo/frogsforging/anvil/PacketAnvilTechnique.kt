package com.hoborific.cryo.frogsforging.anvil

import com.hoborific.cryo.frogsforging.FrogsForgingMod
import com.teamwizardry.librarianlib.features.network.PacketBase
import com.teamwizardry.librarianlib.features.saving.Save
import net.minecraft.util.math.BlockPos
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext
import net.minecraftforge.fml.relauncher.Side

class PacketAnvilTechnique() : PacketBase() {

    @Save
    private var technique: Int? = null

    @Save
    private var blockPos: BlockPos? = null

    constructor(technique: Int, blockPos: BlockPos) : this() {
        this.technique = technique
        this.blockPos = blockPos
    }

    override fun handle(ctx: MessageContext) {
        val technique = technique
        val blockPos = blockPos

        if (technique == null || blockPos == null) {
            FrogsForgingMod.logger?.error(
                "Received packet with null fields. Not synced by library?"
            )
            return
        }

        if (ctx.side != Side.SERVER) {
            FrogsForgingMod.logger?.error(
                "Received Anvil technique packet on side client (%d, %d, %d). This is a bug.".format(
                    blockPos.x,
                    blockPos.y,
                    blockPos.z
                )
            )
            return
        }

        val player = ctx.serverHandler.player ?: return
        val anvilBlock = player.world.getTileEntity(blockPos)

        if (anvilBlock == null || anvilBlock !is TileEntityImprovedAnvil) {
            FrogsForgingMod.logger?.debug(
                "Received Anvil technique packet at position (%d, %d, %d) from player %s but tile entity does not exist.".format(
                    blockPos.x,
                    blockPos.y,
                    blockPos.z,
                    player.name
                )
            )
            return
        }

        anvilBlock.handleTechniqueUsed(player, technique)
    }
}