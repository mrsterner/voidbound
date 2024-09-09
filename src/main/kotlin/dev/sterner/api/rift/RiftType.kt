package dev.sterner.api.rift

import dev.sterner.api.VoidBoundApi
import dev.sterner.common.blockentity.SpiritRiftBlockEntity
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level
import team.lodestar.lodestone.handlers.RenderHandler
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry
import team.lodestar.lodestone.systems.easing.Easing
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder
import team.lodestar.lodestone.systems.particle.data.GenericParticleData
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData
import java.awt.Color

abstract class RiftType {

    private val firstColor = Color(100, 100, 255, 255)
    private val secondColor = Color(200, 100, 255, 255)

    private val firstColorAlpha = Color(100, 100, 255, 25)
    private val secondColorAlpha = Color(200, 100, 255, 25)

    /**
     * Renders the small middle of a Rift, in case the user cant see the shader
     */
    open fun tick(level: Level, blockPos: BlockPos, blockEntity: SpiritRiftBlockEntity) {

        if (level.isClientSide) {
            var transparency = GenericParticleData.create(0.05f, 0.1f).build()
            var color =
                ColorParticleData.create(firstColorAlpha, secondColorAlpha).setEasing(Easing.SINE_IN).setCoefficient(0.75f)
                    .build()
            if (Minecraft.getInstance().player != null) {
                if (VoidBoundApi.hasGoggles()) {
                    transparency = GenericParticleData.create(0.2f, 0.8f).build()
                    color =
                        ColorParticleData.create(firstColor, secondColor).setEasing(Easing.SINE_IN).setCoefficient(0.75f)
                            .build()
                }
            }

            if (level.gameTime % 2L == 0L) {
                WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setRenderTarget(RenderHandler.LATE_DELAYED_RENDER)
                    .setScaleData(GenericParticleData.create(0.2f, 0f).build())
                    .setTransparencyData(transparency)
                    .setColorData(color)
                    .setSpinData(SpinParticleData.create(0f, 0.4f).setEasing(Easing.QUARTIC_IN).build())
                    .setLifetime(20)
                    .enableNoClip()
                    .spawn(level, blockPos.x + 0.5, blockPos.y + 0.5, blockPos.z + 0.5)
            }
        }
    }
}