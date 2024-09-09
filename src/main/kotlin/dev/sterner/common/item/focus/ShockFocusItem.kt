package dev.sterner.common.item.focus

import dev.sterner.registry.VoidBoundWandFocusRegistry
import java.awt.Color

class ShockFocusItem(properties: Properties) : AbstractFocusItem(VoidBoundWandFocusRegistry.SHOCK.get(), properties) {

    override fun color(): Color = Color(155, 255, 255)

    override fun endColor(): Color = Color(50, 125, 203)
}