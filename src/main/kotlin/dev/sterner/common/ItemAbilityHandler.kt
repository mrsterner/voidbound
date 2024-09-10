package dev.sterner.common

import dev.sterner.VoidBound
import dev.sterner.api.item.ItemAbility
import dev.sterner.client.screen.ItemAbilityScreen
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.player.LocalPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameType
import org.lwjgl.glfw.GLFW

class ItemAbilityHandler {

    var selectionScreen: ItemAbilityScreen? = null
    var currentAbility: ItemAbility? = ItemAbility.NONE
    var active: Boolean = false

    init {
        currentAbility = ItemAbility.NONE

    }

    fun tick() {
        val mc = Minecraft.getInstance()
        if (mc.gameMode != null && mc.gameMode!!.playerMode == GameType.SPECTATOR) {
            if (active) {
                active = false
            }
            return
        }
        val player = mc.player
        if (player != null && selectionScreen == null) {
            selectionScreen = ItemAbilityScreen(player.mainHandItem)
        }
        val stack = player?.mainHandItem
        if (stack == null) {
            active = false
            return
        }
        init(player)
        if (!active) {
            return
        }

        selectionScreen?.update()
    }

    fun render(graphics: GuiGraphics, partialTicks: Float, width: Int, height: Int) {
        if (Minecraft.getInstance().options.hideGui || !active) {
            return
        }

        selectionScreen?.render(graphics, partialTicks)
    }

    private fun init(player: LocalPlayer?) {
        active = true
    }

    fun onKeyInput(key: Int, pressed: Boolean) {
        if (!active) {
            return
        }

        if (key != GLFW.GLFW_KEY_LEFT_ALT) {
            return
        }

        if (pressed && selectionScreen?.focus != true) {
            selectionScreen?.focus = true
        }
        if (!pressed && selectionScreen?.focus == true) {
            selectionScreen!!.focus = false
            selectionScreen!!.onClose()
        }
    }

    fun mouseScrolled(delta: Double): Boolean {
        if (!active) {
            return false
        }

        if (selectionScreen?.focus == true) {
            selectionScreen?.cycle(delta.toInt())
            return true
        }
        return false
    }

}