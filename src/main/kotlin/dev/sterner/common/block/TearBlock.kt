package dev.sterner.common.block

import dev.sterner.VoidBound
import dev.sterner.api.util.VoidBoundUtils
import dev.sterner.common.components.VoidBoundRevelationComponent
import dev.sterner.registry.VoidBoundComponentRegistry
import dev.sterner.registry.VoidBoundItemRegistry
import dev.sterner.registry.VoidBoundPacketRegistry
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Containers
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.LevelAccessor
import net.minecraft.world.level.LevelReader
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class TearBlock(val type: Type, properties: Properties) : Block(properties) {

    override fun getShape(state: BlockState, level: BlockGetter, pos: BlockPos, context: CollisionContext): VoxelShape {
        return Shapes.box(6.0 / 16, 13.0 / 16, 6.0 / 16, 10.0 / 16, 16.0 / 16, 10.0 / 16)
    }

    override fun canSurvive(state: BlockState, level: LevelReader, pos: BlockPos): Boolean {
        return level.getBlockState(pos.above()).block == Blocks.END_STONE || level.getBlockState(pos.above()).block == Blocks.NETHERRACK
    }

    override fun updateShape(
        state: BlockState,
        direction: Direction,
        neighborState: BlockState,
        level: LevelAccessor,
        pos: BlockPos,
        neighborPos: BlockPos
    ): BlockState {
        return if (!state.canSurvive(level, pos)) Blocks.AIR.defaultBlockState() else super.updateShape(
            state,
            direction,
            neighborState,
            level,
            pos,
            neighborPos
        )
    }

    override fun playerDestroy(
        level: Level,
        player: Player,
        pos: BlockPos,
        state: BlockState,
        blockEntity: BlockEntity?,
        tool: ItemStack
    ) {

        val comp = VoidBoundComponentRegistry.VOID_BOUND_REVELATION_COMPONENT.get(player)
        if (comp.isTearKnowledgeComplete()) {

            var item = VoidBoundItemRegistry.TEAR_OF_ENDER.get()
            if (type == Type.BRIMSTONE) {
                item = VoidBoundItemRegistry.TEAR_OF_CRIMSON.get()
            }

            Containers.dropItemStack(level, pos.x + 0.5, pos.y + 0.5, pos.z + 0.5, ItemStack(item))
        }

        super.playerDestroy(level, player, pos, state, blockEntity, tool)
    }

    enum class Type {
        BRIMSTONE,
        ENDER
    }
}