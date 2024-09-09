package dev.sterner.registry

import dev.sterner.VoidBound
import dev.sterner.common.menu.OsmoticEnchanterMenu
import io.github.fabricators_of_create.porting_lib.util.LazyRegistrar
import io.github.fabricators_of_create.porting_lib.util.RegistryObject
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.inventory.MenuType

object VoidBoundMenuTypeRegistry {

    val MENU_TYPES: LazyRegistrar<MenuType<*>> = LazyRegistrar.create(BuiltInRegistries.MENU, VoidBound.modid)

    val OSMOTIC_ENCHANTER: RegistryObject<ExtendedScreenHandlerType<OsmoticEnchanterMenu>> = MENU_TYPES
        .register(
            "osmotic_enchanter"
        ) {
            ExtendedScreenHandlerType { id, inv, buf -> OsmoticEnchanterMenu(id, inv, buf) }
        }
}