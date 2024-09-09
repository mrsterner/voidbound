package dev.sterner.common.entity

import net.minecraft.nbt.CompoundTag
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.MobSpawnType
import net.minecraft.world.entity.SpawnGroupData
import net.minecraft.world.entity.ai.goal.*
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal
import net.minecraft.world.entity.ai.navigation.GroundPathNavigation
import net.minecraft.world.entity.ai.navigation.PathNavigation
import net.minecraft.world.entity.monster.Monster
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.level.ServerLevelAccessor

/**
 * Base class for all cultists with some basic goals
 */
abstract class AbstractCultistEntity(entityType: EntityType<out Monster>, level: Level) : Monster(entityType, level) {

    init {
        this.setPersistenceRequired()
    }

    override fun finalizeSpawn(
        level: ServerLevelAccessor,
        difficulty: DifficultyInstance,
        reason: MobSpawnType,
        spawnData: SpawnGroupData?,
        dataTag: CompoundTag?
    ): SpawnGroupData? {
        val spawnGroupData = super.finalizeSpawn(level, difficulty, reason, spawnData, dataTag)
        (getNavigation() as GroundPathNavigation).setCanOpenDoors(true)
        val randomSource = level.random
        this.populateDefaultEquipmentSlots(randomSource, difficulty)
        this.populateDefaultEquipmentEnchantments(randomSource, difficulty)
        this.setPersistenceRequired()
        return spawnGroupData
    }

    open fun getArmPose(): GrimcultArmPose {
        if (this.isHolding(Items.CROSSBOW)) {
            return GrimcultArmPose.CROSSBOW_HOLD
        }
        if (this.isAggressive) {
            return GrimcultArmPose.ATTACKING
        }
        return GrimcultArmPose.NEUTRAL
    }

    override fun createNavigation(level: Level): PathNavigation {
        return super.createNavigation(level)
    }

    override fun getAmbientSound(): SoundEvent {
        return SoundEvents.VINDICATOR_AMBIENT
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.VINDICATOR_DEATH
    }

    override fun getHurtSound(damageSource: DamageSource): SoundEvent? {
        return SoundEvents.VINDICATOR_HURT
    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(0, FloatGoal(this))
        goalSelector.addGoal(2, OpenDoorGoal(this, false))

        goalSelector.addGoal(7, WaterAvoidingRandomStrollGoal(this, 1.0))
        goalSelector.addGoal(8, LookAtPlayerGoal(this, Player::class.java, 8.0f))
        goalSelector.addGoal(8, RandomLookAroundGoal(this))

        targetSelector.addGoal(
            1, HurtByTargetGoal(
                this,
                AbstractCultistEntity::class.java
            ).setAlertOthers(*arrayOfNulls(0))
        )
        targetSelector.addGoal(
            2, NearestAttackableTargetGoal(
                this as Mob,
                Player::class.java, true
            )
        )
    }

    enum class GrimcultArmPose {
        ATTACKING,
        SPELLCASTING,
        BOW_AND_ARROW,
        CROSSBOW_HOLD,
        CROSSBOW_CHARGE,
        NEUTRAL,
        BLOCK
    }
}