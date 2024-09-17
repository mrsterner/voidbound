package dev.sterner.api.util

import com.sammy.malum.registry.common.block.BlockRegistry
import dev.sterner.registry.VoidBoundComponentRegistry
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.server.level.ServerPlayer
import net.minecraft.tags.BlockTags
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import kotlin.math.abs

object VoidBoundBlockUtils {

    /**
     * Finds the furthest block from the origin.
     */
    private fun findFurthestBlock(logs: List<BlockPos>, origin: BlockPos): BlockPos {
        return logs.maxByOrNull { it.distManhattan(origin) } ?: origin
    }

    /**
     * Recursively gathers all connected log blocks of the same type.
     * If returnFurthestOnly is true, it returns only the furthest block away from the origin.
     */
    fun gatherConnectedLogs(
        level: Level,
        pos: BlockPos,
        logsToBreak: MutableList<BlockPos>,
        logType: Block,
        returnFurthestOnly: Boolean = false,
        origin: BlockPos = pos
    ): List<BlockPos> {
        if (logsToBreak.size > 256) return logsToBreak

        val surroundingLogs = mutableListOf<BlockPos>()
        val surroundingPositions = BlockPos.betweenClosed(pos.x - 1, pos.y, pos.z - 1, pos.x + 1, pos.y + 1, pos.z + 1)
        surroundingPositions.forEach { surroundingLogs.add(it.immutable()) }

        val checkPositions = surroundingLogs.filter { !logsToBreak.contains(it) && isLogMatch(level, it, logType) }
        logsToBreak.addAll(checkPositions)

        if (checkPositions.isEmpty()) {
            return if (returnFurthestOnly) {
                listOf(findFurthestBlock(logsToBreak, origin))
            } else {
                logsToBreak
            }
        }

        checkPositions.forEach { gatherConnectedLogs(level, it, logsToBreak, logType, returnFurthestOnly, origin) }

        val upwardPosition = pos.above(2)
        return gatherConnectedLogs(level, upwardPosition.immutable(), logsToBreak, logType, returnFurthestOnly, origin)
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