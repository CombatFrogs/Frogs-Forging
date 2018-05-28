package com.hoborific.cryo.smithcrafting.packet

import com.hoborific.cryo.smithcrafting.container.ContainerForgeAnvil
import io.netty.buffer.ByteBuf
import net.minecraftforge.fml.common.FMLCommonHandler
import net.minecraftforge.fml.common.network.simpleimpl.IMessage
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext

class PacketAnvilInteraction(windowId: Int, buttonId: Int) : IMessage {
    var windowId: Int = windowId
        private set
    var buttonId: Int = buttonId
        private set

    constructor() : this(0, 0)

    override fun fromBytes(buf: ByteBuf?) {
        buf ?: return
        windowId = buf.readInt()
        buttonId = buf.readInt()
    }

    override fun toBytes(buf: ByteBuf?) {
        buf ?: return
        buf.writeInt(windowId)
        buf.writeInt(buttonId)
    }

    class Handler : IMessageHandler<PacketAnvilInteraction, IMessage> {
        override fun onMessage(message: PacketAnvilInteraction?, ctx: MessageContext?): IMessage? {
            message ?: return null
            ctx ?: return null

            FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask { handle(message, ctx) }
            return null
        }

        private fun handle(message: PacketAnvilInteraction, ctx: MessageContext) {
            val playerEntity = ctx.serverHandler.player

            val openContainer = playerEntity.openContainer ?: return
            if (openContainer.windowId == message.windowId &&
                openContainer.getCanCraft(playerEntity) &&
                !playerEntity.isSpectator
            ) {
                if (openContainer !is ContainerForgeAnvil) return

                openContainer.handleAnvilButtonPressed(message.buttonId)
                openContainer.detectAndSendChanges()
            }
        }
    }
}