package dev.sterner.registry

import dev.sterner.VoidBound
import dev.sterner.common.worldgen.EldritchObeliskStructure
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar
import io.github.fabricators_of_create.porting_lib.util.RegistryObject
import net.minecraft.core.registries.Registries
import net.minecraft.world.level.levelgen.structure.StructureType

object VoidBoundStructureRegistry {

    val STRUCTURES: LazyRegistrar<StructureType<*>> = LazyRegistrar.create(Registries.STRUCTURE_TYPE, VoidBound.modid)


    val ELDRITCH_OBELISK: RegistryObject<StructureType<EldritchObeliskStructure>> =
        STRUCTURES.register<StructureType<EldritchObeliskStructure>>(
            "eldritch_obelisk"
        ) { StructureType { (EldritchObeliskStructure.CODEC) } }
}