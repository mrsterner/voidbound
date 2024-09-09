package dev.sterner.common.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape
import team.lodestar.lodestone.systems.multiblock.MultiblockComponentBlock

class EldritchObeliskComponentBlock(properties: Properties?) : MultiblockComponentBlock(properties) {

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.INVISIBLE
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shapes.create(1 / 16.0, 0.0, 1 / 16.0, 15 / 16.0, 1.0, 15 / 16.0)
    }
}