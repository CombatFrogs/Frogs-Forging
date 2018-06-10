package com.hoborific.cryo.frogsforging.proxy

import com.hoborific.cryo.frogsforging.anvil.BlockForgeAnvil
import com.hoborific.cryo.frogsforging.anvil.PacketAnvilTechnique
import com.hoborific.cryo.frogsforging.items.ItemForgeHammer
import com.hoborific.cryo.frogsforging.items.ItemSmithedPickaxe
import com.teamwizardry.librarianlib.features.network.PacketHandler
import net.minecraft.block.Block
import net.minecraft.item.Item
import net.minecraftforge.event.RegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side

@Mod.EventBusSubscriber
open class CommonProxy {
    open fun preInit(e: FMLPreInitializationEvent) {
    }

    fun init(unusedEvent: FMLInitializationEvent) {
        PacketHandler.register(
            PacketAnvilTechnique::class.java,
            Side.SERVER
        )
    }

    fun postInit(unusedEvent: FMLPostInitializationEvent) {}

    companion object {
        @JvmStatic
        @SubscribeEvent
        fun registerItems(event: RegistryEvent.Register<Item>) {
            event.registry.register(ItemSmithedPickaxe(Item.ToolMaterial.IRON))
            event.registry.register(ItemForgeHammer(Item.ToolMaterial.IRON))
        }

        @JvmStatic
        @SubscribeEvent
        fun registerBlocks(event: RegistryEvent.Register<Block>) {
            event.registry.register(BlockForgeAnvil())
        }
    }
}
