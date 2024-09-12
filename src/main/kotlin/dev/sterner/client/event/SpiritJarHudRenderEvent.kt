package dev.sterner.client.event

import com.sammy.malum.common.block.curiosities.spirit_altar.SpiritAltarBlockEntity
import com.sammy.malum.common.block.storage.jar.SpiritJarBlockEntity
import com.sammy.malum.common.recipe.SpiritInfusionRecipe
import dev.sterner.VoidBound
import dev.sterner.api.util.VoidBoundPlayerUtils
import dev.sterner.api.util.VoidBoundRenderUtils
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.LightTexture
import net.minecraft.core.BlockPos
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.phys.BlockHitResult
import java.awt.Color

object SpiritJarHudRenderEvent {

    fun spiritJarHud(guiGraphics: GuiGraphics, partialTick: Float) {
        val client: Minecraft = Minecraft.getInstance()

        if (VoidBoundPlayerUtils.hasGoggles()) {
            if (client.level != null && client.hitResult is BlockHitResult) {
                val result = client.hitResult as BlockHitResult
                val pos: BlockPos = result.blockPos
                if (client.level!!.getBlockEntity(pos) is SpiritJarBlockEntity) {
                    val jar = client.level!!.getBlockEntity(pos) as SpiritJarBlockEntity

                    val spirit = jar.type
                    val count = jar.count

                    if (count > 0 && spirit != null) {
                        val poseStack = guiGraphics.pose()
                        poseStack.pushPose()
                        poseStack.translate(
                            if (client.window.guiScaledWidth % 2 != 0) 0.5 else 0.0,
                            if (client.window.guiScaledHeight % 2 != 0) 0.5 else 0.0,
                            0.0
                        )
                        val startX: Int = (client.window.guiScaledWidth / 2) - (16 * 5)
                        val centerY: Int = (client.window.guiScaledHeight / 2) - (16 * 3)
                        val id = spirit.identifier
                        poseStack.scale(1.5f, 1.5f, 1f)
                        poseStack.translate(startX.toDouble(), centerY.toDouble(), 0.0)

                        VoidBoundRenderUtils.drawScreenIcon(
                            poseStack,
                            icon = VoidBound.id("textures/spirit/$id.png")
                        )
                        poseStack.scale(0.4f, 0.4f, 0.0f)
                        poseStack.translate(8.0, 8.0, 0.0)
                        Minecraft.getInstance().font.drawInBatch(
                            count.toString(),
                            32f, 32f,
                            Color(255, 255, 255).rgb,
                            true,
                            poseStack.last().pose(),
                            guiGraphics.bufferSource(),
                            Font.DisplayMode.NORMAL,
                            0,
                            LightTexture.FULL_BRIGHT,
                            true
                        )
                        poseStack.translate(16.0, 0.0, 0.0)
                        poseStack.popPose()
                    }
                }
            }
        }
    }
}