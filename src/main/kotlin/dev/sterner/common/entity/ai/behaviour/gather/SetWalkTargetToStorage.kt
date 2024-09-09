package dev.sterner.common.entity.ai.behaviour.gather

import com.mojang.datafixers.util.Pair
import dev.sterner.common.entity.SoulSteelGolemEntity
import dev.sterner.registry.VoidBoundMemoryTypeRegistry
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.ai.behavior.BlockPosTracker
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.ai.memory.MemoryStatus
import net.minecraft.world.entity.ai.memory.WalkTarget
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour
import net.tslat.smartbrainlib.util.BrainUtils


class SetWalkTargetToStorage : ExtendedBehaviour<SoulSteelGolemEntity>() {

    override fun getMemoryRequirements(): MutableList<Pair<MemoryModuleType<*>, MemoryStatus>> {
        return mutableListOf(
            Pair.of(
                VoidBoundMemoryTypeRegistry.INPUT_STORAGE_LOCATION.get(),
                MemoryStatus.VALUE_PRESENT
            )
        )
    }

    override fun start(level: ServerLevel, entity: SoulSteelGolemEntity, gameTime: Long) {
        super.start(level, entity, gameTime)

        if (!entity.inventory.isEmpty) {
            val memory = BrainUtils.getMemory(entity, VoidBoundMemoryTypeRegistry.INPUT_STORAGE_LOCATION.get())
            if (memory != null) {
                val walkTarget = WalkTarget(memory, 1f, 1)
                BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, walkTarget)
                entity.brain.setMemory(MemoryModuleType.LOOK_TARGET, BlockPosTracker(memory))
            }
        }
    }
}