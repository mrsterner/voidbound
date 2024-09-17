package dev.sterner.registry

import dev.onyxstudios.cca.api.v3.component.ComponentKey
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer
import dev.sterner.VoidBound
import dev.sterner.common.components.*
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player

class VoidBoundComponentRegistry : WorldComponentInitializer, EntityComponentInitializer {

    override fun registerWorldComponentFactories(registry: WorldComponentFactoryRegistry) {
        registry.register(VOID_BOUND_WORLD_COMPONENT, ::VoidBoundWorldComponent)
    }

    override fun registerEntityComponentFactories(registry: EntityComponentFactoryRegistry) {
        registry.beginRegistration(LivingEntity::class.java, VOID_BOUND_ENTITY_COMPONENT)
            .respawnStrategy(RespawnCopyStrategy.NEVER_COPY).end { livingEntity: LivingEntity ->
                VoidBoundEntityComponent(
                    livingEntity
                )
            }

        registry.registerForPlayers(
            VOID_BOUND_PLAYER_COMPONENT,
            { entity: Player ->
                VoidBoundPlayerComponent(
                    entity
                )
            }, RespawnCopyStrategy.ALWAYS_COPY
        )

        registry.registerForPlayers(
            VOID_BOUND_REVELATION_COMPONENT,
            { entity: Player ->
                VoidBoundRevelationComponent(
                    entity
                )
            }, RespawnCopyStrategy.ALWAYS_COPY
        )

        registry.registerForPlayers(
            VOID_BOUND_PLAYER_ITEM_ABILITY_COMPONENT,
            { entity: Player ->
                VoidBoundPlayerItemAbilityComponent(
                    entity
                )
            }, RespawnCopyStrategy.ALWAYS_COPY
        )

        registry.registerForPlayers(
            VOID_BOUND_PLAYER_SEALER_COMPONENT,
            { entity: Player ->
                VoidBoundPlayerSealerComponent(
                    entity
                )
            }, RespawnCopyStrategy.ALWAYS_COPY
        )
    }

    companion object {
        val VOID_BOUND_WORLD_COMPONENT: ComponentKey<VoidBoundWorldComponent> = ComponentRegistry.getOrCreate(
            VoidBound.id("world"),
            VoidBoundWorldComponent::class.java
        )

        val VOID_BOUND_ENTITY_COMPONENT: ComponentKey<VoidBoundEntityComponent> = ComponentRegistry.getOrCreate(
            VoidBound.id("entity"),
            VoidBoundEntityComponent::class.java
        )

        val VOID_BOUND_PLAYER_COMPONENT: ComponentKey<VoidBoundPlayerComponent> = ComponentRegistry.getOrCreate(
            VoidBound.id("player"),
            VoidBoundPlayerComponent::class.java
        )

        val VOID_BOUND_PLAYER_ITEM_ABILITY_COMPONENT: ComponentKey<VoidBoundPlayerItemAbilityComponent> = ComponentRegistry.getOrCreate(
            VoidBound.id("player_item_ability"),
            VoidBoundPlayerItemAbilityComponent::class.java
        )

        val VOID_BOUND_PLAYER_SEALER_COMPONENT: ComponentKey<VoidBoundPlayerSealerComponent> = ComponentRegistry.getOrCreate(
            VoidBound.id("player_sealer"),
            VoidBoundPlayerSealerComponent::class.java
        )

        val VOID_BOUND_REVELATION_COMPONENT: ComponentKey<VoidBoundRevelationComponent> = ComponentRegistry.getOrCreate(
            VoidBound.id("revelation"),
            VoidBoundRevelationComponent::class.java
        )
    }
}