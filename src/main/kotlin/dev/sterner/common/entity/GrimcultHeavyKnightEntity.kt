package dev.sterner.common.entity

import dev.sterner.common.entity.ai.goal.RaiseShieldGoal
import dev.sterner.registry.VoidBoundEntityTypeRegistry
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.tags.DamageTypeTags
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal
import net.minecraft.world.item.AxeItem
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.level.Level


class GrimcultHeavyKnightEntity(level: Level) :
    AbstractCultistEntity( VoidBoundEntityTypeRegistry.GRIMCULT_HEAVY_KNIGHT_ENTITY.get(), level) {

    var shieldCoolDown: Int = 0

    fun setShieldBlocking(isShieldBlocking: Boolean) {
        entityData.set(IS_SHIELD_BLOCKING, isShieldBlocking)
    }

    private fun isShieldBlocking(): Boolean {
        return entityData.get(IS_SHIELD_BLOCKING)
    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        entityData.define(IS_SHIELD_BLOCKING, false)
    }


    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(2, MeleeAttackGoal(this, 1.0, false))
        goalSelector.addGoal(3, RaiseShieldGoal(this))

        goalSelector.addGoal(2, object : MeleeAttackGoal(this, 1.0, false) {
            override fun checkAndPerformAttack(enemy: LivingEntity, distToEnemySqr: Double) {
                val d0: Double = this.getAttackReachSqr(enemy)
                if (distToEnemySqr <= d0 && this.ticksUntilNextAttack <= 0) {
                    this.resetAttackCooldown()
                    this.mob.stopUsingItem()
                    val cultist = mob as GrimcultHeavyKnightEntity
                    if (cultist.shieldCoolDown == 0) cultist.shieldCoolDown = 8
                    this.mob.swing(InteractionHand.MAIN_HAND)
                    this.mob.doHurtTarget(enemy)
                }
            }
        })
    }

    override fun getArmPose(): GrimcultArmPose {
        if (this.isShieldBlocking()) {
            return GrimcultArmPose.BLOCK
        }

        return super.getArmPose()
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putInt("ShieldCooldown", this.shieldCoolDown)
        super.addAdditionalSaveData(compound)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        this.shieldCoolDown = compound.getInt("ShieldCooldown")
        super.readAdditionalSaveData(compound)
    }

    override fun baseTick() {
        if (this.shieldCoolDown > 0) {
            --this.shieldCoolDown
        }

        super.baseTick()
    }

    override fun blockUsingShield(attacker: LivingEntity) {
        super.blockUsingShield(attacker)
        if (attacker.mainHandItem.item is AxeItem) this.disableShield(true)
    }

    override fun isBlocking(): Boolean {
        if (isShieldBlocking()) {
            return true
        }

        return super.isBlocking()
    }

    override fun hurt(source: DamageSource, amount: Float): Boolean {

        if (amount > 0.0f && this.isDamageSourceBlocked(source)) {
            this.hurtCurrentlyUsedShield(amount)

            val directEntity = source.directEntity
            if (!source.`is`(DamageTypeTags.IS_PROJECTILE) && directEntity is LivingEntity) {
                this.blockUsingShield(directEntity)
            }
        }

        return super.hurt(source, amount)
    }

    private fun disableShield(increase: Boolean) {
        var chance = 0.25f + EnchantmentHelper.getBlockEfficiency(this).toFloat() * 0.05f
        if (increase) chance += 0.75.toFloat()
        if (random.nextFloat() < chance) {
            this.shieldCoolDown = 100
            this.stopUsingItem()
            level().broadcastEntityEvent(this, 30.toByte())
        }
    }


    companion object {


        val IS_SHIELD_BLOCKING: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            AbstractCultistEntity::class.java, EntityDataSerializers.BOOLEAN
        )

        fun createGrimcultAttributes(): AttributeSupplier.Builder? {
            return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 30.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.75)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.ARMOR)
                .add(Attributes.ARMOR_TOUGHNESS)
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.ATTACK_KNOCKBACK)
        }
    }
}