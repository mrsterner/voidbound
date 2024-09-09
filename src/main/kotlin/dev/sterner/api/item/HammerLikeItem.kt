package dev.sterner.api.item

import io.github.fabricators_of_create.porting_lib.event.common.BlockEvents.BreakEvent
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.BlockTags
import net.minecraft.tags.TagKey
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Tier
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.BlockHitResult
import java.util.function.Consumer

/**
 * Implement to add a classic hammer behaviour to an item, this means an item which should break multiple blocks on one break
 */
interface HammerLikeItem {

    fun getRadius(): Int
    fun getDepth(): Int
    fun getHammerTier(): Tier
    fun getBlockTags(): TagKey<Block>

    /**
     * Performs an area-of-effect block-breaking action.
     */
    fun executeHammerAction(
        level: ServerLevel,
        blockPos: BlockPos,
        blockState: BlockState,
        itemStack: ItemStack,
        player: ServerPlayer
    ) {
        // Only proceed if not on the client side and the block can be broken
        if (level.isClientSide || blockState.getDestroySpeed(level, blockPos) == 0.0f) return

        // Skip if the player is crouching
        if (player.isCrouching) return

        // Raycast to find block the player is pointing at
        val hitResult = player.pick(20.0, 0.0f, false) as? BlockHitResult ?: return

        // Perform the AOE block break
        breakNearbyBlocks(hitResult, blockPos, itemStack, level, player)
    }

    /**
     * Determines if the hammer can break the block based on its tier.
     */
    fun isToolEffective(state: BlockState): Boolean {
        val tierLevel = getHammerTier().level
        return when {
            tierLevel < 3 && state.`is`(BlockTags.NEEDS_DIAMOND_TOOL) -> false
            tierLevel < 2 && state.`is`(BlockTags.NEEDS_IRON_TOOL) -> false
            tierLevel < 1 && state.`is`(BlockTags.NEEDS_STONE_TOOL) -> false
            else -> state.`is`(getBlockTags())
        }
    }

    /**
     * Determines if a block can be destroyed.
     */
    private fun canBreakBlock(targetState: BlockState, level: Level, pos: BlockPos): Boolean {
        return targetState.getDestroySpeed(level, pos) > 0 && level.getBlockEntity(pos) == null
    }

    /**
     * Finds and breaks blocks near the initial hit position in an area.
     */
    fun breakNearbyBlocks(
        hitResult: BlockHitResult,
        blockPos: BlockPos,
        hammerStack: ItemStack,
        level: Level,
        entity: LivingEntity
    ) {
        if (entity !is ServerPlayer) return

        val direction = hitResult.direction
        val areaOfEffect = calculateAoeBoundingBox(blockPos, direction, getRadius(), getDepth())

        if (!entity.isCreative && hammerStack.damageValue >= hammerStack.maxDamage - 1) return

        var blocksBroken = 0
        val iterator = BlockPos.betweenClosedStream(areaOfEffect).iterator()
        val removedBlocks = mutableSetOf<BlockPos>()

        while (iterator.hasNext()) {
            val pos = iterator.next()

            if (!entity.isCreative && hammerStack.damageValue + (blocksBroken + 1) >= hammerStack.maxDamage - 1) break

            val targetState = level.getBlockState(pos)
            if (pos == blockPos || removedBlocks.contains(pos) || !canBreakBlock(targetState, level, pos)) continue

            if (!isToolEffective(targetState)) continue

            // Trigger block break event
            BreakEvent.BLOCK_BREAK.invoker().onBlockBreak(BreakEvent(level, pos, targetState, entity))

            removedBlocks.add(pos)
            level.destroyBlock(pos, false, entity)

            if (!entity.isCreative) {
                handleBlockDrops(targetState, pos, hammerStack, level, entity, hitResult)
            }

            blocksBroken++
        }

        if (blocksBroken > 0 && !entity.isCreative) {
            hammerStack.hurtAndBreak(
                blocksBroken, entity
            ) { it.broadcastBreakEvent(EquipmentSlot.MAINHAND) }
        }
    }

    /**
     * Calculates the area of effect for the hammer based on direction, radius, and depth.
     */
    fun calculateAoeBoundingBox(blockPos: BlockPos, direction: Direction, radius: Int, depth: Int): BoundingBox {
        val size = radius / 2
        val offset = size - 1

        return when (direction) {
            Direction.DOWN, Direction.UP -> BoundingBox(
                blockPos.x - size,
                blockPos.y - if (direction == Direction.UP) depth - 1 else 0,
                blockPos.z - size,
                blockPos.x + size,
                blockPos.y + if (direction == Direction.DOWN) depth - 1 else 0,
                blockPos.z + size
            )

            Direction.NORTH, Direction.SOUTH -> BoundingBox(
                blockPos.x - size,
                blockPos.y - size + offset,
                blockPos.z - if (direction == Direction.SOUTH) depth - 1 else 0,
                blockPos.x + size,
                blockPos.y + size + offset,
                blockPos.z + if (direction == Direction.NORTH) depth - 1 else 0
            )

            Direction.WEST, Direction.EAST -> BoundingBox(
                blockPos.x - if (direction == Direction.EAST) depth - 1 else 0,
                blockPos.y - size + offset,
                blockPos.z - size,
                blockPos.x + if (direction == Direction.WEST) depth - 1 else 0,
                blockPos.y + size + offset,
                blockPos.z + size
            )
        }
    }

    /**
     * Handles block drops when the hammer breaks a block.
     */
    private fun handleBlockDrops(
        targetState: BlockState,
        pos: BlockPos,
        hammerStack: ItemStack,
        level: Level,
        entity: LivingEntity,
        hitResult: BlockHitResult
    ) {
        val isCorrectTool = hammerStack.isCorrectToolForDrops(targetState)
        if (isCorrectTool) {
            targetState.spawnAfterBreak(level as ServerLevel, pos, hammerStack, true)
            val drops = Block.getDrops(targetState, level, pos, level.getBlockEntity(pos), entity, hammerStack)
            drops.forEach { drop ->
                Block.popResourceFromFace(level, pos, hitResult.direction, drop)
            }
        }
    }
}