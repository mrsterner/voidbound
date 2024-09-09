package dev.sterner.registry

import dev.sterner.VoidBound
import dev.sterner.common.blockentity.*
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar
import io.github.fabricators_of_create.porting_lib.util.RegistryObject
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState

@Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
object VoidBoundBlockEntityTypeRegistry {

    val BLOCK_ENTITY_TYPES: LazyRegistrar<BlockEntityType<*>> =
        LazyRegistrar.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, VoidBound.modid)

    var SPIRIT_BINDER: RegistryObject<BlockEntityType<SpiritBinderBlockEntity>> =
        BLOCK_ENTITY_TYPES.register("spirit_binder") {
            BlockEntityType.Builder.of(
                { pos, state -> SpiritBinderBlockEntity(pos, state) },
                VoidBoundBlockRegistry.SPIRIT_BINDER.get(),
            )
                .build(null)
        }

    var SPIRIT_BINDER_STABILIZER: RegistryObject<BlockEntityType<SpiritStabilizerBlockEntity>> =
        BLOCK_ENTITY_TYPES.register("spirit_stabilizer") {
            BlockEntityType.Builder.of(
                { pos, state -> SpiritStabilizerBlockEntity(pos, state) },
                VoidBoundBlockRegistry.SPIRIT_STABILIZER.get(),
            )
                .build(null)
        }

    var SPIRIT_RIFT: RegistryObject<BlockEntityType<SpiritRiftBlockEntity>> =
        BLOCK_ENTITY_TYPES.register("destabilized_spirit_rift") {
            BlockEntityType.Builder.of(
                { pos, state -> SpiritRiftBlockEntity(pos, state) },
                VoidBoundBlockRegistry.SPIRIT_RIFT.get()
            )
                .build(null)
        }


    var PORTABLE_HOLE: RegistryObject<BlockEntityType<PortableHoleBlockEntity>> =
        BLOCK_ENTITY_TYPES.register("portable_hole") {
            BlockEntityType.Builder.of(
                { pos, state -> PortableHoleBlockEntity(pos, state) },
                VoidBoundBlockRegistry.PORTABLE_HOLE.get()
            )
                .build(null)
        }

    var OSMOTIC_ENCHANTER: RegistryObject<BlockEntityType<OsmoticEnchanterBlockEntity>> =
        BLOCK_ENTITY_TYPES.register("osmotic_enchanter") {
            BlockEntityType.Builder.of(
                { pos, state -> OsmoticEnchanterBlockEntity(pos, state) },
                VoidBoundBlockRegistry.OSMOTIC_ENCHANTER.get()
            )
                .build(null)
        }

    val ELDRITCH_OBELISK: RegistryObject<BlockEntityType<EldritchObeliskBlockEntity>> =
        BLOCK_ENTITY_TYPES.register("eldritch_obelisk") {
            BlockEntityType.Builder.of(
                { pos: BlockPos?, state: BlockState? ->
                    EldritchObeliskBlockEntity(
                        pos,
                        state
                    )
                }, VoidBoundBlockRegistry.ELDRITCH_OBELISK.get()
            ).build(null)
        }

}