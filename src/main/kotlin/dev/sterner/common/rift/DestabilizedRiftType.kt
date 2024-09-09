package dev.sterner.common.rift

import com.sammy.malum.client.SpiritBasedParticleBuilder
import dev.sterner.api.rift.RiftType
import dev.sterner.common.blockentity.SpiritRiftBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import team.lodestar.lodestone.helpers.RandomHelper
import team.lodestar.lodestone.registry.common.particle.LodestoneParticleRegistry
import team.lodestar.lodestone.systems.particle.data.GenericParticleData
import team.lodestar.lodestone.systems.particle.render_types.LodestoneWorldParticleRenderType
import team.lodestar.lodestone.systems.particle.world.options.LodestoneTerrainParticleOptions
import kotlin.math.cos
import kotlin.math.sin

class DestabilizedRiftType : RiftType() {

    override fun tick(level: Level, blockPos: BlockPos, blockEntity: SpiritRiftBlockEntity) {
        super.tick(level, blockPos, blockEntity)
        for (i in 1..4) {
            genParticleOrbit(level, blockPos, 8, Blocks.GRASS_BLOCK.defaultBlockState(), i)
        }
    }

    /**
     * Precondition, direction assumes a value of [1 to 4] and will clamp if necessary
     */
    private fun genParticleOrbit(level: Level, blockPos: BlockPos, range: Int, state: BlockState, direction: Int) {
        val clampedDir = Mth.clamp(direction, 1, 4)
        val discRad = (range * (1 / 3f) + level.getRandom().nextGaussian() / 5f)
        val yRand = (level.getRandom().nextGaussian() - 0.5) / 4

        val builder = SpiritBasedParticleBuilder.createSpirit(
            LodestoneTerrainParticleOptions(
                LodestoneParticleRegistry.TERRAIN_PARTICLE,
                state,
                blockPos
            )
        )
            .setRenderType(LodestoneWorldParticleRenderType.TERRAIN_SHEET)
            .setGravityStrength(0f)
            .setFrictionStrength(0.98f)
            .setScaleData(GenericParticleData.create(0.0625f).build())

        builder
            .setMotion(discRad / 6f, 0.0, discRad / 6f)
            .addTickActor {
                val speed = 0.1f
                val time: Float = it.age / 6f

                val (newX, newZ) = when (clampedDir) {
                    1 -> Pair(cos(time) * discRad, sin(time) * discRad)
                    2 -> Pair(cos(time) * discRad, -sin(time) * discRad)
                    3 -> Pair(-cos(time) * discRad, sin(time) * discRad)
                    4 -> Pair(-cos(time) * discRad, -sin(time) * discRad)
                    else -> Pair(0f, 0f)
                }

                it.setParticleSpeed(
                    newX.toDouble() * speed,
                    it.particleSpeed.y,
                    newZ.toDouble() * speed
                )
            }
            .setLifetime(RandomHelper.randomBetween(level.random, 40, 80))
            .spawn(
                level,
                blockPos.x + 0.5,
                blockPos.y + 0.5 + yRand,
                blockPos.z + 0.5 + if (direction % 2 == 0) discRad / 2 else -discRad / 2
            )
    }
}