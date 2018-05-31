package com.hoborific.cryo.frogsforging.commands

import com.hoborific.cryo.frogsforging.util.setSmithedToolQuality
import net.minecraft.command.CommandBase
import net.minecraft.command.ICommandSender
import net.minecraft.entity.player.EntityPlayer
import net.minecraft.server.MinecraftServer
import net.minecraft.util.text.TextComponentString
import net.minecraft.util.text.TextFormatting

class CommandSetToolQuality : CommandBase() {
    override fun getName(): String {
        return "settq"
    }

    override fun getUsage(sender: ICommandSender?): String {
        return "settq [tool quality : int]"
    }

    override fun execute(server: MinecraftServer?, sender: ICommandSender?, args: Array<out String>?) {
        if (sender !is EntityPlayer) return
        if (args == null || args.isEmpty()) return

        val toolExp = args[0].toIntOrNull() ?: return

        if (!setSmithedToolQuality(sender.heldItemMainhand, toolExp)) {
            sender.sendMessage(TextComponentString(TextFormatting.RED.toString() + "Invalid held item."))
        }
    }
}