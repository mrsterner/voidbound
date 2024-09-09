package dev.sterner.api

import dev.sterner.common.item.GrimBookItem
import net.minecraft.client.Minecraft


object ClientTickHandler {

    var ticksWithGrimcultOpen: Int = 0
    var ticksInGame: Int = 0
    private var partialTicks: Float = 0f

    fun total(): Float {
        return ticksInGame + partialTicks
    }

    fun renderTick(renderTickTime: Float) {
        partialTicks = renderTickTime
    }

    fun clientTickEnd(mc: Minecraft) {
        if (!mc.isPaused) {
            ticksInGame++
            partialTicks = 0f
        }

        val ticksToOpen = 10
        if (GrimBookItem.isOpen()) {
            if (ticksWithGrimcultOpen < 0) {
                ticksWithGrimcultOpen = 0
            }
            if (ticksWithGrimcultOpen < ticksToOpen) {
                ticksWithGrimcultOpen++
            }
        } else {
            if (ticksWithGrimcultOpen > 0) {
                if (ticksWithGrimcultOpen > ticksToOpen) {
                    ticksWithGrimcultOpen = ticksToOpen
                }
                ticksWithGrimcultOpen--
            }
        }
    }
}