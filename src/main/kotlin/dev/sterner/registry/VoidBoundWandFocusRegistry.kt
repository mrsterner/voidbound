package dev.sterner.registry

import dev.sterner.VoidBound
import dev.sterner.api.wand.IWandFocus
import dev.sterner.common.item.focus.PortableHoleFocus
import dev.sterner.common.item.focus.*
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar
import io.github.fabricators_of_create.porting_lib.util.RegistryObject
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder
import net.minecraft.core.Registry
import net.minecraft.resources.ResourceKey

object VoidBoundWandFocusRegistry {

    private val WAND_FOCUS_KEY: ResourceKey<Registry<IWandFocus>> =
        ResourceKey.createRegistryKey(VoidBound.id("wand_focus"))
    val WAND_FOCUS: Registry<IWandFocus> = FabricRegistryBuilder.createSimple(WAND_FOCUS_KEY).buildAndRegister()

    val WAND_FOCI: LazyRegistrar<IWandFocus> = LazyRegistrar.create(WAND_FOCUS, VoidBound.modid)

    val PORTABLE_HOLE: RegistryObject<PortableHoleFocus> = WAND_FOCI.register("portable_hole") {
        PortableHoleFocus()
    }

    val EXCAVATION: RegistryObject<ExcavationFocus> = WAND_FOCI.register("excavation") {
        ExcavationFocus()
    }

    val SHOCK: RegistryObject<ShockFocus> = WAND_FOCI.register("shock") {
        ShockFocus()
    }

    val FIRE: RegistryObject<FireFocus> = WAND_FOCI.register("fire") {
        FireFocus()
    }

    val WARDING: RegistryObject<WardingFocus> = WAND_FOCI.register("warding") {
        WardingFocus()
    }
}