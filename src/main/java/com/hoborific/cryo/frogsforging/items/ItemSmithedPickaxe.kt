package com.hoborific.cryo.frogsforging.items

import com.hoborific.cryo.frogsforging.FrogsForgingMod
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.item.ItemPickaxe
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class ItemSmithedPickaxe(
    material: Item.ToolMaterial
) : ItemPickaxe(material) {

    init {
        setRegistryName("iron_pickaxe")
        unlocalizedName = FrogsForgingMod.MODID + ".iron_pickaxe"
    }

    @SideOnly(Side.CLIENT)
    fun initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName!!.toString()))
    }

    companion object {
        val TOOL_QUALITY_TAG = "BLOCKS_DESTROYED"
    }
}
