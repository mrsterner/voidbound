package dev.sterner.common.item.focus

import dev.sterner.api.util.VoidBoundBlockUtils
import dev.sterner.registry.VoidBoundBlockRegistry
import dev.sterner.registry.VoidBoundTags
import eu.pb4.common.protection.api.CommonProtection
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult
import java.awt.Color
import java.util.*

class PortableHoleFocusItem(properties: Properties) :
    AbstractFocusItem(properties) {
    override fun color(): Color {
        return Color(0, 0, 0)
    }

    override fun endColor(): Color {
        return Color(0, 0, 0)
    }

    override fun isVoid(): Boolean {
        return true
    }

    fun onAbilityRightClick(stack: ItemStack, level: Level, player: Player, hitResult: HitResult) {
        if (hitResult.type == HitResult.Type.BLOCK) {
            val blockHit = hitResult as BlockHitResult
            val blockPos = blockHit.blockPos
            var (hx, hy, hz) = Triple(blockPos.x, blockPos.y, blockPos.z)

            val maxDistance = 32
            var distanceTraveled = 0

            // Iterate through blocks to check for obstacles and determine the distance
            for (distance in 0 until maxDistance) {
                distanceTraveled = distance
                val currentPos = BlockPos(hx, hy, hz)
                val blockState = level.getBlockState(currentPos)

                if (blockState.`is`(VoidBoundTags.PORTABLE_HOLE_BLACKLIST)) {
                    return
                }

                if (!canBlockBeBroken(level, currentPos, player)) {
                    return
                }

                if (blockState.isAir) {
                    break
                }

                if (distanceTraveled >= maxDistance - 1) {
                    return
                }

                // Move in the direction of the block hit
                when (blockHit.direction) {
                    Direction.DOWN -> hy--
                    Direction.UP -> hy++
                    Direction.NORTH -> hz--
                    Direction.SOUTH -> hz++
                    Direction.WEST -> hx--
                    Direction.EAST -> hx++
                }
            }

            createHoleGrid(level, blockHit, player, distanceTraveled)
            playTeleportEffect(level, hitResult, player)
        }
    }

    /**
     * Verifies if the block can be broken by the player.
     */
    private fun canBlockBeBroken(level: Level, pos: BlockPos, player: Player): Boolean {
        return VoidBoundBlockUtils.canBlockBreak(level, pos) &&
                CommonProtection.canBreakBlock(level, pos, player.gameProfile, player)
    }

    /**
     * Creates a 3x3 grid of holes in the direction of the player's hit.
     */
    private fun createHoleGrid(level: Level, blockHit: BlockHitResult, player: Player, distance: Int) {
        val centerPos = blockHit.blockPos
        val direction = blockHit.direction
        val (dirX, dirY) = getPerpendicularDirections(direction)

        for (x in -1..1) {
            for (y in -1..1) {
                val offsetPos = centerPos
                    .relative(dirX, x)
                    .relative(dirY, y)

                createHole(player.uuid, level, offsetPos, direction, distance)
            }
        }

        // Swing player hand to indicate action completion
        player.swing(InteractionHand.MAIN_HAND)
    }

    /**
     * Plays the teleport sound effect when the hole is created.
     */
    private fun playTeleportEffect(level: Level, hitResult: HitResult, player: Player) {
        if (!level.isClientSide) {
            level.playSound(
                null,
                hitResult.location.x + 0.5,
                hitResult.location.y + 0.5,
                hitResult.location.z + 0.5,
                SoundEvents.ENDERMAN_TELEPORT,
                SoundSource.BLOCKS,
                1.0f,
                1.0f
            )
        }
    }

    companion object {
        /**
         * Determines the perpendicular directions based on the block hit direction.
         */
        private fun getPerpendicularDirections(direction: Direction): Pair<Direction, Direction> {
            return when (direction) {
                Direction.UP, Direction.DOWN -> Pair(Direction.NORTH, Direction.EAST)
                Direction.NORTH, Direction.SOUTH -> Pair(Direction.UP, Direction.EAST)
                Direction.WEST, Direction.EAST -> Pair(Direction.UP, Direction.NORTH)
                else -> throw IllegalArgumentException("Invalid direction: $direction")
            }
        }

        /**
         * Creates a hole at the specified position by replacing the block.
         */
        fun createHole(uuid: UUID, level: Level, pos: BlockPos, direction: Direction, distance: Int) {
            val oldState = level.getBlockState(pos)
            val player = level.getPlayerByUUID(uuid)

            if (oldState.`is`(VoidBoundTags.PORTABLE_HOLE_BLACKLIST) || !canBreakBlock(level, pos, player)) {
                return
            }

            val oldEntity = level.getBlockEntity(pos)
            val holeState = VoidBoundBlockRegistry.PORTABLE_HOLE.get().defaultBlockState()

            // Replace the block with a portable hole and create corresponding block entity
            level.removeBlockEntity(pos)
            level.setBlock(pos, holeState, 1 shl 3 or (1 shl 1))
            level.removeBlockEntity(pos)
            level.setBlockEntity(
                VoidBoundBlockRegistry.PORTABLE_HOLE.get().createWithData(
                    uuid,
                    pos,
                    oldState,
                    oldEntity,
                    direction,
                    distance
                )
            )
        }

        /**
         * Checks if the block can be broken at the given position by the player.
         */
        private fun canBreakBlock(level: Level, pos: BlockPos, player: Player?): Boolean {
            return VoidBoundBlockUtils.canBlockBreak(level, pos) &&
                    CommonProtection.canBreakBlock(level, pos, player?.gameProfile, player)
        }
    }
}