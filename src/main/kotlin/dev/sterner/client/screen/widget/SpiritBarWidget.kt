package dev.sterner.client.screen.widget

import com.mojang.blaze3d.systems.RenderSystem
import com.sammy.malum.core.systems.spirit.MalumSpiritType
import dev.sterner.VoidBound
import dev.sterner.api.rift.SimpleSpiritCharge
import dev.sterner.api.util.VoidBoundRenderUtils
import dev.sterner.client.screen.OsmoticEnchanterScreen
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.components.Tooltip
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.client.renderer.ShaderInstance
import net.minecraft.network.chat.Component
import net.minecraft.network.chat.Style
import net.minecraft.util.Mth
import org.lwjgl.opengl.GL11
import team.lodestar.lodestone.helpers.RenderHelper
import team.lodestar.lodestone.registry.client.LodestoneShaderRegistry
import team.lodestar.lodestone.systems.rendering.VFXBuilders
import team.lodestar.lodestone.systems.rendering.shader.ExtendedShaderInstance
import java.awt.Color
import java.util.function.Supplier

class SpiritBarWidget(private var screen: OsmoticEnchanterScreen, x: Int, y: Int) : AbstractWidget(
    x, y, 8, 45,
    Component.empty()
) {

    var isScry: Boolean = false
    var spiritType: MalumSpiritType? = null
    private val icon = VoidBound.id("textures/gui/spirit_bar.png")

    override fun renderWidget(guiGraphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        if (spiritType != null) {
            val targetSpirits = screen.menu.osmoticEnchanter?.spiritsToConsume
            val consumedSpirits = screen.menu.osmoticEnchanter?.consumedSpirits

            // Calculate the normalized value for the spirit bar fill level
            val normalizer = if (isScry) calcNormal(targetSpirits) else calcNormal(consumedSpirits)

            // Full height of the bar (when completely filled)
            val maxBarHeight = height

            // Calculate the height of the filled portion of the bar
            val fillHeight: Float = (maxBarHeight * normalizer)

            // Calculate the Y position for the top of the filled portion
            val adjustedY = y + (maxBarHeight - fillHeight)

            val minV = 1f - (fillHeight / height.toFloat())

            val shaderInstance = LodestoneShaderRegistry.DISTORTED_TEXTURE.instance.get() as ExtendedShaderInstance
            if (isScry) {
                shaderInstance.safeGetUniform("YFrequency").set(1f)
                shaderInstance.safeGetUniform("XFrequency").set(1f)
                shaderInstance.safeGetUniform("Speed").set(1f)
                shaderInstance.safeGetUniform("Intensity").set(1f)
            }

            val shaderInstanceSupplier = Supplier<ShaderInstance> { shaderInstance }

            val builder = VFXBuilders.createScreen()
                .setPosColorTexLightmapDefaultFormat()
                .setShader(shaderInstanceSupplier)
                .setAlpha(if (isScry) 0.75f else 1f)
                .setColor(spiritType!!.primaryColor.brighter())
                .setLight(RenderHelper.FULL_BRIGHT)

            RenderSystem.blendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE)

            // Render the texture with adjusted UV for top cropping
            VoidBoundRenderUtils.drawScreenIcon(
                icon,                         // ResourceLocation
                guiGraphics.pose(),            // PoseStack
                builder,                       // VFXBuilders.ScreenVFXBuilder
                x,                             // X position
                adjustedY,                     // Adjusted Y position (moves upwards)
                0f,                          // U (texture starting position)
                minV,                          // **Adjusted minV for top cropping**
                width.toFloat(),                         // Bar width
                fillHeight,                    // Filled height
                width,                         // Full texture width
                height                         // Full texture height
            )

            shaderInstance.setUniformDefaults()
            RenderSystem.defaultBlendFunc()

            // Tooltip logic: show the tooltip if the mouse is over the bar
            if (mouseX >= x && mouseX < x + width && mouseY >= y && mouseY < y + maxBarHeight) {
                val name = Component.translatable(spiritType!!.identifier.replaceFirstChar { it.uppercase() })

                val targetCount = targetSpirits?.getChargeForType(spiritType!!)
                val consumedCount = consumedSpirits?.getChargeForType(spiritType!!)

                val count = Component.translatable("\n$consumedCount / $targetCount")
                    .setStyle(Style.EMPTY.withColor(Color.LIGHT_GRAY.rgb))

                tooltip = Tooltip.create(name.append(count))
            }
        }
    }


    private fun calcNormal(targetSpirits: SimpleSpiritCharge?): Float {
        var normalizer = 0f
        if (targetSpirits != null) {
            normalizer = targetSpirits.getChargeForType(spiritType!!) / screen.maxSpiritCharge.toFloat()
            normalizer = Mth.clamp(normalizer, 0.0f, 1.0f)
        }
        return normalizer
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) {

    }
}