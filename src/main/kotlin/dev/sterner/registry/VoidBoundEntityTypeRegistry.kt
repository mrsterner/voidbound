package dev.sterner.registry

import dev.sterner.VoidBound
import dev.sterner.common.entity.*
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar
import io.github.fabricators_of_create.porting_lib.util.RegistryObject
import net.fabricmc.fabric.api.`object`.builder.v1.entity.FabricEntityTypeBuilder
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.EntityDimensions
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.MobCategory
import net.minecraft.world.level.Level

object VoidBoundEntityTypeRegistry {
    var ENTITY_TYPES: LazyRegistrar<EntityType<*>> =
        LazyRegistrar.create(BuiltInRegistries.ENTITY_TYPE, VoidBound.modid)

    var PARTICLE_ENTITY: RegistryObject<EntityType<ParticleEntity?>> = ENTITY_TYPES.register("particle_entity") {
        EntityType.Builder.of(
            { _: EntityType<ParticleEntity?>?, w: Level ->
                ParticleEntity(
                    w
                )
            }, MobCategory.MISC
        ).sized(0.05f, 0.05f).clientTrackingRange(50).build(VoidBound.id("particle_entity").toString())
    }

    var ITEM_CARRIER_ENTITY: RegistryObject<EntityType<ItemCarrierItemEntity?>> = ENTITY_TYPES.register("item_carrier") {
        EntityType.Builder.of(
            { _: EntityType<ItemCarrierItemEntity?>?, w: Level ->
                ItemCarrierItemEntity(
                    w
                )
            }, MobCategory.MISC
        ).sized(0.5f, 0.75f).clientTrackingRange(50).build(VoidBound.id("item_carrier").toString())
    }

    var SOUL_STEEL_GOLEM_ENTITY: RegistryObject<EntityType<SoulSteelGolemEntity>> =
        ENTITY_TYPES.register("soul_steel_golem") {
            FabricEntityTypeBuilder.Mob.createMob<SoulSteelGolemEntity>()
                .entityFactory { _, w -> SoulSteelGolemEntity(w) }
                .dimensions(EntityDimensions(0.5f, 1.1f, true))
                .spawnGroup(MobCategory.CREATURE)
                .defaultAttributes { AbstractGolemEntity.createGolemAttributes() }
                .build()
        }

    var GRIMCULT_KNIGHT_ENTITY: RegistryObject<EntityType<GrimcultKnightEntity>> =
        ENTITY_TYPES.register("grimcult_knight") {
            FabricEntityTypeBuilder.Mob.createMob<GrimcultKnightEntity>()
                .entityFactory { _, w -> GrimcultKnightEntity(w) }
                .dimensions(EntityDimensions(0.6f, 1.8f, true))
                .spawnGroup(MobCategory.MONSTER)
                .defaultAttributes { GrimcultKnightEntity.createGrimcultAttributes() }
                .build()
        }

    var GRIMCULT_ARCHER_ENTITY: RegistryObject<EntityType<GrimcultArcherEntity>> =
        ENTITY_TYPES.register("grimcult_archer") {
            FabricEntityTypeBuilder.Mob.createMob<GrimcultArcherEntity>()
                .entityFactory { _, w -> GrimcultArcherEntity(w) }
                .dimensions(EntityDimensions(0.6f, 1.8f, true))
                .spawnGroup(MobCategory.MONSTER)
                .defaultAttributes { GrimcultArcherEntity.createGrimcultAttributes() }
                .build()
        }

    var GRIMCULT_CLERIC_ENTITY: RegistryObject<EntityType<GrimcultClericEntity>> =
        ENTITY_TYPES.register("grimcult_cleric") {
            FabricEntityTypeBuilder.Mob.createMob<GrimcultClericEntity>()
                .entityFactory { _, w -> GrimcultClericEntity(w) }
                .dimensions(EntityDimensions(0.6f, 1.8f, true))
                .spawnGroup(MobCategory.MONSTER)
                .defaultAttributes { GrimcultClericEntity.createGrimcultAttributes() }
                .build()
        }

    var GRIMCULT_NECROMANCER_ENTITY: RegistryObject<EntityType<GrimcultNecromancerEntity>> =
        ENTITY_TYPES.register("grimcult_necromancer") {
            FabricEntityTypeBuilder.Mob.createMob<GrimcultNecromancerEntity>()
                .entityFactory { _, w -> GrimcultNecromancerEntity(w) }
                .dimensions(EntityDimensions(0.6f, 1.8f, true))
                .spawnGroup(MobCategory.MONSTER)
                .defaultAttributes { GrimcultNecromancerEntity.createGrimcultAttributes() }
                .build()
        }

    var GRIMCULT_HEAVY_KNIGHT_ENTITY: RegistryObject<EntityType<GrimcultHeavyKnightEntity>> =
        ENTITY_TYPES.register("grimcult_heavy_knight") {
            FabricEntityTypeBuilder.Mob.createMob<GrimcultHeavyKnightEntity>()
                .entityFactory { _, w -> GrimcultHeavyKnightEntity(w) }
                .dimensions(EntityDimensions(0.6f, 1.8f, true))
                .spawnGroup(MobCategory.MONSTER)
                .defaultAttributes { GrimcultHeavyKnightEntity.createGrimcultAttributes() }
                .build()
        }

    var GRIMCULT_JESTER_ENTITY: RegistryObject<EntityType<GrimcultJesterEntity>> =
        ENTITY_TYPES.register("grimcult_jester") {
            FabricEntityTypeBuilder.Mob.createMob<GrimcultJesterEntity>()
                .entityFactory { _, w -> GrimcultJesterEntity(w) }
                .dimensions(EntityDimensions(0.6f, 1.9f, true))
                .spawnGroup(MobCategory.MONSTER)
                .defaultAttributes { GrimcultJesterEntity.createGrimcultAttributes() }
                .build()
        }

    var BOLT_ENTITY: RegistryObject<EntityType<BoltEntity>> =
        ENTITY_TYPES.register("bolt") {
            FabricEntityTypeBuilder.create<BoltEntity>()
                .entityFactory { e, w -> BoltEntity(e, w) }
                .dimensions(EntityDimensions(0.6f, 1.9f, true))
                .spawnGroup(MobCategory.MISC)
                .build()
        }
}