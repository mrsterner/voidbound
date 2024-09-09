package dev.sterner.client.screen

import dev.sterner.VoidBound
import dev.sterner.client.screen.widget.SelectFocusWidget
import dev.sterner.common.item.WandItem
import net.minecraft.client.GameNarrator
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Player

class FocusSelectionScreen(title: Component) : Screen(title) {

    var player: Player? = null

    private val backgroundTexture: ResourceLocation = VoidBound.id("textures/gui/focus_screen.png")
    private var imageWidth: Int = 113
    private var imageHeight: Int = 62

    constructor(player: Player) : this(GameNarrator.NO_TITLE) {
        this.player = player
    }

    override fun init() {
        super.init()

        val xInMenu = (this.width - this.imageWidth) / 2
        val yInMenu = (this.height - this.imageHeight) / 2

        val mainItem = player?.mainHandItem
        if (mainItem?.item is WandItem) {
            val wandItem = mainItem.item as WandItem
            val content = wandItem.getContents(mainItem)
            for ((index, focus) in content.toList().withIndex()) {
                val focusWidget = if (index < 6) {
                    SelectFocusWidget(this, xInMenu + 16 * index + 8, yInMenu + 8, 16, 16)
                } else {
                    SelectFocusWidget(this, xInMenu + 16 * index + 8, yInMenu + 8 + 16, 16, 16)
                }
                focusWidget.focus = focus
                this.addRenderableWidget(focusWidget)
            }
        }
    }

    override fun render(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        val k = (this.width - this.imageWidth) / 2
        val l = (this.height - this.imageHeight) / 2
        guiGraphics.blit(
            backgroundTexture,
            k,
            l,
            this.imageWidth,
            this.imageHeight,
            0f,
            0f,
            this.imageWidth,
            this.imageHeight,
            this.imageWidth,
            this.imageHeight
        )
        super.render(guiGraphics, mouseX, mouseY, partialTick)
    }

    override fun isPauseScreen(): Boolean {
        return false
    }
}