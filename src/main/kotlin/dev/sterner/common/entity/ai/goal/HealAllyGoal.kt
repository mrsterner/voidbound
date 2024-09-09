package dev.sterner.common.entity.ai.goal

import dev.sterner.common.entity.AbstractCultistEntity
import dev.sterner.common.entity.GrimcultClericEntity
import dev.sterner.networking.HeartParticlePacket
import dev.sterner.registry.VoidBoundPacketRegistry
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.util.Mth
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.goal.Goal
import org.joml.Vector3f
import java.util.*
import kotlin.math.sqrt


class HealAllyGoal(
    private val healer: GrimcultClericEntity, movespeed: Double,
    private val attackIntervalMin: Int, maxAttackTime: Int, maxAttackDistanceIn: Float
) :
    Goal() {
    private var mob: LivingEntity? = null
    private var rangedAttackTime = -1
    private val entityMoveSpeed = movespeed
    private var seeTime = 0
    private val maxRangedAttackTime = maxAttackTime
    private val attackRadius = maxAttackDistanceIn
    private val maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn

    init {
        this.flags = (EnumSet.of(Flag.MOVE, Flag.LOOK))
    }

    override fun canUse(): Boolean {

        val list: List<AbstractCultistEntity> = healer.level().getEntitiesOfClass(
            AbstractCultistEntity::class.java, healer.boundingBox.inflate(10.0, 3.0, 10.0)
        )
        if (list.isNotEmpty()) {
            for (mob in list) {
                if ((mob.isAlive &&
                            mob.health < mob.maxHealth &&
                            mob != healer &&
                            mob.isAlive
                            )
                ) {
                    this.mob = mob
                    return true
                }
            }
        }
        return false
    }

    override fun canContinueToUse(): Boolean {
        return this.canUse() && (mob != null) && (mob!!.health < mob!!.maxHealth)
    }

    override fun stop() {
        this.mob = null
        this.seeTime = 0
        this.rangedAttackTime = 0
        healer.setIsHealing(false)
    }

    override fun tick() {
        if (mob == null) return
        val d0: Double = healer.distanceToSqr(mob!!)

        healer.setIsHealing(true)

        healer.lookControl.setLookAt(mob!!)
        if (!(d0 > maxAttackDistance.toDouble())) {
            healer.navigation.stop()
        } else {
            healer.navigation.moveTo(this.mob!!, this.entityMoveSpeed)
        }
        if (mob!!.distanceTo(healer) <= 3.0) {
            healer.moveControl.strafe(-0.5f, 0f)
        }
        if (--this.rangedAttackTime == 0) {
            val f = this.attackRadius
            this.healAlly(mob!!)
            this.rangedAttackTime =
                Mth.floor(f * (this.maxRangedAttackTime - this.attackIntervalMin).toFloat() + attackIntervalMin.toFloat())
        } else if (this.rangedAttackTime < 0) {
            this.rangedAttackTime = Mth.floor(
                Mth.lerp(
                    sqrt(d0) / attackRadius.toDouble(),
                    this.attackIntervalMin.toDouble(),
                    this.maxAttackDistance.toDouble()
                )
            )
        }
    }

    private fun healAlly(mob: LivingEntity) {
        mob.heal(15f)
        mob.level().playSound(null, mob, SoundEvents.EVOKER_CAST_SPELL, SoundSource.NEUTRAL, 1.0f, 1.0f)
        for (player in PlayerLookup.tracking(mob)) {
            for (i in 0..20) {
                VoidBoundPacketRegistry.VOID_BOUND_CHANNEL.sendToClient(
                    HeartParticlePacket(
                        Vector3f(
                            mob.getRandomX(0.5).toFloat(), mob.randomY.toFloat(), mob.getRandomZ(0.5).toFloat()
                        )
                    ),
                    player
                )
            }
        }
    }
}