package dev.sterner.common.entity.ai.sensor

import dev.sterner.api.entity.GolemCore
import dev.sterner.common.entity.SoulSteelGolemEntity
import dev.sterner.registry.VoidBoundMemoryTypeRegistry
import dev.sterner.registry.VoidBoundSensorTypeRegistry
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.Container
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.ai.sensing.SensorType
import net.minecraft.world.level.block.entity.BlockEntity
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor
import net.tslat.smartbrainlib.api.core.sensor.PredicateSensor
import net.tslat.smartbrainlib.`object`.SquareRadius
import net.tslat.smartbrainlib.util.BrainUtils
import java.util.function.BiPredicate

class GolemStorageSensor : PredicateSensor<BlockEntity, SoulSteelGolemEntity>(
    BiPredicate { _: BlockEntity, entity: SoulSteelGolemEntity ->
        entity.getGolemCore() == GolemCore.FILL || entity.getGolemCore() == GolemCore.EMPTY
    }
) {

    private var radius: SquareRadius = SquareRadius(16.0, 8.0)

    override fun memoriesUsed(): MutableList<MemoryModuleType<*>> {
        return mutableListOf(
            VoidBoundMemoryTypeRegistry.INPUT_STORAGE_LOCATIONS.get(),
            VoidBoundMemoryTypeRegistry.OUTPUT_STORAGE_LOCATIONS.get()
        )
    }

    override fun type(): SensorType<out ExtendedSensor<*>> {
        return VoidBoundSensorTypeRegistry.GOLEM_STORAGE_SENSOR.get()
    }

    override fun doTick(level: ServerLevel, entity: SoulSteelGolemEntity) {
        val blocks: MutableList<BlockPos> = ObjectArrayList()

        for (pos in BlockPos.betweenClosed(
            entity.blockPosition().subtract(this.radius.toVec3i()), entity.blockPosition().offset(
                this.radius.toVec3i()
            )
        )) {
            val be = level.getBlockEntity(pos)

            if (predicate().test(be, entity)) {
                if (be is Container) {
                    blocks.add(pos)
                }
            }
        }

        if (blocks.isEmpty()) {
            BrainUtils.clearMemory(entity, VoidBoundMemoryTypeRegistry.NEARBY_CROPS.get())
        } else {
            if (entity.getGolemCore() == GolemCore.FILL) {
                BrainUtils.setMemory(entity, VoidBoundMemoryTypeRegistry.OUTPUT_STORAGE_LOCATIONS.get(), blocks)
            } else if (entity.getGolemCore() == GolemCore.EMPTY) {
                BrainUtils.setMemory(entity, VoidBoundMemoryTypeRegistry.INPUT_STORAGE_LOCATIONS.get(), blocks)
            }
        }
    }
}