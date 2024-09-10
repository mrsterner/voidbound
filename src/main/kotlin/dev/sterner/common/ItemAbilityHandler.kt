package dev.sterner.common

import dev.sterner.api.item.ItemAbility
import dev.sterner.client.screen.ItemAbilityScreen
import net.minecraft.client.Minecraft
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameType
import org.lwjgl.glfw.GLFW

class ItemAbilityHandler {

    var selectionScreen: ItemAbilityScreen = ItemAbilityScreen(ItemStack.EMPTY)
    var currentAbility: ItemAbility? = ItemAbility.NONE
    var active: Boolean = false


    fun tick() {
        val mc = Minecraft.getInstance()
        if (mc.gameMode != null && mc.gameMode!!.playerMode == GameType.SPECTATOR) {
            if (active) {
                active = false
            }
            return
        }
        val player = mc.player
        val stack = player!!.mainHandItem
        if (stack == null) {
            active = false
            return
        }
        if (!active) {
            return
        }

        selectionScreen.update()
    }

    fun onKeyInput(key: Int, pressed: Boolean) {
        if (!active) {
            return
        }
        if (key != GLFW.GLFW_KEY_LEFT_ALT) {
            return
        }

        if (pressed && !selectionScreen.focus) {
            selectionScreen.focus = true
        }
        if (!pressed && selectionScreen.focus) {
            selectionScreen.focus = false
            selectionScreen.onClose()
        }
    }

    fun mouseScrolled(delta: Double): Boolean {
        if (!active) {
            return false
        }

        if (selectionScreen.focus) {
            selectionScreen.cycle(delta.toInt())
            return true
        }
        return false
    }

}