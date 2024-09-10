package dev.sterner.registry

import dev.sterner.VoidBound
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block

object VoidBoundTags {

    val PORTABLE_HOLE_BLACKLIST: TagKey<Block> = block("portable_hole_blacklist")

    val ITEM_WITH_ABILITY: TagKey<Item> = item("item_with_ability")

    private fun item(name: String): TagKey<Item> {
        return TagKey.create(Registries.ITEM, VoidBound.id(name))
    }

    private fun block(name: String): TagKey<Block> {
        return TagKey.create(Registries.BLOCK, VoidBound.id(name))
    }

    fun init() {

    }
}