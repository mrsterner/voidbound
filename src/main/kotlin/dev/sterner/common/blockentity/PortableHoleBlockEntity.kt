package dev.sterner.common.blockentity

import dev.sterner.common.item.focus.PortableHoleFocus
import dev.sterner.registry.VoidBoundBlockEntityTypeRegistry
import dev.sterner.registry.VoidBoundBlockRegistry
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.HolderGetter
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import team.lodestar.lodestone.systems.blockentity.LodestoneBlockEntity
import java.util.*

class PortableHoleBlockEntity(pos: BlockPos, state: BlockState) : LodestoneBlockEntity(
    VoidBoundBlockEntityTypeRegistry.PORTABLE_HOLE.get(), pos,
    state
) {
    private var originalBlockState: BlockState? = null
    private var originalBlockEntity: BlockEntity? = null
    private val maxDuration = 120
    private var duration = maxDuration
    private var distance = 0
    private var direction = Direction.DOWN
    private var owner: UUID? = null

    constructor(
        owner: UUID,
        pos: BlockPos,
        oldState: BlockState,
        oldEntity: BlockEntity?,
        direction: Direction,
        distance: Int
    ) : this(pos, VoidBoundBlockRegistry.PORTABLE_HOLE.get().defaultBlockState()) {
        this.distance = distance
        this.direction = direction
        this.originalBlockState = oldState
        this.originalBlockEntity = oldEntity
        this.owner = owner
        notifyUpdate()
    }

    override fun tick() {
        if (level == null || originalBlockState == null) {
            return
        }

        if (duration > 0) {
            --duration
        }

        if (duration == maxDuration - 1 && distance > 1) {
            val nextPos = blockPos.relative(direction)
            if (owner != null) {
                PortableHoleFocus.createHole(owner!!, level!!, nextPos, direction, distance - 1)
            }
        }

        if (this.duration <= 0) {
            level!!.setBlockAndUpdate(blockPos, originalBlockState!!)
            if (originalBlockEntity != null) {
                originalBlockEntity!!.clearRemoved()
                level!!.setBlockEntity(originalBlockEntity!!)
            }
            this.level!!.scheduleTick(blockPos, originalBlockState!!.block, 2)
        }
    }

    override fun saveAdditional(tag: CompoundTag) {
        tag.putInt("Duration", this.duration)
        tag.putInt("Distance", this.distance)
        tag.putInt("Direction", this.direction.ordinal)
        if (owner != null) {
            tag.putUUID("Owner", this.owner!!)
        }
        if (originalBlockState != null) {
            tag.put("BlockState", NbtUtils.writeBlockState(originalBlockState!!))
        }

        if (originalBlockEntity != null) {
            tag.put("BlockEntity", originalBlockEntity!!.saveWithFullMetadata())
        }
        super.saveAdditional(tag)
    }


    override fun load(tag: CompoundTag) {
        if (tag.contains("Duration")) {
            duration = tag.getInt("Duration")
        }
        if (tag.contains("Distance")) {
            distance = tag.getInt("Distance")
        }
        if (tag.contains("Direction")) {
            direction = Direction.entries.toTypedArray()[tag.getInt("Direction")]
        }
        if (tag.contains("Owner")) {
            owner = tag.getUUID("Owner")
        }

        val holderGetter =
            (if (this.level != null) level!!.holderLookup(Registries.BLOCK) else BuiltInRegistries.BLOCK.asLookup()) as HolderGetter<Block?>
        if (tag.contains("BlockState")) {
            originalBlockState = NbtUtils.readBlockState(holderGetter, tag.getCompound("BlockState"))

            if (tag.contains("BlockEntity")) {
                val blockEntityTag: CompoundTag = tag.get("BlockEntity") as CompoundTag
                originalBlockEntity = loadStatic(blockPos, originalBlockState!!, blockEntityTag)
            }
        }

        super.load(tag)
    }
}