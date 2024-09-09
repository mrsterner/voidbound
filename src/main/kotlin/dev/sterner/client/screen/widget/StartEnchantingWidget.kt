package dev.sterner.client.screen.widget

import dev.sterner.VoidBound
import dev.sterner.client.screen.OsmoticEnchanterScreen
import dev.sterner.networking.StartEnchantingPacket
import dev.sterner.registry.VoidBoundPacketRegistry
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component

class StartEnchantingWidget(private var screen: OsmoticEnchanterScreen, x: Int, y: Int) :
    AbstractWidget(x, y, 22, 14, Component.empty()) {

    override fun onClick(mouseX: Double, mouseY: Double) {
        if (screen.menu.osmoticEnchanter?.activated == true) {
            return
        }

        VoidBoundPacketRegistry.VOID_BOUND_CHANNEL.sendToServer(StartEnchantingPacket(screen.menu.pos))
        super.onClick(mouseX, mouseY)
    }

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {

        var icon = VoidBound.id("textures/gui/enchanter_check.png")
        if (screen.menu.osmoticEnchanter?.activated == false) {
            icon = VoidBound.id("textures/gui/enchanter_check_glowing.png")
        }

        guiGraphics.blit(icon, x, y, 0f, 0f, width, height, width, height)
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {

    }
}