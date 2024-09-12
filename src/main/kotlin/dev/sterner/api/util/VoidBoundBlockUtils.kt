package dev.sterner.api.util

import com.sammy.malum.registry.common.block.BlockRegistry
import dev.sterner.registry.VoidBoundComponentRegistry
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.BlockTags
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import kotlin.math.abs
import kotlin.math.max

object VoidBoundBlockUtils {

    private var lastPos: BlockPos? = null
    private var lastDistance: Double = 0.0

    /**
     * Finds and breaks the furthest log block in a connected set.
     */
    fun breakFurthestLog(level: Level, pos: BlockPos, blockState: BlockState, player: Player): Boolean {
        lastPos = pos
        lastDistance = 0.0

        val reachDistance = if (level.getBlockState(pos).`is`(BlockTags.LOGS)) 2 else 1
        searchForBlocks(level, pos, blockState, reachDistance)

        val blockBroken = breakBlock(level, player, lastPos!!)
        level.markAndNotifyBlock(pos, level.getChunkAt(pos), blockState, blockState, 3, 20)

        if (blockBroken && level.getBlockState(pos).`is`(BlockTags.LOGS)) {
            level.markAndNotifyBlock(pos, level.getChunkAt(pos), blockState, blockState, 3, 20)

            for (xOffset in -3..3) {
                for (yOffset in -3..3) {
                    for (zOffset in -3..3) {
                        val offsetPos = lastPos!!.offset(xOffset, yOffset, zOffset)
                        level.scheduleTick(offsetPos, level.getBlockState(offsetPos).block, 20)
                    }
                }
            }
        }

        return blockBroken
    }

    /**
     * Handles breaking the block, applying any tool enchantments or player abilities.
     */
    private fun breakBlock(level: Level, player: Player, pos: BlockPos): Boolean {
        if (player is ServerPlayer) {
            val blockState = level.getBlockState(pos)
            val blockEntity = level.getBlockEntity(pos)

            val successfulBreak: Boolean = if (player.abilities.instabuild) {
                removeBlock(player, pos)
            } else {
                val itemStack = player.mainHandItem
                val canDestroy = blockState.canEntityDestroy(level, pos, player)
                val isRemoved = removeBlock(player, pos, canDestroy)

                if (isRemoved && canDestroy) {
                    val enchantedStack = applyFortune(itemStack)
                    blockState.block.playerDestroy(level, player, pos, blockState, blockEntity, enchantedStack)
                }

                isRemoved
            }

            return successfulBreak
        }

        return false
    }

    /**
     * Removes the block and applies any block-breaking logic.
     */
    private fun removeBlock(player: Player, pos: BlockPos, canHarvest: Boolean = false): Boolean {
        val blockState = player.level().getBlockState(pos)

        return blockState.onDestroyedByPlayer(player.level(), pos, player, canHarvest, blockState.fluidState).also { flag ->
            if (flag) {
                blockState.block.destroy(player.level(), pos, blockState)
            }
        }
    }

    /**
     * Recursively searches for the furthest block within the given reach.
     */
    private fun searchForBlocks(level: Level, pos: BlockPos, blockState: BlockState, reach: Int) {
        for (xOffset in -reach..reach) {
            for (yOffset in reach downTo -reach) {
                for (zOffset in -reach..reach) {
                    if (isOutOfBounds(pos, xOffset, yOffset, zOffset)) return

                    val currentPos = lastPos!!.offset(xOffset, yOffset, zOffset)
                    val currentState = level.getBlockState(currentPos)

                    if (currentState.block == blockState.block && currentState.block.properties.destroyTime >= 0.0f) {
                        val distance = calculateDistance(pos, xOffset, yOffset, zOffset)
                        if (distance > lastDistance) {
                            lastDistance = distance
                            lastPos = currentPos
                            searchForBlocks(level, pos, blockState, reach)
                            return
                        }
                    }
                }
            }
        }
    }

