package dev.sterner.common.blockentity

import com.sammy.malum.visual_effects.SpiritLightSpecs
import dev.sterner.registry.VoidBoundBlockEntityTypeRegistry
import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import team.lodestar.lodestone.handlers.RenderHandler
import team.lodestar.lodestone.helpers.RandomHelper
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry
import team.lodestar.lodestone.systems.easing.Easing
import team.lodestar.lodestone.systems.particle.ParticleEffectSpawner
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder
import team.lodestar.lodestone.systems.particle.data.GenericParticleData
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData
import java.awt.Color

class SpiritStabilizerBlockEntity(pos: BlockPos, blockState: BlockState) : BlockEntity(
    VoidBoundBlockEntityTypeRegistry.SPIRIT_BINDER_STABILIZER.get(), pos,
    blockState
) {

    private val firstColor = Color(250, 10, 255, 255)
    private val secondColor = Color(100, 100, 255, 255)

    val x = (worldPosition.x.toFloat() + 0.5f).toDouble()
    val y = (worldPosition.y.toFloat() + 0.2f).toDouble()
    val z = (worldPosition.z.toFloat() + 0.5f).toDouble()

    fun tick() {
        if (level != null && level!!.isClientSide) {


            if (level!!.gameTime % 8L == 0L) {

                val random = level!!.random
                val colorData = ColorParticleData.create(firstColor, secondColor).setCoefficient(1.5f)
                    .setEasing(Easing.BOUNCE_IN_OUT).build()

                val lifeTime = RandomHelper.randomBetween(random, 40, 60)
                val scale = RandomHelper.randomBetween(random, 0.3f, 0.4f)

                val lightSpecs: ParticleEffectSpawner =
                    SpiritLightSpecs.spiritLightSpecs(this.level, Vec3(x, y, z), colorData)
                lightSpecs.builder.setRenderTarget(RenderHandler.LATE_DELAYED_RENDER).setLifetime(lifeTime)
                    .setScaleData(GenericParticleData.create(scale, 0.0f).setEasing(Easing.SINE_IN_OUT).build())
                    .setTransparencyData(GenericParticleData.create(0.3f, 0.6f, 0.0f).build())
                    .addMotion(0.0, -(scale * 0.05f).toDouble(), 0.0)
                lightSpecs.spawnParticlesRaw()
            }

            if (level!!.gameTime % 4L == 0L) {

                val random = level!!.random
                val colorData = ColorParticleData.create(firstColor, secondColor).setCoefficient(1.5f)
                    .setEasing(Easing.BOUNCE_IN_OUT).build()

                var lifeTime = RandomHelper.randomBetween(random, 10, 15)
                var scale = RandomHelper.randomBetween(random, 0.02f, 0.03f)

                WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setRenderTarget(RenderHandler.LATE_DELAYED_RENDER)
                    .setScaleData(GenericParticleData.create(scale, 0.0f).setEasing(Easing.SINE_IN).build())
                    .setTransparencyData(
                        GenericParticleData.create(0.4f, 0.8f, 0.2f).setEasing(Easing.QUAD_OUT).build()
                    )
                    .setColorData(colorData).setSpinData(
                        SpinParticleData.create(0.2f, 0.4f).setSpinOffset(level!!.gameTime.toFloat() * 0.2f % 6.28f)
                            .setEasing(Easing.QUARTIC_IN).build()
                    )
                    .setLifetime(lifeTime)
                    .addMotion(0.0, -(scale * 1.5f).toDouble(), 0.0)
                    .enableNoClip()
                    .spawn(this.level, x, y, z)


                lifeTime = 20
                scale = 0.16f
                WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setRenderTarget(RenderHandler.LATE_DELAYED_RENDER)
                    .setScaleData(GenericParticleData.create(scale, 0.0f).build())
                    .setTransparencyData(GenericParticleData.create(0.2f, 0.8f).build())
                    .setColorData(
                        ColorParticleData.create(firstColor, secondColor).setEasing(Easing.SINE_IN).setCoefficient(0.5f)
                            .build()
                    )
                    .setSpinData(SpinParticleData.create(0.0f, 0.4f).setEasing(Easing.QUARTIC_IN).build())
                    .setLifetime(lifeTime)
                    .enableNoClip()
                    .spawn(this.level, x, y, z)

                scale = 0.10f
                WorldParticleBuilder.create(LodestoneParticleRegistry.WISP_PARTICLE)
                    .setRenderTarget(RenderHandler.LATE_DELAYED_RENDER)
                    .setScaleData(GenericParticleData.create(scale, 0.0f).build())
                    .setTransparencyData(GenericParticleData.create(0.2f, 0.8f).build())
                    .setColorData(
                        ColorParticleData.create(Color(255, 255, 255), Color(255, 255, 255)).setEasing(Easing.SINE_IN)
                            .setCoefficient(0.5f)
                            .build()
                    )
                    .setSpinData(SpinParticleData.create(0.0f, 0.4f).setEasing(Easing.QUARTIC_IN).build())
                    .setLifetime(lifeTime)
                    .enableNoClip()
                    .spawn(this.level, x, y, z)
            }
        }
    }
}