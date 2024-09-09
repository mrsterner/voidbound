package dev.sterner.common.entity.ai.behaviour.harvest

import com.mojang.datafixers.util.Pair
import dev.sterner.api.entity.GolemCore
import dev.sterner.common.entity.SoulSteelGolemEntity
import dev.sterner.registry.VoidBoundMemoryTypeRegistry
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.ai.memory.MemoryStatus
import net.minecraft.world.entity.ai.memory.WalkTarget
import net.minecraft.world.level.block.state.BlockState
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour
import net.tslat.smartbrainlib.util.BrainUtils


class SetCropWalkTarget : ExtendedBehaviour<SoulSteelGolemEntity>() {

    override fun getMemoryRequirements(): MutableList<Pair<MemoryModuleType<*>, MemoryStatus>> {
        return mutableListOf(Pair.of(VoidBoundMemoryTypeRegistry.NEARBY_CROPS.get(), MemoryStatus.VALUE_PRESENT))
    }

    override fun start(level: ServerLevel, entity: SoulSteelGolemEntity, gameTime: Long) {
        super.start(level, entity, gameTime)
        if (entity.getGolemCore() != GolemCore.HARVEST) {
            BrainUtils.clearMemory(entity, VoidBoundMemoryTypeRegistry.NEARBY_CROPS.get())
        } else {

            val crops: List<Pair<BlockPos, BlockState>>? =
                BrainUtils.getMemory(entity, VoidBoundMemoryTypeRegistry.NEARBY_CROPS.get())

            val closestCrop = crops?.minByOrNull { it.first.distSqr(entity.blockPosition()) }

            if (closestCrop != null) {
                val walkTarget = WalkTarget(closestCrop.first, 1f, 1)
                BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, walkTarget)
            }
        }
    }
}