package dev.sterner.common.item.focus

import com.sammy.malum.visual_effects.ScreenParticleEffects
import net.minecraft.client.Minecraft
import net.minecraft.util.Mth
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import team.lodestar.lodestone.handlers.screenparticle.ParticleEmitterHandler.ItemParticleSupplier
import team.lodestar.lodestone.registry.common.particle.LodestoneScreenParticleRegistry
import team.lodestar.lodestone.systems.easing.Easing
import team.lodestar.lodestone.systems.particle.builder.ScreenParticleBuilder
import team.lodestar.lodestone.systems.particle.data.GenericParticleData
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData
import team.lodestar.lodestone.systems.particle.data.spin.SpinParticleData
import team.lodestar.lodestone.systems.particle.render_types.LodestoneScreenParticleRenderType
import team.lodestar.lodestone.systems.particle.screen.ScreenParticleHolder
import java.awt.Color

abstract class AbstractFocusItem(properties: Properties) : Item(properties),
    ItemParticleSupplier {

    abstract fun color(): Color

    abstract fun endColor(): Color

    open fun isVoid(): Boolean {
        return false
    }

    override fun spawnEarlyParticles(
        target: ScreenParticleHolder?,
        level: Level,
        partialTick: Float,
        stack: ItemStack?,
        x: Float,
        y: Float
    ) {
        if (isVoid()) {
            ScreenParticleEffects.spawnVoidItemScreenParticles(target, level, 1f, partialTick)
        } else {
            spawnLightItemScreenParticles(target, level, 1f, partialTick)
        }
    }

    private fun spawnLightItemScreenParticles(
        target: ScreenParticleHolder?,
        level: Level,
        intensity: Float,
        partialTick: Float
    ) {
        val timeMultiplier = Mth.nextFloat(level.random, 0.9f, 1.4f)

        val gameTime = level.gameTime + partialTick
        val rand = Minecraft.getInstance().level!!.getRandom()
        val spinParticleData =
            SpinParticleData.createRandomDirection(rand, 0f, (if (level.random.nextBoolean()) 1 else -2).toFloat())
                .setSpinOffset(0.025f * gameTime % 6.28f).setEasing(Easing.EXPO_IN_OUT).build()
        ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.STAR, target)
            .setScaleData(
                GenericParticleData.create(1.2f * intensity + rand.nextFloat() * 0.1f * intensity, 0f)
                    .setEasing(Easing.SINE_IN_OUT, Easing.BOUNCE_IN_OUT).build()
            )
            .setTransparencyData(GenericParticleData.create(0.1f, 0.6f, 0f).setEasing(Easing.SINE_IN_OUT).build())
            .setColorData(ColorParticleData.create(color(), endColor()).setCoefficient(2f).build())
            .setSpinData(spinParticleData)
            .setLifetime(((10 + rand.nextInt(10)) * timeMultiplier).toInt())
            .setRandomOffset(0.05)
            .setRandomMotion(0.05, 0.05)
            .setRenderType(LodestoneScreenParticleRenderType.ADDITIVE)
            .spawnOnStack(0.0, 0.0)

        ScreenParticleBuilder.create(LodestoneScreenParticleRegistry.WISP, target)
            .setScaleData(
                GenericParticleData.create(0.8f * intensity + rand.nextFloat() * 0.6f * intensity, 0f)
                    .setEasing(Easing.EXPO_OUT).build()
            )
            .setTransparencyData(GenericParticleData.create(0.1f, 0.2f, 0f).setEasing(Easing.SINE_IN_OUT).build())
            .setColorData(ColorParticleData.create(color(), endColor().darker()).setCoefficient(1.25f).build())
            .setSpinData(spinParticleData)
            .setLifetime(20 + rand.nextInt(8))
            .setRandomOffset(0.1)
            .setRandomMotion(0.4, 0.4)
            .setRenderType(LodestoneScreenParticleRenderType.ADDITIVE)
            .spawnOnStack(0.0, 0.0)
            .setLifetime(((10 + rand.nextInt(2)) * timeMultiplier).toInt())
            .setSpinData(SpinParticleData.create(Mth.nextFloat(rand, 0.05f, 0.1f)).build())
            .setScaleData(GenericParticleData.create(0.8f + rand.nextFloat() * 0.4f, 0f).build())
            .setRandomMotion(0.01, 0.01)
            .spawnOnStack(0.0, 0.0)
    }
}