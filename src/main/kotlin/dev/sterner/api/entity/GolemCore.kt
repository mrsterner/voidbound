package dev.sterner.api.entity

import dev.sterner.registry.VoidBoundItemRegistry
import net.minecraft.nbt.CompoundTag
import net.minecraft.util.StringRepresentable
import net.minecraft.world.item.Item

/**
 * implemented is false if it should notify the player from the tooltip
 */
enum class GolemCore(val implemented: Boolean) : StringRepresentable {
    NONE(true),
    GATHER(true),
    FILL(false),
    EMPTY(false),
    HARVEST(true),
    GUARD(true),
    BUTCHER(false),
    LUMBER(false);

    override fun getSerializedName(): String {
        return this.name.lowercase()
    }

    companion object {

        fun getItem(core: GolemCore): Item? {
            return when (core.serializedName) {
                "gather" -> VoidBoundItemRegistry.GOLEM_CORE_GATHER.get()
                "fill" -> VoidBoundItemRegistry.GOLEM_CORE_FILL.get()
                "empty" -> VoidBoundItemRegistry.GOLEM_CORE_EMPTY.get()
                "harvest" -> VoidBoundItemRegistry.GOLEM_CORE_HARVEST.get()
                "guard" -> VoidBoundItemRegistry.GOLEM_CORE_GUARD.get()
                "butcher" -> VoidBoundItemRegistry.GOLEM_CORE_BUTCHER.get()
                "lumber" -> VoidBoundItemRegistry.GOLEM_CORE_LUMBER.get()
                else -> null
            }
        }

        fun writeNbt(tag: CompoundTag, core: GolemCore) {
            tag.putString("name", core.name)
        }

        fun readNbt(tag: CompoundTag): GolemCore {
            if (tag.contains("name")) {
                return valueOf(tag.getString("name"))
            }
            return NONE
        }
    }
}