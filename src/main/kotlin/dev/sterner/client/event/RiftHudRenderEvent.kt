package dev.sterner.client.event

import dev.sterner.VoidBound
import dev.sterner.api.VoidBoundApi
import dev.sterner.api.util.VoidBoundRenderUtils
import dev.sterner.common.blockentity.SpiritRiftBlockEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.LightTexture
import net.minecraft.core.BlockPos
import net.minecraft.world.phys.BlockHitResult
import java.awt.Color


object RiftHudRenderEvent {

    /**
     * Renders the spirits in a rift at crosshair
     */
    fun spiritRiftHud(guiGraphics: GuiGraphics, partialTick: Float) {
        val client: Minecraft = Minecraft.getInstance()

        if (VoidBoundApi.hasGoggles()) {
            if (client.level != null && client.hitResult is BlockHitResult) {
                val result = client.hitResult as BlockHitResult
                val pos: BlockPos = result.blockPos
                if (client.level!!.getBlockEntity(pos) is SpiritRiftBlockEntity) {
                    val spiritRift = client.level!!.getBlockEntity(pos) as SpiritRiftBlockEntity

                    val spiritList = spiritRift.simpleSpiritCharge.getNonEmptyMutableList()

                    if (spiritList.isNotEmpty()) {
                        val poseStack = guiGraphics.pose()
                        poseStack.pushPose()

                        // Adjust for subpixel rendering on odd screen dimensions
                        poseStack.translate(
                            if (client.window.guiScaledWidth % 2 != 0) 0.5 else 0.0,
                            if (client.window.guiScaledHeight % 2 != 0) 0.5 else 0.0,
                            0.0
                        )

                        // Center of the screen
                        val centerX = client.window.guiScaledWidth / 2.0 - 15
                        val centerY =
                            client.window.guiScaledHeight / 2.0 - 15 // Adjusted for your original centerY offset
                        val radius = 16.0 // You can adjust the radius to fit your design

                        // Iterate over the spirits to position them in a circular pattern
                        for ((index, spirit) in spiritList.withIndex()) {
                            val id = spirit.type.identifier

                            // Calculate the angle for this spirit
                            val angle = ((index.toDouble() / spiritList.size) * 2 * Math.PI) - (Math.PI / 2)

                            // Calculate the x and y positions based on the angle and radius
                            val x = centerX + radius * Math.cos(angle)
                            val y = centerY + radius * Math.sin(angle)

                            // Move the matrix stack to the calculated position and draw the spirit icon
                            poseStack.pushPose()
                            poseStack.translate(x, y, 0.0)
                            VoidBoundRenderUtils.drawScreenIcon(
                                poseStack,
                                icon = VoidBound.id("textures/spirit/$id.png")
                            )
                            poseStack.pushPose()
                            poseStack.scale(0.5f, 0.5f, 0.0f)
                            if (spiritRift.infinite) {
                                poseStack.pushPose()
                                poseStack.scale(1.5f, 1.5f, 1.5f)
                                poseStack.translate(8.0, 8.0, 240.0)
                                VoidBoundRenderUtils.drawScreenIcon(
                                    poseStack,
                                    icon = VoidBound.id("textures/gui/infinite_light.png")
                                )
                                poseStack.popPose()
                            } else {
                                Minecraft.getInstance().font.drawInBatch(
                                    spirit.count.toString(),
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
                            }

                            poseStack.popPose()
                            poseStack.popPose()
                        }

                        poseStack.popPose()
                    }
                }
            }
        }
    }
}