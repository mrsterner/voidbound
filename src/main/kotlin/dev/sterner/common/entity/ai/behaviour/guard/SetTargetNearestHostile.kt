package dev.sterner.common.entity.ai.behaviour.guard

import com.mojang.datafixers.util.Pair
import dev.sterner.common.entity.SoulSteelGolemEntity
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.ai.memory.MemoryStatus
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour
import net.tslat.smartbrainlib.util.BrainUtils

class SetTargetNearestHostile : ExtendedBehaviour<SoulSteelGolemEntity>() {

    override fun getMemoryRequirements(): MutableList<Pair<MemoryModuleType<*>, MemoryStatus>> {
        return ObjectArrayList.of(
            Pair.of(
                MemoryModuleType.ATTACK_TARGET,
                MemoryStatus.VALUE_ABSENT
            ),
            Pair.of(
                MemoryModuleType.NEAREST_HOSTILE,
                MemoryStatus.VALUE_PRESENT
            )
        )
    }

    override fun start(entity: SoulSteelGolemEntity) {
        val target: LivingEntity? = BrainUtils.getMemory(entity, MemoryModuleType.NEAREST_HOSTILE)
        if (target == null) {
            BrainUtils.clearMemory(entity, MemoryModuleType.ATTACK_TARGET)
        } else {
            BrainUtils.setMemory(entity, MemoryModuleType.ATTACK_TARGET, target)
            BrainUtils.clearMemory(entity, MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE)
        }
    }
}