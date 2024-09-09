package dev.sterner.client.particle

import net.fabricmc.fabric.impl.client.particle.FabricSpriteProviderImpl
import net.minecraft.client.multiplayer.ClientLevel
import team.lodestar.lodestone.systems.particle.world.FrameSetParticle
import team.lodestar.lodestone.systems.particle.world.options.WorldParticleOptions

class SmokeParticle(
    world: ClientLevel?,
    options: WorldParticleOptions?,
    spriteSet: FabricSpriteProviderImpl?,
    x: Double,
    y: Double,
    z: Double,
    xd: Double,
    yd: Double,
    zd: Double
) :
    FrameSetParticle(world, options, spriteSet, x, y, z, xd, yd, zd) {
    init {
        addFrames(0, 11)
        addLoop(0, 11, 6)
        setLifetime(frameSet.size)
    }
}