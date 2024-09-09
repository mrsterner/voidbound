package dev.sterner.common.components

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.world.entity.LivingEntity

/**
 * Used by Eldritch rift to track entities being sucked
 */
class VoidBoundEntityComponent(val livingEntity: LivingEntity) : AutoSyncedComponent {

    var eldritchRiftPos: BlockPos? = null

    override fun readFromNbt(tag: CompoundTag) {
        eldritchRiftPos = NbtUtils.readBlockPos(tag.getCompound("BlockPos"))
    }

    override fun writeToNbt(tag: CompoundTag) {
        if (eldritchRiftPos != null) {
            val posTag: CompoundTag = NbtUtils.writeBlockPos(eldritchRiftPos!!)
            tag.put("BlockPos", posTag)
        }
    }
}