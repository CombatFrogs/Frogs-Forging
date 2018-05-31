package com.hoborific.cryo.frogsforging.registry

import com.hoborific.cryo.frogsforging.items.ItemForgeHammer
import com.hoborific.cryo.frogsforging.items.ItemSmithedPickaxe
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

object ModItemRegistry {

    @GameRegistry.ObjectHolder("smithcrafting:iron_pickaxe")
    var itemSmithedPickaxe: ItemSmithedPickaxe? = null

    @GameRegistry.ObjectHolder("smithcrafting:forge_hammer")
    var itemForgeHammer: ItemForgeHammer? = null

    @SideOnly(Side.CLIENT)
    fun initModels() {
        itemSmithedPickaxe!!.initModel()
        itemForgeHammer!!.initModel()
    }
}
