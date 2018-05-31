package com.hoborific.cryo.frogsforging.proxy

import com.hoborific.cryo.frogsforging.container.ContainerForgeAnvil
import com.hoborific.cryo.frogsforging.gui.GuiForgeAnvil
import com.hoborific.cryo.frogsforging.tileentities.TileEntityForgeAnvil
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.util.math.BlockPos
import net.minecraft.world.World
import net.minecraftforge.fml.common.network.IGuiHandler

class GuiProxy : IGuiHandler {

    override fun getServerGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val pos = BlockPos(x, y, z)
        val te = world.getTileEntity(pos)
        return if (te is TileEntityForgeAnvil) {
            ContainerForgeAnvil(player.inventory, te)
        } else null
    }

    override fun getClientGuiElement(ID: Int, player: EntityPlayer, world: World, x: Int, y: Int, z: Int): Any? {
        val pos = BlockPos(x, y, z)
        val te = world.getTileEntity(pos)
        if (te is TileEntityForgeAnvil) {
            return GuiForgeAnvil(te, ContainerForgeAnvil(player.inventory, te))
        }
        return null
    }
}