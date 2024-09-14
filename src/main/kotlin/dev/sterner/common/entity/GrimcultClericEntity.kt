package dev.sterner.common.entity

import com.sammy.malum.registry.common.SpiritTypeRegistry
import dev.sterner.common.entity.ai.goal.FocusSpiritRiftGoal
import dev.sterner.common.entity.ai.goal.HealAllyGoal
import dev.sterner.common.entity.ai.goal.SummonAllyGoal
import dev.sterner.networking.CultistRiftParticlePacket
import dev.sterner.registry.VoidBoundBlockRegistry
import dev.sterner.registry.VoidBoundEntityTypeRegistry
import dev.sterner.registry.VoidBoundPacketRegistry
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.minecraft.core.BlockPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.ai.attributes.AttributeSupplier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.level.Level
import java.util.*

class GrimcultClericEntity(level: Level) :
    AbstractCultistEntity(VoidBoundEntityTypeRegistry.GRIMCULT_CLERIC_ENTITY.get(), level) {

    private var healCooldown = 0

    private var lookForRiftCooldown = 0

    override fun addAdditionalSaveData(compound: CompoundTag) {
        compound.putInt("HealCooldown", this.healCooldown)
        super.addAdditionalSaveData(compound)
    }

    override fun readAdditionalSaveData(compound: CompoundTag) {
        this.healCooldown = compound.getInt("HealCooldown")
        super.readAdditionalSaveData(compound)
    }

    override fun baseTick() {
        if (this.healCooldown > 0) {
            --this.healCooldown
        }

        if (lookForRiftCooldown <= 0) {
            lookForRiftCooldown = 20 * 4

            val aABB = this.boundingBox.inflate(5.0)

            for (blockPos in BlockPos.betweenClosed(
                Mth.floor(aABB.minX),
                Mth.floor(aABB.minY),
                Mth.floor(aABB.minZ),
                Mth.floor(aABB.maxX),
                Mth.floor(aABB.maxY),
                Mth.floor(aABB.maxZ)
            )) {
                val blockState = level().getBlockState(blockPos)
                if (blockState.`is`(VoidBoundBlockRegistry.SPIRIT_RIFT.get())) {
                    this.setRiftPos(blockPos.immutable())
                }
            }

        } else {
            lookForRiftCooldown--
        }

        if (getRiftPos().isPresent) {
            if (level() is ServerLevel) {
                for (player in PlayerLookup.tracking(this)) {
                    VoidBoundPacketRegistry.VOID_BOUND_CHANNEL.sendToClient(
                        CultistRiftParticlePacket(
                            this.id,
                            getRiftPos().get(),
                            SpiritTypeRegistry.ELDRITCH_SPIRIT.identifier
                        ), player
                    )
                }
            }
        }

        super.baseTick()
    }

    override fun registerGoals() {
        super.registerGoals()
        this.goalSelector.addGoal(1, SummonAllyGoal(this))
        this.goalSelector.addGoal(2, HealAllyGoal(this, 1.0, 100, 0, 10.0F))
        this.goalSelector.addGoal(3, FocusSpiritRiftGoal(this))
    }

    override fun getArmPose(): GrimcultArmPose {
        if (this.isHealing()) {
            return GrimcultArmPose.SPELLCASTING
        }

        return super.getArmPose()
    }


    override fun defineSynchedData() {
        super.defineSynchedData()
        entityData.define(IS_HEALING, false)
        entityData.define(OPTIONAL_RIFT_POS, Optional.empty())
    }

    fun setIsHealing(b: Boolean) {
        entityData.set(IS_HEALING, b)
    }

    fun isHealing(): Boolean {
        return entityData.get(IS_HEALING)
    }

    fun setRiftPos(pos: BlockPos) {
        entityData.set(OPTIONAL_RIFT_POS, Optional.of(pos))
    }

    fun getRiftPos(): Optional<BlockPos> {
        return entityData.get(OPTIONAL_RIFT_POS)
    }

    companion object {

        val IS_HEALING: EntityDataAccessor<Boolean> = SynchedEntityData.defineId(
            AbstractCultistEntity::class.java, EntityDataSerializers.BOOLEAN
        )

        val OPTIONAL_RIFT_POS: EntityDataAccessor<Optional<BlockPos>> = SynchedEntityData.defineId(
            AbstractCultistEntity::class.java, EntityDataSerializers.OPTIONAL_BLOCK_POS
        )

        fun createGrimcultAttributes(): AttributeSupplier.Builder? {
            return LivingEntity.createLivingAttributes()
                .add(Attributes.MAX_HEALTH, 20.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3)
                .add(Attributes.KNOCKBACK_RESISTANCE, 0.25)
                .add(Attributes.ATTACK_DAMAGE, 2.0)
                .add(Attributes.ARMOR)
                .add(Attributes.ARMOR_TOUGHNESS)
                .add(Attributes.FOLLOW_RANGE, 16.0)
                .add(Attributes.ATTACK_KNOCKBACK)
        }
    }
}