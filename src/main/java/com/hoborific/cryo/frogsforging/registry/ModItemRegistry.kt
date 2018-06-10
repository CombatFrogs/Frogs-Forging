package com.hoborific.cryo.frogsforging.registry

import com.hoborific.cryo.frogsforging.items.ItemForgeHammer
import com.hoborific.cryo.frogsforging.items.ItemSmithedPickaxe
import net.minecraftforge.fml.common.registry.GameRegistry
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

object ModItemRegistry {

    @GameRegistry.ObjectHolder("frogsforging:iron_pickaxe")
    var itemSmithedPickaxe: ItemSmithedPickaxe? = null

    @GameRegistry.ObjectHolder("frogsforging:forge_hammer")
    var itemForgeHammer: ItemForgeHammer? = null

    @SideOnly(Side.CLIENT)
    fun initModels() {
        itemSmithedPickaxe!!.initModel()
        itemForgeHammer!!.initModel()
    }
}
