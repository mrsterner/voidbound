package dev.sterner.common.entity

import dev.sterner.api.entity.GolemCore
import dev.sterner.registry.VoidBoundItemRegistry
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundEvents
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3
import java.util.*

/**
 * In case there will e other types of Golems, abstract the important bits
 */
abstract class AbstractGolemEntity(entityType: EntityType<out PathfinderMob>, level: Level) : PathfinderMob(
    entityType,
    level
) {

    var attackAnimationTick = 0

    init {
        this.setMaxUpStep(1.0f)
        this.setCanPickUpLoot(true)
        this.setPersistenceRequired()
    }

    fun dropCore(level: Level, pos: Vec3) {
        if (getGolemCore() != GolemCore.NONE) {
            val item = GolemCore.getItem(getGolemCore())
            if (item != null) {
                Containers.dropItemStack(level, pos.x, pos.y, pos.z, ItemStack(item))
            }
        }
    }

    open fun onPickUpGolem(level: Level, pos: Vec3) {
        Containers.dropItemStack(level, pos.x, pos.y, pos.z, mainHandItem)
        Containers.dropItemStack(level, pos.x, pos.y, pos.z, ItemStack(VoidBoundItemRegistry.SOUL_STEEL_GOLEM.get()))
        this.remove(RemovalReason.CHANGED_DIMENSION)
    }

    override fun defineSynchedData() {
        super.defineSynchedData()
        entityData.define(coreEntityData, GolemCore.NONE.name)
        entityData.define(ownerUUID, Optional.empty())
    }

    override fun addAdditionalSaveData(tag: CompoundTag) {
        super.addAdditionalSaveData(tag)
        GolemCore.writeNbt(tag, getGolemCore())
        if (getOwner().isPresent) {
            tag.putUUID("Owner", getOwner().get())
        }
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        setGolemCore(GolemCore.readNbt(tag))
        if (tag.contains("Owner")) {
            setOwner(tag.getUUID("Owner"))
        }
    }

    override fun doHurtTarget(target: Entity): Boolean {
        this.attackAnimationTick = 10
        level().broadcastEntityEvent(this, 4.toByte())
        return super.doHurtTarget(target)
    }

    override fun aiStep() {
        super.aiStep()
        this.updateSwingTime()
        if (this.attackAnimationTick > 0) {
            attackAnimationTick--
        }
    }

    override fun handleEntityEvent(id: Byte) {
        if (id.toInt() == 4) {
            this.attackAnimationTick = 10
            this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 0.75f, 1.25f)
        } else {
            super.handleEntityEvent(id)
        }
    }

    override fun swing(hand: InteractionHand) {
        this.attackAnimationTick = 10
        super.swing(hand)
    }

    //Boring section

    fun setGolemCore(core: GolemCore) {
        entityData.set(coreEntityData, core.name)
    }

    fun getGolemCore(): GolemCore {
        return GolemCore.valueOf(entityData.get(coreEntityData))
    }

    fun setOwner(uuid: UUID) {
        entityData.set(ownerUUID, Optional.of(uuid))
    }

    fun getOwner(): Optional<UUID> {
        return entityData.get(ownerUUID)
    }

    override fun getHurtSound(damageSource: DamageSource): SoundEvent {
        return SoundEvents.IRON_GOLEM_HURT
    }

    override fun getDeathSound(): SoundEvent {
        return SoundEvents.IRON_GOLEM_DEATH
    }

    override fun playStepSound(pos: BlockPos, state: BlockState) {
        this.playSound(SoundEvents.IRON_GOLEM_STEP, 0.5f, 1.25f)
    }

    companion object {

        var coreEntityData: EntityDataAccessor<String> =
            SynchedEntityData.defineId(SoulSteelGolemEntity::class.java, EntityDataSerializers.STRING)
        var ownerUUID: EntityDataAccessor<Optional<UUID>> =
            SynchedEntityData.defineId(SoulSteelGolemEntity::class.java, EntityDataSerializers.OPTIONAL_UUID)

        fun createGolemAttributes(): AttributeSupplier.Builder {
            return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
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