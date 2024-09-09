package dev.sterner.common.entity.ai.behaviour.harvest

import dev.sterner.common.entity.SoulSteelGolemEntity
import dev.sterner.registry.VoidBoundMemoryTypeRegistry
import net.minecraft.core.BlockPos
import net.minecraft.tags.BlockTags
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.ai.behavior.BlockPosTracker
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.ai.memory.MemoryStatus
import net.minecraft.world.level.block.CropBlock
import net.minecraft.world.level.block.state.BlockState
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour
import net.tslat.smartbrainlib.util.BrainUtils

class HarvestCrop : ExtendedBehaviour<SoulSteelGolemEntity>() {

    private var crop: com.mojang.datafixers.util.Pair<BlockPos, BlockState>? = null

    override fun getMemoryRequirements(): MutableList<com.mojang.datafixers.util.Pair<MemoryModuleType<*>, MemoryStatus>> {
        return mutableListOf(
            com.mojang.datafixers.util.Pair.of(
                VoidBoundMemoryTypeRegistry.NEARBY_CROPS.get(),
                MemoryStatus.VALUE_PRESENT
            )
        )
    }

    override fun start(entity: SoulSteelGolemEntity) {

        val crops = BrainUtils.getMemory(entity, VoidBoundMemoryTypeRegistry.NEARBY_CROPS.get())
        val closestCrop = crops?.minByOrNull { it.first.distSqr(entity.blockPosition()) }
        if (closestCrop != null) {
            val bl = closestCrop.first.distSqr(entity.blockPosition()) < 2
            if (bl) {
                crop = closestCrop
            }
        }

        if (crop != null && crop!!.second.`is`(BlockTags.CROPS)) {
            entity.swing(InteractionHand.MAIN_HAND)
            entity.brain.setMemory(MemoryModuleType.LOOK_TARGET, BlockPosTracker(this.crop!!.first))
            entity.level().destroyBlock(crop!!.first, true)
            if (crop!!.second.block is CropBlock) {
                val c = crop!!.second.block as CropBlock
                entity.level().setBlockAndUpdate(crop!!.first, c.getStateForAge(0))
                BrainUtils.clearMemory(entity, VoidBoundMemoryTypeRegistry.NEARBY_CROPS.get())
            }
            crop = null
        }
    }

    override fun stop(entity: SoulSteelGolemEntity) {
        entity.brain.eraseMemory(MemoryModuleType.LOOK_TARGET)
        super.stop(entity)
    }
}