package dev.sterner.registry

import dev.sterner.VoidBound
import dev.sterner.client.particle.SmokeParticleType
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar
import io.github.fabricators_of_create.porting_lib.util.RegistryObject
import net.fabricmc.fabric.api.client.particle.v1.FabricSpriteProvider
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry
import net.minecraft.core.particles.ParticleType
import net.minecraft.core.registries.BuiltInRegistries
import team.lodestar.lodestone.systems.particle.world.type.LodestoneWorldParticleType

object VoidBoundParticleTypeRegistry {

    val PARTICLES: LazyRegistrar<ParticleType<*>> =
        LazyRegistrar.create(BuiltInRegistries.PARTICLE_TYPE, VoidBound.modid)


    var HEAL_PARTICLE: RegistryObject<LodestoneWorldParticleType> = PARTICLES.register(
        "heal"
    ) { LodestoneWorldParticleType() }

    var BUBBLE_PARTICLE: RegistryObject<LodestoneWorldParticleType> = PARTICLES.register(
        "bubble"
    ) { LodestoneWorldParticleType() }

    var SMOKE_PARTICLE: RegistryObject<SmokeParticleType> = PARTICLES.register(
        "smoke"
    ) { SmokeParticleType() }

    fun init() {
        ParticleFactoryRegistry.getInstance().register(
            HEAL_PARTICLE.get()
        ) { sprite: FabricSpriteProvider? ->
            LodestoneWorldParticleType.Factory(
                sprite
            )
        }

        ParticleFactoryRegistry.getInstance().register(
            BUBBLE_PARTICLE.get()
        ) { sprite: FabricSpriteProvider? ->
            LodestoneWorldParticleType.Factory(
                sprite
            )
        }

        ParticleFactoryRegistry.getInstance().register(
            SMOKE_PARTICLE.get()
        ) { sprite: FabricSpriteProvider ->
            SmokeParticleType.Factory(
                sprite
            )
        }
    }
}