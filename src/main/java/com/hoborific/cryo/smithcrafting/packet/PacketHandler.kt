package com.hoborific.cryo.smithcrafting.packet

import net.minecraftforge.fml.common.network.NetworkRegistry
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper
import net.minecraftforge.fml.relauncher.Side


object PacketHandler {
    var networkInstance: SimpleNetworkWrapper? = null

    private var packetId = 0

    fun nextID(): Int {
        return packetId++
    }

    fun registerMessages(channelName: String) {
        networkInstance = NetworkRegistry.INSTANCE.newSimpleChannel(channelName)
        registerMessages()
    }

    fun registerMessages() {
        networkInstance!!.registerMessage(
            PacketAnvilInteraction.Handler::class.java,
            PacketAnvilInteraction::class.java,
            nextID(),
            Side.SERVER
        )
    }
}