    /**
     * Calculates the squared distance for optimization purposes.
     */
    private fun calculateDistance(pos: BlockPos, xOffset: Int, yOffset: Int, zOffset: Int): Double {
        val xd = lastPos!!.x + xOffset - pos.x
        val yd = lastPos!!.y + yOffset - pos.y
        val zd = lastPos!!.z + zOffset - pos.z
        return (xd * xd + yd * yd + zd * zd).toDouble()
    }

    /**
     * Checks if the position is out of bounds for searching.
     */
    private fun isOutOfBounds(pos: BlockPos, xOffset: Int, yOffset: Int, zOffset: Int): Boolean {
        return abs(lastPos!!.x + xOffset - pos.x) > 24 ||
                abs(lastPos!!.y + yOffset - pos.y) > 48 ||
                abs(lastPos!!.z + zOffset - pos.z) > 24
    }

    /**
     * Recursively gathers all connected log blocks of the same type.
     */
    fun gatherConnectedLogs(
        level: Level,
        pos: BlockPos,
        logsToBreak: MutableList<BlockPos>,
        logType: Block
    ): List<BlockPos> {
        if (logsToBreak.size > 256) return logsToBreak

        val surroundingLogs = mutableListOf<BlockPos>()
        val surroundingPositions = BlockPos.betweenClosed(pos.x - 1, pos.y, pos.z - 1, pos.x + 1, pos.y + 1, pos.z + 1)
        surroundingPositions.forEach { surroundingLogs.add(it.immutable()) }

        val checkPositions = surroundingLogs.filter { !logsToBreak.contains(it) && isLogMatch(level, it, logType) }
        logsToBreak.addAll(checkPositions)

        if (checkPositions.isEmpty()) return logsToBreak

        checkPositions.forEach { gatherConnectedLogs(level, it, logsToBreak, logType) }

        val upwardPosition = pos.above(2)
        return gatherConnectedLogs(level, upwardPosition.immutable(), logsToBreak, logType)
    }

    /**
     * Determines if two logs are the same type or are exposed logs.
     */
    private fun isLogMatch(level: Level, pos: BlockPos, logType: Block): Boolean {
        val log = level.getBlockState(pos).block
        return log == logType || isExposedLog(log, logType)
    }

    /**
     * Special handling for exposed log types.
     */
    private fun isExposedLog(log1: Block, log2: Block): Boolean {
        return log1.defaultBlockState().`is`(BlockRegistry.EXPOSED_RUNEWOOD_LOG.get()) ||
                log2.defaultBlockState().`is`(BlockRegistry.EXPOSED_RUNEWOOD_LOG.get()) ||
                log1.defaultBlockState().`is`(BlockRegistry.EXPOSED_SOULWOOD_LOG.get()) ||
                log2.defaultBlockState().`is`(BlockRegistry.EXPOSED_SOULWOOD_LOG.get())
    }

    /**
     * Applies fortune enchantments to the item stack used for block breaking.
     */
    private fun applyFortune(itemStack: ItemStack): ItemStack {
        val enchantments = EnchantmentHelper.getEnchantments(itemStack).toMutableMap()
        val fortuneLevel = enchantments[Enchantments.BLOCK_FORTUNE] ?: 0

        if (fortuneLevel > 0) {
            enchantments[Enchantments.BLOCK_FORTUNE] = fortuneLevel
        }

        return itemStack.copy().also { EnchantmentHelper.setEnchantments(enchantments, it) }
    }

    /**
    * Returns false if the block being broken is warded by any player
    */
    fun canBlockBreak(level: Level, blockPos: BlockPos): Boolean {
        val comp = VoidBoundComponentRegistry.VOID_BOUND_WORLD_COMPONENT.get(level)
        if (comp.isEmpty()) {
            return true
        }

        if (comp.hasBlockPos(GlobalPos.of(level.dimension(), blockPos))) {
            return false
        }
        return true
    }
}