package dev.sterner.registry

import dev.sterner.VoidBound
import dev.sterner.api.rift.RiftType
import dev.sterner.common.rift.DestabilizedRiftType
import dev.sterner.common.rift.EldritchRiftType
import dev.sterner.common.rift.NormalRiftType
import dev.sterner.common.rift.PoolRiftType
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar
import io.github.fabricators_of_create.porting_lib.util.RegistryObject
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

object VoidBoundRiftTypeRegistry {

    private val RIFT_KEY: ResourceKey<Registry<RiftType>> = ResourceKey.createRegistryKey(VoidBound.id("rift"))
    val RIFT: Registry<RiftType> = FabricRegistryBuilder.createSimple(RIFT_KEY).buildAndRegister()

    val RIFT_TYPES: LazyRegistrar<RiftType> = LazyRegistrar.create(RIFT, VoidBound.modid)

    val NORMAL: RegistryObject<NormalRiftType> = RIFT_TYPES.register("normal") {
        NormalRiftType()
    }

    val DESTABILIZED: RegistryObject<DestabilizedRiftType> = RIFT_TYPES.register("destabilized") {
        DestabilizedRiftType()
    }

    val ELDRITCH: RegistryObject<EldritchRiftType> = RIFT_TYPES.register("eldritch") {
        EldritchRiftType()
    }

    val POOL: RegistryObject<PoolRiftType> = RIFT_TYPES.register("pool") {
        PoolRiftType()
    }
}