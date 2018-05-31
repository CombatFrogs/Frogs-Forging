package com.hoborific.cryo.frogsforging.events

import com.hoborific.cryo.frogsforging.FrogsForgingMod
import com.hoborific.cryo.frogsforging.util.getSmithedToolQuality
import net.minecraftforge.event.entity.player.PlayerEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent

@Mod.EventBusSubscriber(modid = FrogsForgingMod.MODID)
object EntityPlayerEventHandlers {

    private val MAX_EXP_EFFICIENCY_BONUS = 17f

    @JvmStatic
    @SubscribeEvent
    fun clonePlayer(event: PlayerEvent.Clone) {
        System.out.println("Firing an event from Kotlin!")
    }

    @JvmStatic
    @SubscribeEvent
    fun breakSpeed(breakSpeedEvent: PlayerEvent.BreakSpeed) {
        val itemStack = breakSpeedEvent.entityPlayer.heldItemMainhand

        val originalEfficiency = breakSpeedEvent.originalSpeed
        val toolQuality = getSmithedToolQuality(itemStack)
        val additionalSpeed = MAX_EXP_EFFICIENCY_BONUS * (toolQuality / 250f)

        breakSpeedEvent.newSpeed = originalEfficiency + additionalSpeed
    }
}