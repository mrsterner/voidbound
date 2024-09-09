package dev.sterner.common.item.focus

import dev.sterner.registry.VoidBoundWandFocusRegistry
import java.awt.Color

class PortableHoleFocusItem(properties: Properties) :
    AbstractFocusItem(VoidBoundWandFocusRegistry.PORTABLE_HOLE.get(), properties) {
    override fun color(): Color {
        return Color(0, 0, 0)
    }

    override fun endColor(): Color {
        return Color(0, 0, 0)
    }

    override fun isVoid(): Boolean {
        return true
    }
}