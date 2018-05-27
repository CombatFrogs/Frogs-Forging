package com.hoborific.cryo.smithcrafting

import com.hoborific.cryo.smithcrafting.commands.CommandSetToolQuality
import com.hoborific.cryo.smithcrafting.config.ConfigCustomRecipes
import com.hoborific.cryo.smithcrafting.proxy.CommonProxy
import com.hoborific.cryo.smithcrafting.registry.SmithingRegistry
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.SidedProxy
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent
import net.minecraftforge.fml.common.event.FMLServerStartingEvent
import org.apache.logging.log4j.Logger

@Mod(
    modid = SmithcraftingMod.MODID,
    name = SmithcraftingMod.MODNAME,
    version = SmithcraftingMod.VERSION,
    useMetadata = true
)
class SmithcraftingMod {

    @Mod.EventHandler
    fun preInit(e: FMLPreInitializationEvent) {
        logger = e.modLog

        proxy!!.preInit(e)

        println("Test Mod Please Ignore")

        ConfigCustomRecipes.instance.initializeRecipes(e)
    }

    @Mod.EventHandler
    fun init(e: FMLInitializationEvent) {
        proxy!!.init(e)
    }

    @Mod.EventHandler
    fun postInit(e: FMLPostInitializationEvent) {
        proxy!!.postInit(e)
    }

    @Mod.EventHandler
    fun serverLoad(event: FMLServerStartingEvent) {
        event.registerServerCommand(CommandSetToolQuality())
    }

    companion object {

        const val MODID = "smithcrafting"
        const val MODNAME = "SmithCrafting"
        const val VERSION = "0.0.1"

        @SidedProxy(
            clientSide = "com.hoborific.cryo.smithcrafting.proxy.ClientProxy",
            serverSide = "com.hoborific.cryo.smithcrafting.proxy.ServerProxy"
        )
        var proxy: CommonProxy? = null

        @Mod.Instance
        var instance: SmithcraftingMod? = null

        var logger: Logger? = null
            private set

        val smithingRegistry = SmithingRegistry()
    }
}