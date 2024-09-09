package dev.sterner.client.screen.widget

import dev.sterner.client.screen.FocusSelectionScreen
import dev.sterner.common.item.WandItem
import dev.sterner.networking.SelectFocusPacket
import dev.sterner.registry.VoidBoundPacketRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

class SelectFocusWidget(
    var screen: FocusSelectionScreen, x: Int, y: Int, width: Int, height: Int
) : AbstractWidget(x, y, width, height, Component.empty()) {

    var focus: ItemStack? = null

    override fun onClick(mouseX: Double, mouseY: Double) {

        var mainHandItem = screen.player?.mainHandItem
        if (mainHandItem?.item is WandItem && focus != null) {
            val wandItem = mainHandItem.item as WandItem
            wandItem.updateSelectedFocus(mainHandItem, focus!!)
            // wandItem.bindFocus(mainHandItem, focus!!)
            VoidBoundPacketRegistry.VOID_BOUND_CHANNEL.sendToServer(SelectFocusPacket(screen.player!!.uuid, focus!!))
        }
        Minecraft.getInstance().setScreen(null)


        super.onClick(mouseX, mouseY)
    }

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        if (focus != null) {
            guiGraphics.renderItem(focus!!, x, y)
        }
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {

    }
}