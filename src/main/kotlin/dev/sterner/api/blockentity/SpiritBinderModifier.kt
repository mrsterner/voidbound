package dev.sterner.api.blockentity

import net.minecraft.util.StringRepresentable
import java.util.*

/**
 * Used by the SpiritBinder to execute different tick functions
 */
enum class SpiritBinderModifier : StringRepresentable {
    NONE,
    BRILLIANT,
    HEX_ASH;

    override fun getSerializedName(): String {
        return this.name.lowercase(Locale.getDefault())
    }
}