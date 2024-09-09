package dev.sterner.common.entity.ai.behaviour.gather

import com.mojang.datafixers.util.Pair
import dev.sterner.api.entity.GolemCore
import dev.sterner.common.entity.SoulSteelGolemEntity
import dev.sterner.registry.VoidBoundMemoryTypeRegistry
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.ai.memory.MemoryStatus
import net.minecraft.world.entity.ai.memory.WalkTarget
import net.minecraft.world.entity.item.ItemEntity
import net.tslat.smartbrainlib.api.core.behaviour.ExtendedBehaviour
import net.tslat.smartbrainlib.util.BrainUtils

class SetWalkTargetToItem : ExtendedBehaviour<SoulSteelGolemEntity>() {

    override fun getMemoryRequirements(): MutableList<Pair<MemoryModuleType<*>, MemoryStatus>> {
        return mutableListOf(Pair.of(VoidBoundMemoryTypeRegistry.NEARBY_ITEMS.get(), MemoryStatus.VALUE_PRESENT))
    }

    override fun start(level: ServerLevel, entity: SoulSteelGolemEntity, gameTime: Long) {
        super.start(level, entity, gameTime)
        if (entity.getGolemCore() != GolemCore.GATHER) {
            BrainUtils.clearMemory(entity, VoidBoundMemoryTypeRegistry.NEARBY_ITEMS.get())
        } else {

            val items: List<ItemEntity>? = BrainUtils.getMemory(entity, VoidBoundMemoryTypeRegistry.NEARBY_ITEMS.get())
                ?.filter { true } //TODO filter to inventory space

            val closest: ItemEntity? = items?.minByOrNull { it.distanceToSqr(entity) }!!

            if (closest != null) {
                val walkTarget = WalkTarget(closest.position(), 1f, 1)
                BrainUtils.setMemory(entity, MemoryModuleType.WALK_TARGET, walkTarget)
            }
        }
    }
}