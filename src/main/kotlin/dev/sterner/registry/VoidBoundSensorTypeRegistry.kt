package dev.sterner.registry

import dev.sterner.VoidBound
import dev.sterner.common.entity.ai.sensor.GolemGatherSensor
import dev.sterner.common.entity.ai.sensor.GolemGuardSensor
import dev.sterner.common.entity.ai.sensor.GolemHarvestSensor
import dev.sterner.common.entity.ai.sensor.GolemStorageSensor
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar
import io.github.fabricators_of_create.porting_lib.util.RegistryObject
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.entity.ai.sensing.SensorType

object VoidBoundSensorTypeRegistry {

    var SENSOR_TYPES: LazyRegistrar<SensorType<*>> =
        LazyRegistrar.create(BuiltInRegistries.SENSOR_TYPE, VoidBound.modid)

    var GOLEM_GATHER_SENSOR: RegistryObject<SensorType<GolemGatherSensor>> = SENSOR_TYPES.register("golem_gather") {
        SensorType(::GolemGatherSensor)
    }

    var GOLEM_HARVEST_SENSOR: RegistryObject<SensorType<GolemHarvestSensor>> = SENSOR_TYPES.register("golem_harvest") {
        SensorType(::GolemHarvestSensor)
    }

    var GOLEM_GUARD_SENSOR: RegistryObject<SensorType<GolemGuardSensor>> = SENSOR_TYPES.register("golem_guard") {
        SensorType(::GolemGuardSensor)
    }

    var GOLEM_STORAGE_SENSOR: RegistryObject<SensorType<GolemStorageSensor>> = SENSOR_TYPES.register("golem_storage") {
        SensorType(::GolemStorageSensor)
    }
}