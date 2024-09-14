package dev.sterner.common.entity

import dev.sterner.registry.VoidBoundEntityTypeRegistry
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.util.RandomSource
import net.minecraft.world.DifficultyInstance
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.ai.goal.RangedCrossbowAttackGoal
import net.minecraft.world.entity.monster.CrossbowAttackMob
import net.minecraft.world.entity.projectile.Projectile
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.ProjectileWeaponItem
import net.minecraft.world.level.Level

class GrimcultArcherEntity(level: Level) :
    AbstractCultistEntity(VoidBoundEntityTypeRegistry.GRIMCULT_ARCHER_ENTITY.get(), level), CrossbowAttackMob {

    override fun defineSynchedData() {
        super.defineSynchedData()
        entityData.define(IS_CHARGING_CROSSBOW, false)
    }

    override fun getArmPose(): GrimcultArmPose {
        if (this.isChargingCrossbow()) {
            return GrimcultArmPose.CROSSBOW_CHARGE
        }
        return super.getArmPose()
    }

    override fun setChargingCrossbow(chargingCrossbow: Boolean) {
        entityData.set(IS_CHARGING_CROSSBOW, chargingCrossbow)
    }

    private fun isChargingCrossbow(): Boolean {
        return entityData.get(IS_CHARGING_CROSSBOW)
    }

    override fun registerGoals() {
        super.registerGoals()
        goalSelector.addGoal(3, RangedCrossbowAttackGoal(this, 1.0, 8.0f))
    }

    override fun populateDefaultEquipmentSlots(random: RandomSource, difficulty: DifficultyInstance) {
        this.setItemSlot(EquipmentSlot.MAINHAND, ItemStack(Items.CROSSBOW))
    }

    override fun canFireProjectileWeapon(projectileWeapon: ProjectileWeaponItem): Boolean {
        return projectileWeapon === Items.CROSSBOW
    }

    override fun performRangedAttack(target: LivingEntity, velocity: Float) {
        this.performCrossbowAttack(this, 1.6f)
    }

    override fun shootCrossbowProjectile(
        target: LivingEntity,
        crossbowStack: ItemStack,
        projectile: Projectile,
        projectileAngle: Float
    ) {
        this.shootCrossbowProjectile(this, target, projectile, projectileAngle, 1.6f)
    }

    override fun onCrossbowAttackPerformed() {
        this.noActionTime = 0
    }

    companion object {
        fun createGrimcultAttributes(): AttributeSupplier.Builder? {
            return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.05)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.ARMOR)
                .add(Attributes.ARMOR_TOUGHNESS)
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.ATTACK_KNOCKBACK)
        }

        val IS_CHARGING_CROSSBOW: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            AbstractCultistEntity::class.java, EntityDataSerializers.BOOLEAN
        )
    }
}