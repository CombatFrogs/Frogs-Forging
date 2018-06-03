package com.hoborific.cryo.frogsforging.proxy

import com.hoborific.cryo.frogsforging.FrogsForgingMod
import com.hoborific.cryo.frogsforging.anvil.BlockImprovedAnvil
import com.hoborific.cryo.frogsforging.blocks.BlockForgeAnvil
import com.hoborific.cryo.frogsforging.items.ItemForgeHammer
import com.hoborific.cryo.frogsforging.items.ItemSmithedPickaxe
import com.hoborific.cryo.frogsforging.packet.PacketHandler
import com.hoborific.cryo.frogsforging.registry.ModBlockRegistry
import com.hoborific.cryo.frogsforging.tileentities.TileEntityForgeAnvil
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraft.item.ItemBlock
import net.minecraft.util.ResourceLocation
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.registry.GameRegistry

@Mod.EventBusSubscriber
open class CommonProxy {
    open fun preInit(e: FMLPreInitializationEvent) {
        PacketHandler.registerMessages(FrogsForgingMod.MODID)
    }

    fun init(e: FMLInitializationEvent) {
        NetworkRegistry.INSTANCE.registerGuiHandler(
            FrogsForgingMod.instance,
            GuiProxy()
        )
    }

    fun postInit(e: FMLPostInitializationEvent) {}

    companion object {
        @JvmStatic
        @SubscribeEvent
        fun registerItems(event: RegistryEvent.Register<Item>) {
            event.registry.register(ItemSmithedPickaxe(Item.ToolMaterial.IRON))
            event.registry.register(ItemForgeHammer(Item.ToolMaterial.IRON))
            event.registry
                .register(
                    ItemBlock(ModBlockRegistry.blockForgeAnvil)
                        .setRegistryName(ModBlockRegistry.blockForgeAnvil!!.registryName)
                )
        }

        @JvmStatic
        @SubscribeEvent
        fun registerBlocks(event: RegistryEvent.Register<Block>) {
            event.registry.register(BlockForgeAnvil())
            event.registry.register(BlockImprovedAnvil())
            GameRegistry.registerTileEntity(
                TileEntityForgeAnvil::class.java,
                ResourceLocation(FrogsForgingMod.MODID, "forge_anvil")
            )
        }
    }
}
