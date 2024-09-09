package dev.sterner.client.particle

import net.fabricmc.fabric.impl.client.particle.FabricSpriteProviderImpl
import net.minecraft.client.multiplayer.ClientLevel
import net.minecraft.client.particle.Particle
import net.minecraft.client.particle.ParticleProvider
import net.minecraft.client.particle.SpriteSet
import team.lodestar.lodestone.systems.particle.world.options.WorldParticleOptions
import team.lodestar.lodestone.systems.particle.world.type.LodestoneWorldParticleType

class SmokeParticleType : LodestoneWorldParticleType() {

    class Factory(private val sprite: SpriteSet) : ParticleProvider<WorldParticleOptions> {

        override fun createParticle(
            data: WorldParticleOptions,
            world: ClientLevel,
            x: Double,
            y: Double,
            z: Double,
            mx: Double,
            my: Double,
            mz: Double
        ): Particle {
            return SmokeParticle(world, data, sprite as FabricSpriteProviderImpl, x, y, z, mx, my, mz)
        }
    }
}