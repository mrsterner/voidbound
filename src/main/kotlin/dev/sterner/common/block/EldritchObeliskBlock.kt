package dev.sterner.common.block

import dev.sterner.common.blockentity.EldritchObeliskBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.BooleanOp
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import team.lodestar.lodestone.systems.block.LodestoneEntityBlock

class EldritchObeliskBlock(properties: Properties) : LodestoneEntityBlock<EldritchObeliskBlockEntity>(properties) {

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shapes.join(shape, shape2, BooleanOp.OR)
    }

    companion object {
        val shape = Shapes.create(1 / 16.0, 8 / 16.0, 1 / 16.0, 15 / 16.0, 1.0, 15 / 16.0)
        val shape2 = Shapes.create(4 / 16.0, 0.0, 4 / 16.0, 12 / 16.0, 8 / 16.0, 12 / 16.0)
    }
}