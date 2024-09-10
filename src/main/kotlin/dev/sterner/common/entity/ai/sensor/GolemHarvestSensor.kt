package dev.sterner.common.entity.ai.sensor

import com.mojang.datafixers.util.Pair
import dev.sterner.api.entity.GolemCore
import dev.sterner.common.entity.SoulSteelGolemEntity
import dev.sterner.registry.VoidBoundMemoryTypeRegistry
import dev.sterner.registry.VoidBoundSensorTypeRegistry
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.tags.BlockTags
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.ai.sensing.SensorType
import net.minecraft.world.level.block.CropBlock
import net.minecraft.world.level.block.state.BlockState
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor
import net.tslat.smartbrainlib.api.core.sensor.PredicateSensor
import net.tslat.smartbrainlib.`object`.SquareRadius
import net.tslat.smartbrainlib.util.BrainUtils
import java.util.function.BiPredicate

class GolemHarvestSensor : PredicateSensor<BlockState, SoulSteelGolemEntity>(
    BiPredicate { state: BlockState, entity: SoulSteelGolemEntity ->
        entity.getGolemCore() == GolemCore.HARVEST && state.`is`(BlockTags.CROPS)
    }
) {

    private var radius: SquareRadius = SquareRadius(24.0, 8.0)

    override fun memoriesUsed(): MutableList<MemoryModuleType<*>> {
        return mutableListOf(VoidBoundMemoryTypeRegistry.NEARBY_CROPS.get())
    }

    override fun type(): SensorType<out ExtendedSensor<*>> {
        return VoidBoundSensorTypeRegistry.GOLEM_HARVEST_SENSOR.get()
    }

    fun setRadius(xz: Double, y: Double): GolemHarvestSensor {
        this.radius = SquareRadius(xz, y)

        return this
    }

    override fun doTick(level: ServerLevel, entity: SoulSteelGolemEntity) {
        if (entity.getGolemCore() != GolemCore.HARVEST) {
            return  // Exit early if the predicate is false
        }

        //test golemcore predicate here

        val blocks: MutableList<Pair<BlockPos, BlockState>> = ObjectArrayList()

        for (pos in BlockPos.betweenClosed(
            entity.blockPosition().subtract(this.radius.toVec3i()), entity.blockPosition().offset(
                this.radius.toVec3i()
            )
        )) {
            val state = level.getBlockState(pos)

            if (predicate().test(state, entity)) {

                if (state.block is CropBlock) {
                    val crop = state.block as CropBlock
                    if (crop.isMaxAge(state)) {
                        blocks.add(Pair.of(pos.immutable(), state))
                    }
                }
            }
        }

        if (blocks.isEmpty()) {
            BrainUtils.clearMemory(entity, VoidBoundMemoryTypeRegistry.NEARBY_CROPS.get())
        } else {
            BrainUtils.setMemory(entity, VoidBoundMemoryTypeRegistry.NEARBY_CROPS.get(), blocks)
        }
    }
}