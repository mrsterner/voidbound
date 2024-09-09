package dev.sterner.registry

import dev.sterner.VoidBound
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar
import io.github.fabricators_of_create.porting_lib.util.RegistryObject
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.level.block.state.BlockState
import java.util.*


object VoidBoundMemoryTypeRegistry {

    var MEMORY_TYPES: LazyRegistrar<MemoryModuleType<*>> =
        LazyRegistrar.create(BuiltInRegistries.MEMORY_MODULE_TYPE, VoidBound.modid)

    var NEARBY_ITEMS: RegistryObject<MemoryModuleType<List<ItemEntity>>> = MEMORY_TYPES.register("nearby_items") {
        MemoryModuleType<List<ItemEntity>>(Optional.empty())
    }

    var NEARBY_CROPS: RegistryObject<MemoryModuleType<List<com.mojang.datafixers.util.Pair<BlockPos, BlockState>>>> =
        MEMORY_TYPES.register("nearby_crops") {
            MemoryModuleType<List<com.mojang.datafixers.util.Pair<BlockPos, BlockState>>>(Optional.empty())
        }

    var INPUT_STORAGE_LOCATION: RegistryObject<MemoryModuleType<BlockPos>> =
        MEMORY_TYPES.register("input_storage_location") {
            MemoryModuleType<BlockPos>(Optional.empty())
        }

    var INPUT_STORAGE_LOCATIONS: RegistryObject<MemoryModuleType<MutableList<BlockPos>>> =
        MEMORY_TYPES.register("input_storage_locations") {
            MemoryModuleType<MutableList<BlockPos>>(Optional.empty())
        }

    var OUTPUT_STORAGE_LOCATION: RegistryObject<MemoryModuleType<BlockPos>> =
        MEMORY_TYPES.register("output_storage_location") {
            MemoryModuleType<BlockPos>(Optional.empty())
        }

    var OUTPUT_STORAGE_LOCATIONS: RegistryObject<MemoryModuleType<MutableList<BlockPos>>> =
        MEMORY_TYPES.register("output_storage_locations") {
            MemoryModuleType<MutableList<BlockPos>>(Optional.empty())
        }
}