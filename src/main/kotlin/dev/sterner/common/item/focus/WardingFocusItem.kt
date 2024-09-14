package dev.sterner.common.item.focus

import dev.sterner.registry.VoidBoundWandFocusRegistry
import java.awt.Color

class WardingFocusItem(properties: Properties) :
    AbstractFocusItem(VoidBoundWandFocusRegistry.WARDING.get(), properties) {

    override fun color(): Color = Color(255, 255, 255)

    override fun endColor(): Color = Color(255, 155, 23)
}