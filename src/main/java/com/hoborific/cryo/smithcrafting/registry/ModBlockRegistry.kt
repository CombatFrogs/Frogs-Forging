package com.hoborific.cryo.smithcrafting.registry

import com.hoborific.cryo.smithcrafting.blocks.BlockForgeAnvil
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

object ModBlockRegistry {

    @GameRegistry.ObjectHolder("smithcrafting:forge_anvil")
    var blockForgeAnvil: BlockForgeAnvil? = null

    @SideOnly(Side.CLIENT)
    fun initModels() {
        blockForgeAnvil!!.initModel()
    }
}