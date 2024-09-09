package dev.sterner.common.block

import dev.sterner.common.blockentity.SpiritStabilizerBlockEntity
import dev.sterner.registry.VoidBoundBlockEntityTypeRegistry
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class SpiritBinderStabilizerBlock(properties: Properties) : BaseEntityBlock(
    properties.noOcclusion()
) {

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity? {
        return VoidBoundBlockEntityTypeRegistry.SPIRIT_BINDER_STABILIZER.get().create(pos, state)
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        blockState: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> {
        return BlockEntityTicker { _, _, _, blockEntity ->
            if (blockEntity is SpiritStabilizerBlockEntity) {
                (blockEntity as SpiritStabilizerBlockEntity).tick()
            }
        }
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return makeShape()
    }

    companion object {
        fun makeShape(): VoxelShape {
            var shape = Shapes.empty()
            shape = Shapes.join(shape, Shapes.box(0.0, 11.0 / 16, 0.0, 16 / 16.0, 16 / 16.0, 16 / 16.0), BooleanOp.OR)
            shape = Shapes.join(
                shape,
                Shapes.box(3 / 16.0, 8.0 / 16, 3 / 16.0, 13 / 16.0, 16 / 16.0, 13 / 16.0),
                BooleanOp.OR
            )

            return shape
        }
    }
}