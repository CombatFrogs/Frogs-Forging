package com.hoborific.cryo.frogsforging.proxy

import com.hoborific.cryo.frogsforging.registry.ModItemRegistry
import net.minecraftforge.client.event.ModelRegistryEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.relauncher.Side

@Mod.EventBusSubscriber(Side.CLIENT)
class ClientProxy : CommonProxy() {

    companion object {

        @JvmStatic
        @SubscribeEvent
        fun registerModels(unusedEvent: ModelRegistryEvent) {
            ModItemRegistry.initModels()
        }
    }
}
