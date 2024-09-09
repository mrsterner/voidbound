package dev.sterner.common.block

import dev.sterner.common.blockentity.OsmoticEnchanterBlockEntity
import dev.sterner.common.menu.OsmoticEnchanterMenu
import dev.sterner.registry.VoidBoundItemRegistry
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.BlockGetter
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.*
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityTicker
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.shapes.CollisionContext
import net.minecraft.world.phys.shapes.Shapes
import net.minecraft.world.phys.shapes.VoxelShape

class OsmoticEnchanterBlock(properties: Properties) : BaseEntityBlock(properties) {

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity {
        return OsmoticEnchanterBlockEntity(pos, state)
    }

    override fun <T : BlockEntity?> getTicker(
        level: Level,
        blockState: BlockState,
        blockEntityType: BlockEntityType<T>
    ): BlockEntityTicker<T> {
        return BlockEntityTicker { _, _, _, blockEntity ->
            if (blockEntity is OsmoticEnchanterBlockEntity) {
                (blockEntity as OsmoticEnchanterBlockEntity).tick()
            }
        }
    }

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        hand: InteractionHand,
        hit: BlockHitResult
    ): InteractionResult {

        val handItem = player.mainHandItem
        if (handItem.`is`(VoidBoundItemRegistry.GRIMCULT_RITES.get()) && !state.getValue(BlockStateProperties.HAS_BOOK)) {
            level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.HAS_BOOK, true))
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY)
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS)
            return InteractionResult.SUCCESS
        } else if (player.isShiftKeyDown && handItem.isEmpty && state.getValue(BlockStateProperties.HAS_BOOK)) {
            player.setItemInHand(InteractionHand.MAIN_HAND, ItemStack(VoidBoundItemRegistry.GRIMCULT_RITES.get()))
            level.setBlockAndUpdate(pos, state.setValue(BlockStateProperties.HAS_BOOK, false))
            level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.BLOCKS)
            return InteractionResult.SUCCESS
        }

        if (level.getBlockEntity(pos) is OsmoticEnchanterBlockEntity) {
            val osmoticEnchanter = level.getBlockEntity(pos) as OsmoticEnchanterBlockEntity

            // Check if there are selected enchantments
            if (!osmoticEnchanter.activated) {
                osmoticEnchanter.activeEnchantments.clear()
                osmoticEnchanter.availableEnchantments.clear()
            }

            osmoticEnchanter.refreshEnchants()

            player.openMenu(object : ExtendedScreenHandlerFactory {
                override fun writeScreenOpeningData(player: ServerPlayer, buf: FriendlyByteBuf) {
                    buf.writeBlockPos(pos)
                }

                override fun getDisplayName(): Component {
                    return Component.translatable("osmotic_enchanter")
                }

                override fun createMenu(i: Int, inventory: Inventory, player: Player): AbstractContainerMenu {
                    return OsmoticEnchanterMenu(i, inventory, pos)
                }
            })
            return InteractionResult.SUCCESS
        }



        return super.use(state, level, pos, player, hand, hit)
    }

    override fun onRemove(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        newState: BlockState,
        movedByPiston: Boolean
    ) {
        if (state.`is`(newState.block)) {
            return
        }
        val blockEntity = level.getBlockEntity(pos)
        if (blockEntity is OsmoticEnchanterBlockEntity) {
            Containers.dropContents(level, pos, blockEntity.inventory.stacks)
            level.updateNeighbourForOutputSignal(pos, this)
        }
        if (state.getValue(BlockStateProperties.HAS_BOOK)) {
            Containers.dropItemStack(
                level,
                pos.x + 0.5,
                pos.y + 0.5,
                pos.z + 0.5,
                ItemStack(VoidBoundItemRegistry.GRIMCULT_RITES.get())
            )
        }
        super.onRemove(state, level, pos, newState, movedByPiston)
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun getOcclusionShape(state: BlockState?, level: BlockGetter?, pos: BlockPos?): VoxelShape {
        return SHAPE_COMMON
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState {
        return defaultBlockState().setValue(LecternBlock.FACING, context.horizontalDirection.opposite)
    }

    override fun getCollisionShape(
        state: BlockState?,
        level: BlockGetter?,
        pos: BlockPos?,
        context: CollisionContext?
    ): VoxelShape {
        return LecternBlock.SHAPE_COLLISION
    }

    override fun getShape(
        state: BlockState,
        level: BlockGetter?,
        pos: BlockPos?,
        context: CollisionContext?
    ): VoxelShape {
        return when (state.getValue(LecternBlock.FACING) as Direction) {
            Direction.NORTH -> SHAPE_NORTH
            Direction.SOUTH -> SHAPE_SOUTH
            Direction.EAST -> SHAPE_EAST
            Direction.WEST -> SHAPE_WEST
            else -> SHAPE_COMMON
        }
    }

    override fun rotate(state: BlockState, rotation: Rotation): BlockState {
        return state.setValue(LecternBlock.FACING, rotation.rotate(state.getValue(LecternBlock.FACING)))
    }

    override fun mirror(state: BlockState, mirror: Mirror): BlockState {
        return state.rotate(mirror.getRotation(state.getValue(LecternBlock.FACING)))
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(LecternBlock.FACING, BlockStateProperties.HAS_BOOK)
    }


    init {
        this.registerDefaultState(
            stateDefinition.any().setValue(LecternBlock.FACING, Direction.NORTH)
                .setValue(BlockStateProperties.HAS_BOOK, false)
        )
    }

    companion object {

        val SHAPE_BASE: VoxelShape = box(1.0, 0.0, 1.0, 15.0, 3.0, 15.0)

        val SHAPE_POST: VoxelShape = box(3.0, 2.0, 3.0, 13.0, 14.0, 13.0)

        val SHAPE_COMMON: VoxelShape = Shapes.or(SHAPE_BASE, SHAPE_POST)

        val SHAPE_WEST: VoxelShape = Shapes.or(
            box(1.0, 11.5, 0.5, 5.333333, 13.25, 15.5),
            box(5.333333, 13.5, 0.5, 9.666667, 15.25, 15.5),
            box(9.666667, 15.5, 0.5, 12.5, 17.25, 15.5),
            SHAPE_COMMON
        )

        val SHAPE_NORTH: VoxelShape = Shapes.or(
            box(0.5, 11.5, 1.0, 15.5, 13.25, 5.333333),
            box(0.5, 13.5, 5.333333, 15.5, 15.25, 9.666667),
            box(0.5, 15.5, 9.666667, 15.5, 17.25, 12.5),
            SHAPE_COMMON
        )

        val SHAPE_EAST: VoxelShape = Shapes.or(
            box(10.666667, 11.5, 0.5, 13.5, 13.25, 15.5),
            box(6.333333, 13.5, 0.5, 10.666667, 15.25, 15.5),
            box(3.5, 15.5, 0.5, 6.333333, 17.25, 15.5),
            SHAPE_COMMON
        )

        val SHAPE_SOUTH: VoxelShape = Shapes.or(
            box(0.5, 11.5, 10.666667, 15.5, 13.25, 14.5),
            box(0.5, 13.5, 6.333333, 15.5, 15.25, 10.666667),
            box(0.5, 15.5, 3.5, 15.5, 17.25, 6.333333),
            SHAPE_COMMON
        )
    }
}