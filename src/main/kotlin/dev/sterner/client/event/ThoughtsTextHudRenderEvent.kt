package dev.sterner.client.event

import com.mojang.blaze3d.systems.RenderSystem
import dev.sterner.registry.VoidBoundComponentRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.LightTexture
import net.minecraft.network.chat.Component
import org.joml.Matrix4f
import java.awt.Color

object ThoughtsTextHudRenderEvent {

    fun renderThoughts(guiGraphics: GuiGraphics, partialTick: Float) {
        val minecraft = Minecraft.getInstance()
        if (minecraft.player == null) {
            return
        }

        val comp = VoidBoundComponentRegistry.VOID_BOUND_REVELATION_COMPONENT.get(minecraft.player!!)
        if (comp.thoughtsQueue.isEmpty()) {
            return
        }

        val thoughtsQueue = comp.thoughtsQueue
        val font = minecraft.font
        val window = minecraft.window
        val baseX = window.guiScaledWidth - 120f // Right margin
        var baseY = window.guiScaledHeight - 30f // Bottom margin
        val maxWidth = 220 // Set a max width for wrapping

        // Render each thought
        thoughtsQueue.entries.forEachIndexed { _, entry ->
            val thought = entry.key
            val durationTicks = entry.value.duration

            // Calculate opacity for fade-out effect (last 20 ticks, i.e., 1 second)
            val opacity = if (durationTicks > 20) 255 else (durationTicks / 20.0 * 255).toInt().coerceAtLeast(0)
            if (entry.value.delay > 0) {
                return
            }

            // Wrap the text to fit within maxWidth
            val wrappedLines = wrapText(font, thought, maxWidth)

            // Draw each wrapped line
            for ((index, line) in wrappedLines.withIndex()) {
                drawTextWithShadow(
                    guiGraphics,
                    font,
                    line,
                    baseX.toFloat(), // Align text to the right
                    baseY - (wrappedLines.size - index - 1) * (font.lineHeight + 1).toFloat(), // Adjust Y position for each wrapped line
                    Color(200, 50, 200, opacity).rgb
                )
            }

            // Adjust the baseY for the next thought based on the number of lines
            baseY -= wrappedLines.size * (font.lineHeight + 1).toFloat()
        }
    }


    private fun drawTextWithShadow(
        guiGraphics: GuiGraphics,
        font: Font,
        text: Component,
        x: Float,
        y: Float,
        color: Int
    ) {

        val poseStack = guiGraphics.pose()
        val scale = 0.5f
        poseStack.pushPose() // Save the current state of the pose stack
        poseStack.scale(scale, scale, scale) // Scale down the text to half size

        // Adjust the position to account for the scale
        val scaledX = x * (1 / scale) // Or x * 1.3333f
        val scaledY = y * (1 / scale) // Or y * 1.3333f

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()

        font.drawInBatch(
            text,
            scaledX,
            scaledY,
            color,
            true, // Drop shadow
            Matrix4f(guiGraphics.pose().last().pose()),
            guiGraphics.bufferSource(),
            Font.DisplayMode.NORMAL,
            0, // Background color
            LightTexture.FULL_BRIGHT
        )

        RenderSystem.disableBlend()
        poseStack.popPose()
    }

    private fun wrapText(font: Font, text: Component, maxWidth: Int): List<Component> {
        val lines = mutableListOf<Component>()
        val words = text.string.split(" ")

        var currentLine = StringBuilder()
        for (word in words) {
            val testLine = if (currentLine.isEmpty()) word else "${currentLine} $word"
            if (font.width(Component.literal(testLine)) > maxWidth) {
                lines.add(Component.literal(currentLine.toString()))
                currentLine = StringBuilder(word)
            } else {
                if (currentLine.isNotEmpty()) currentLine.append(" ")
                currentLine.append(word)
            }
        }
        lines.add(Component.literal(currentLine.toString()))
        return lines
    }
}