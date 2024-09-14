package dev.sterner.common.entity.ai.goal

import dev.sterner.common.entity.GrimcultClericEntity
import dev.sterner.registry.VoidBoundEntityTypeRegistry
import net.minecraft.core.BlockPos
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.entity.MobSpawnType
import net.minecraft.world.entity.ai.goal.Goal

class SummonAllyGoal(private val cleric: GrimcultClericEntity) : Goal() {

    private val maxCooldown = 20 * 5
    private var cooldown = maxCooldown

    override fun canUse(): Boolean {
        return cleric.target != null
    }

    override fun stop() {
        super.stop()
        cooldown = maxCooldown
        cleric.setIsHealing(false)
    }

    override fun start() {
        super.start()
        cleric.setIsHealing(true)
    }

    override fun tick() {
        super.tick()
        if (cooldown > 0) {
            cooldown--
        }

        if (cooldown <= 0) {
            cooldown = maxCooldown
            summonAlly(cleric)
        }
    }

    private fun summonAlly(cleric: GrimcultClericEntity) {
        val cultist = if (cleric.random.nextBoolean()) {
            VoidBoundEntityTypeRegistry.GRIMCULT_ARCHER_ENTITY.get().create(cleric.level())
        } else {
            VoidBoundEntityTypeRegistry.GRIMCULT_KNIGHT_ENTITY.get().create(cleric.level())
        }

        val random = cleric.random
        val radius = 3 // Radius within which the cultist will spawn
        var spawnPos: BlockPos

        do {
            // Random offsets within the radius
            val offsetX = Mth.nextDouble(random, -radius.toDouble(), radius.toDouble())
            val offsetZ = Mth.nextDouble(random, -radius.toDouble(), radius.toDouble())

            // Calculate a potential spawn position
            spawnPos = BlockPos.containing(cleric.x + offsetX, cleric.y, cleric.z + offsetZ)
        } while (cleric.blockPosition() == spawnPos)

        // Move the cultist to the calculated spawn position
        cultist!!.moveTo(spawnPos, cultist.yRot, cultist.xRot)
        if (cleric.level() is ServerLevel) {
            cultist.finalizeSpawn(
                cultist.level() as ServerLevel,
                cultist.level().getCurrentDifficultyAt(cultist.blockPosition()),
                MobSpawnType.SPAWNER,
                null,
                null
            )
        }

        // Add the cultist to the world
        cleric.level().addFreshEntity(cultist)
        cleric.level().playSound(null, cleric.onPos, SoundEvents.GHAST_SHOOT, SoundSource.HOSTILE, 0.5f, 1f)
    }
}