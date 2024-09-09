package dev.sterner.common.entity.ai.sensor

import dev.sterner.api.entity.GolemCore
import dev.sterner.common.entity.SoulSteelGolemEntity
import dev.sterner.registry.VoidBoundSensorTypeRegistry
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.ai.sensing.SensorType
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor
import net.tslat.smartbrainlib.api.core.sensor.PredicateSensor
import net.tslat.smartbrainlib.`object`.SquareRadius
import net.tslat.smartbrainlib.util.BrainUtils
import net.tslat.smartbrainlib.util.EntityRetrievalUtil
import java.util.function.BiPredicate

class GolemGuardSensor : PredicateSensor<LivingEntity, SoulSteelGolemEntity>(
    BiPredicate { _: LivingEntity, entity: SoulSteelGolemEntity ->
        entity.getGolemCore() == GolemCore.GUARD
    }
) {

    private var radius: SquareRadius = SquareRadius(32.0, 16.0)

    override fun memoriesUsed(): MutableList<MemoryModuleType<*>> {
        return mutableListOf(MemoryModuleType.NEAREST_HOSTILE)
    }

    override fun type(): SensorType<out ExtendedSensor<*>> {
        return VoidBoundSensorTypeRegistry.GOLEM_GUARD_SENSOR.get()
    }

    fun setRadius(xz: Double, y: Double): GolemGuardSensor {
        this.radius = SquareRadius(xz, y)
        return this
    }

    override fun doTick(level: ServerLevel, entity: SoulSteelGolemEntity) {
        val v: List<LivingEntity> = EntityRetrievalUtil.getEntities(
            level,
            this.radius.inflateAABB(entity.boundingBox)
        ) { obj ->
            obj is LivingEntity && predicate().test(obj, entity)
        }

        BrainUtils.setMemory(
            entity,

            MemoryModuleType.NEAREST_HOSTILE,
            v[0]
        )
    }
}