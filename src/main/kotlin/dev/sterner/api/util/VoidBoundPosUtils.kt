package dev.sterner.api.util

import dev.sterner.common.entity.SoulSteelGolemEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.entity.ai.util.LandRandomPos
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3

object VoidBoundPosUtils {

    fun getRandomNearbyPos(golem: SoulSteelGolemEntity): Vec3 {
        val vec3 = LandRandomPos.getPos(golem, 4, 2)
        return vec3 ?: golem.position()
    }

    /**
     * Returns a list of positions corresponding to some random poses around a block
     */
    fun getFaceCoords(level: Level, blockState: BlockState, pos: BlockPos): List<Vec3> {
        val random = level.random
        val i: Int = pos.x
        val j: Int = pos.y
        val k: Int = pos.z
        val f = 0.03 // Offset for particle positioning

        // Get the bounding box (AABB) of the block
        val shape = blockState.getShape(level, pos)
        if (!shape.isEmpty) {
            val aABB: AABB = blockState.getShape(level, pos).bounds().inflate(f)

            // Calculate the min and max coordinates for the bounding box
            val minX = i + aABB.minX
            val minY = j + aABB.minY
            val minZ = k + aABB.minZ
            val maxX = i + aABB.maxX
            val maxY = j + aABB.maxY
            val maxZ = k + aABB.maxZ

            // List to store particle positions
            val particlePositions = mutableListOf<Vec3>()

            // Function to generate random positions on a face
            fun addFaceParticles(x1: Double, y1: Double, z1: Double, x2: Double, y2: Double, z2: Double) {
                val particleCount = 1 // Number of particles per face
                for (i in 0..particleCount) {
                    val x = random.nextDouble() * (x2 - x1) + x1
                    val y = random.nextDouble() * (y2 - y1) + y1
                    val z = random.nextDouble() * (z2 - z1) + z1
                    particlePositions.add(Vec3(x, y, z))
                }
            }

            // Front face (minZ)
            addFaceParticles(minX, minY, minZ, maxX, maxY, minZ)

            // Back face (maxZ)
            addFaceParticles(minX, minY, maxZ, maxX, maxY, maxZ)

            // Left face (minX)
            addFaceParticles(minX, minY, minZ, minX, maxY, maxZ)

            // Right face (maxX)
            addFaceParticles(maxX, minY, minZ, maxX, maxY, maxZ)

            // Bottom face (minY)
            addFaceParticles(minX, minY, minZ, maxX, minY, maxZ)

            // Top face (maxY)
            addFaceParticles(minX, maxY, minZ, maxX, maxY, maxZ)

            return particlePositions
        }

        return listOf()
    }

    /**
     * Returns a list of positions corresponding to some random poses on one side of a block
     */
    fun getFaceCoords(level: Level, blockState: BlockState, pos: BlockPos, side: Direction): Vec3 {
        val random = level.random
        val i: Int = pos.x
        val j: Int = pos.y
        val k: Int = pos.z
        val f = 0.03f
        val aABB: AABB = blockState.getShape(level, pos).bounds()
        var x: Double = i.toDouble() + random.nextDouble() * (aABB.maxX - aABB.minX - 0.2f) + 0.1f + aABB.minX
        var y: Double = j.toDouble() + random.nextDouble() * (aABB.maxY - aABB.minY - 0.2f) + 0.1f + aABB.minY
        var z: Double = k.toDouble() + random.nextDouble() * (aABB.maxZ - aABB.minZ - 0.2f) + 0.1f + aABB.minZ
        if (side == Direction.DOWN) {
            y = j.toDouble() + aABB.minY - f
        }

        if (side == Direction.UP) {
            y = j.toDouble() + aABB.maxY + f
        }

        if (side == Direction.NORTH) {
            z = k.toDouble() + aABB.minZ - f
        }

        if (side == Direction.SOUTH) {
            z = k.toDouble() + aABB.maxZ + f
        }

        if (side == Direction.WEST) {
            x = i.toDouble() + aABB.minX - f
        }

        if (side == Direction.EAST) {
            x = i.toDouble() + aABB.maxX + f
        }

        return Vec3(x, y, z)
    }
}