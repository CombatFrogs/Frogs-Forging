package com.hoborific.cryo.smithcrafting.items

import com.hoborific.cryo.smithcrafting.SmithcraftingMod
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.ModelResourceLocation
import net.minecraft.item.Item
import net.minecraft.item.ItemTool
import net.minecraftforge.client.model.ModelLoader
import net.minecraftforge.fml.relauncher.Side
import net.minecraftforge.fml.relauncher.SideOnly

class ItemForgeHammer(material: Item.ToolMaterial) : ItemTool(1.0F, -2.8F, material, setOf()) {
    init {
        setRegistryName("forge_hammer")
        unlocalizedName = SmithcraftingMod.MODID + ".forge_hammer"
    }

    override fun canHarvestBlock(blockIn: IBlockState?): Boolean {
        // placeholder
        return false
    }

    @SideOnly(Side.CLIENT)
    fun initModel() {
        ModelLoader.setCustomModelResourceLocation(this, 0, ModelResourceLocation(registryName!!.toString()))
    }
}