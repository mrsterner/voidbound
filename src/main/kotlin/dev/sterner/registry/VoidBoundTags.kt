package dev.sterner.registry

import dev.sterner.VoidBound
import net.minecraft.core.registries.Registries
import net.minecraft.tags.TagKey
import net.minecraft.world.level.block.Block

object VoidBoundTags {

    val PORTABLE_HOLE_BLACKLIST: TagKey<Block> = block("portable_hole_blacklist")

    private fun block(name: String): TagKey<Block> {
        return TagKey.create(Registries.BLOCK, VoidBound.id(name))
    }

    fun init() {

    }
}