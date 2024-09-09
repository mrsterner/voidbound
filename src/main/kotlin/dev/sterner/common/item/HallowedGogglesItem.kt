package dev.sterner.common.item

import dev.sterner.registry.VoidBoundMaterials
import net.minecraft.world.item.ArmorItem

class HallowedGogglesItem(properties: Properties) : ArmorItem(
    VoidBoundMaterials.HALLOWED, Type.HELMET,
    properties
)