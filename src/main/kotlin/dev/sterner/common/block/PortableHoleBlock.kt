package dev.sterner.common.block

import dev.sterner.common.blockentity.PortableHoleBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import team.lodestar.lodestone.systems.block.LodestoneEntityBlock
import java.util.*

class PortableHoleBlock(properties: Properties) : LodestoneEntityBlock<PortableHoleBlockEntity>(properties) {

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shapes.empty()
    }

    fun createWithData(
        uuid: UUID,
        pos: BlockPos,
        oldState: BlockState,
        oldEntity: BlockEntity?,
        direction: Direction,
        distance: Int
    ): BlockEntity {
        return PortableHoleBlockEntity(uuid, pos, oldState, oldEntity, direction, distance)
    }
}