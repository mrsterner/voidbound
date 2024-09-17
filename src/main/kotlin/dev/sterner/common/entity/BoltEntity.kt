package dev.sterner.common.entity

import dev.sterner.common.item.focus.ShockFocusItem
import dev.sterner.registry.VoidBoundEntityTypeRegistry
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.level.Level
import kotlin.math.cos
import kotlin.math.sin

/**
 * Used by the shock focus to damage entities and render a lightning bolt
 */
class BoltEntity(entityType: EntityType<BoltEntity>, level: Level) : Entity(entityType, level) {
    private var ambientTick: Int

    constructor(caster: LivingEntity, length: Double) : this(
        VoidBoundEntityTypeRegistry.BOLT_ENTITY.get(),
        caster.level()
    ) {
        yRot = caster.getYHeadRot()
        xRot = caster.xRot
        val rightOffset = -0.5
        val yawRad = Math.toRadians(caster.yRot.toDouble() - 140)
        val pitchRad = Math.toRadians(caster.xRot.toDouble())

        val offsetX = -sin(yawRad) * rightOffset
        val offsetZ = cos(yawRad) * rightOffset
        val offsetY = -sin(pitchRad) * rightOffset

        setPos(caster.x + offsetX, caster.eyeY - offsetY, caster.z + offsetZ)

        this.length = length.toFloat()
    }

    init {
        this.noCulling = true
        this.ambientTick = 6
    }

    var length: Float
        get() = entityData.get(LENGTH)
        set(length) {
            entityData.set(LENGTH, length)
        }

    override fun defineSynchedData() {
        entityData.define(LENGTH, 0f)
    }

    override fun tick() {
        if (ambientTick == 6) {
            level().playSound(
                null,
                BlockPos.containing(position()),
                SoundEvents.GHAST_SHOOT,
                SoundSource.PLAYERS,
                0.5f,
                1.0f
            )
        }
        val forwardOffset = 0.05f

        val forwardX = -sin(Math.toRadians(yRot.toDouble())) * forwardOffset
        val forwardZ = cos(Math.toRadians(yRot.toDouble())) * forwardOffset

        val forwardPosition = this.position().add(forwardX, 0.0, forwardZ)

        ShockFocusItem.spawnChargeParticles(this.level(), this, forwardPosition, 0.5f)

        ambientTick--
        if (ambientTick < 0) {
            remove(RemovalReason.DISCARDED)
        }
        super.tick()
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {

    }

    override fun addAdditionalSaveData(compound: CompoundTag) {

    }

    companion object {
        private val LENGTH: EntityDataAccessor<Float> =
            SynchedEntityData.defineId(BoltEntity::class.java, EntityDataSerializers.FLOAT)
    }
}