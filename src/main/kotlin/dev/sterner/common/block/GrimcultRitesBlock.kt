package dev.sterner.common.block

import dev.sterner.registry.VoidBoundItemRegistry
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.HorizontalDirectionalBlock
import net.minecraft.world.level.block.LecternBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class GrimcultRitesBlock(properties: Properties) : HorizontalDirectionalBlock(properties) {

    init {
        this.registerDefaultState(
            stateDefinition.any().setValue(LecternBlock.FACING, Direction.NORTH)
        )
    }

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {
        if (player.isShiftKeyDown && player.mainHandItem.isEmpty) {
            player.setItemInHand(InteractionHand.MAIN_HAND, VoidBoundItemRegistry.GRIMCULT_RITES.get().defaultInstance)
            level.destroyBlock(pos, false)
        }
        return super.use(state, level, pos, player, hand, hit)
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(FACING)
    }

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shapes.create(4 / 16.0, 0.0, 4 / 16.0, 12 / 16.0, 4.0 / 16, 12 / 16.0)
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)
    }
}