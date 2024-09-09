package dev.sterner.common.item.focus

import dev.sterner.registry.VoidBoundWandFocusRegistry
import java.awt.Color

class ExcavationFocusItem(properties: Properties) :
    AbstractFocusItem(VoidBoundWandFocusRegistry.EXCAVATION.get(), properties) {

    override fun color(): Color = Color(135, 255, 135)

    override fun endColor(): Color = Color(0, 225, 0)
}