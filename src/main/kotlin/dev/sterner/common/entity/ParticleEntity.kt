package dev.sterner.common.entity

import com.sammy.malum.common.entity.FloatingItemEntity
import com.sammy.malum.core.systems.spirit.MalumSpiritType
import com.sammy.malum.registry.common.SpiritTypeRegistry
import com.sammy.malum.visual_effects.SpiritLightSpecs
import dev.sterner.registry.VoidBoundEntityTypeRegistry
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.util.Mth
import net.minecraft.util.RandomSource
import net.minecraft.world.entity.Entity
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import team.lodestar.lodestone.systems.rendering.trail.TrailPointBuilder
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.cos
import kotlin.math.sin

/**
 * To make a particle have a trail of other particles
 */
class ParticleEntity(level: Level) : Entity(VoidBoundEntityTypeRegistry.PARTICLE_ENTITY.get(), level) {
    val trailPointBuilder: TrailPointBuilder = TrailPointBuilder.create(10)
    var spiritType: MalumSpiritType? = null
    private var destination: Vec3? = null
    private var windDown: Float = 1f
    private var age: Int = 0
    private var maxAge: Int = 20 * 3

    constructor(level: Level, dest: Vec3) : this(level) {
        this.destination = dest
    }

    init {
        this.noPhysics = true
    }

    override fun tick() {
        super.tick()
        ++this.age
        if (this.age > this.maxAge) {
            this.discard()
        }

        val friction = 0.95f
        this.deltaMovement = deltaMovement.multiply(friction.toDouble(), friction.toDouble(), friction.toDouble())
        if (this.isAlive) {
            val destination: Vec3? = this.destination
            if (destination != null) {
                if (this.windDown > 0.0f) {
                    this.windDown -= 0.02f
                }

                val velocity = Mth.clamp(this.windDown - 0.25f, 0.0f, 0.75f) * 5.0f
                val desiredMotion = destination.subtract(this.position()).normalize()
                    .multiply(velocity.toDouble(), velocity.toDouble(), velocity.toDouble())
                val easing = 0.01
                val xMotion = Mth.lerp(easing, deltaMovement.x, desiredMotion.x).toFloat()
                val yMotion = Mth.lerp(easing, deltaMovement.y, desiredMotion.y).toFloat()
                val zMotion = Mth.lerp(easing, deltaMovement.z, desiredMotion.z).toFloat()
                val resultingMotion = Vec3(xMotion.toDouble(), yMotion.toDouble(), zMotion.toDouble())
                this.deltaMovement = resultingMotion

                val distance = this.distanceToSqr(destination).toFloat()
                if (distance < 0.4f) {
                    this.remove(RemovalReason.DISCARDED)
                    return
                }
            }

            val movement = this.deltaMovement
            val nextX: Double = this.x + movement.x
            val nextY: Double = this.y + movement.y
            val nextZ: Double = this.z + movement.z

            this.setPos(nextX, nextY, nextZ)
            //this.move(MoverType.SELF, this.deltaMovement)
        }

        if (level().isClientSide) {
            val x = this.xOld
            val y: Double = this.yOld
            val z = this.zOld
            this.spawnParticles(x, y, z)

            for (i in 0..1) {
                val progress = (i + 1).toFloat() * 0.5f
                val position = getPosition(progress)
                trailPointBuilder.addTrailPoint(position)
            }

            trailPointBuilder.tickTrailPoints()
        }

    }

    private fun spawnParticles(x: Double, y: Double, z: Double) {

        if (!level().isClientSide) {
            return
        }

        if (this.spiritType != null) {
            val motion = this.deltaMovement
            val norm = motion.normalize().scale(0.05000000074505806)
            val lightSpecs = SpiritLightSpecs.spiritLightSpecs(this.level(), Vec3(x, y, z), this.spiritType)
            lightSpecs.builder.setMotion(norm)
            lightSpecs.bloomBuilder.setMotion(norm)
            lightSpecs.spawnParticles()
        }

    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        this.setSpirit(compound.getString("spiritType"))
        this.age = compound.getInt("age")
        this.maxAge = compound.getInt("maxAge")
        this.windDown = compound.getFloat("windDown")
    }

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putString("spiritType", spiritType!!.identifier)
        compound.putInt("age", this.age)
        compound.putInt("maxAge", this.maxAge)
        compound.putFloat("windDown", this.windDown)
    }

    fun setSpirit(spiritType: MalumSpiritType) {
        this.setSpirit(spiritType.identifier)
        this.spiritType = spiritType
    }

    private fun setSpirit(spiritIdentifier: String) {
        getEntityData().set(DATA_SPIRIT, spiritIdentifier)
    }

    override fun defineSynchedData() {
        getEntityData().define(DATA_SPIRIT, SpiritTypeRegistry.ARCANE_SPIRIT.identifier)
    }

    override fun onSyncedDataUpdated(pKey: EntityDataAccessor<*>) {
        if (DATA_SPIRIT == pKey) {
            this.spiritType = SpiritTypeRegistry.SPIRITS[entityData.get(DATA_SPIRIT)]
        }

        super.onSyncedDataUpdated(pKey)
    }

    override fun isNoGravity(): Boolean {
        return true
    }

    override fun fireImmune(): Boolean {
        return true
    }

    companion object {
        val DATA_SPIRIT: EntityDataAccessor<String> =
            SynchedEntityData.defineId(FloatingItemEntity::class.java, EntityDataSerializers.STRING)

        fun getRandomOffset(position: Vec3, random: RandomSource, radius: Double = 20.0): Vec3 {
            // Generate random spherical coordinates
            val theta = random.nextDouble() * 2.0 * PI // Angle around the z-axis
            val phi = acos(random.nextDouble() * 2.0 - 1.0) // Angle from the z-axis
            val r = radius * random.nextDouble() // Random radius within the sphere

            // Convert spherical coordinates to Cartesian coordinates
            val xOffset = r * sin(phi) * cos(theta)
            val yOffset = r * sin(phi) * sin(theta)
            val zOffset = r * cos(phi)

            // Calculate the new position
            return Vec3(
                position.x + xOffset,
                position.y + yOffset,
                position.z + zOffset
            )
        }
    }
}