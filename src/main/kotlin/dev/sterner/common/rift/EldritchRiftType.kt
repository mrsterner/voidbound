package dev.sterner.common.rift

import dev.sterner.api.rift.RiftType
import dev.sterner.api.util.VoidBoundUtils
import dev.sterner.common.blockentity.SpiritRiftBlockEntity
import dev.sterner.networking.SpiritBinderParticlePacket
import dev.sterner.registry.VoidBoundComponentRegistry
import dev.sterner.registry.VoidBoundPacketRegistry
import net.fabricmc.fabric.api.networking.v1.PlayerLookup
import net.minecraft.core.BlockPos
import net.minecraft.util.Mth
import net.minecraft.world.entity.PathfinderMob
import net.minecraft.world.level.Level
import net.minecraft.world.phys.AABB

class EldritchRiftType : RiftType() {

    override fun tick(level: Level, blockPos: BlockPos, blockEntity: SpiritRiftBlockEntity) {
        super.tick(level, blockPos, blockEntity)
        if (blockEntity.entity == null) {
            val list = level.getEntitiesOfClass(PathfinderMob::class.java, AABB(blockPos).inflate(5.0))
                .filter {
                    it.health / it.maxHealth <= 0.25 && it.isAlive && VoidBoundComponentRegistry.VOID_BOUND_ENTITY_COMPONENT.get(
                        it
                    ).eldritchRiftPos == null
                }
            if (list.isNotEmpty()) {
                blockEntity.entity = list.first()
                VoidBoundComponentRegistry.VOID_BOUND_ENTITY_COMPONENT.get(blockEntity.entity!!).eldritchRiftPos =
                    blockPos
                VoidBoundComponentRegistry.VOID_BOUND_ENTITY_COMPONENT.sync(blockEntity.entity!!)
            }
            blockEntity.counter = 0
        } else if (VoidBoundComponentRegistry.VOID_BOUND_ENTITY_COMPONENT.get(blockEntity.entity!!).eldritchRiftPos != null) {
            val spiritDataOptional = VoidBoundUtils.getSpiritData(blockEntity.entity!!)
            if (spiritDataOptional.isPresent) {
                blockEntity.counter++
                for (spirit in spiritDataOptional.get()) {
                    for (player in PlayerLookup.tracking(blockEntity)) {
                        VoidBoundPacketRegistry.VOID_BOUND_CHANNEL.sendToClient(
                            SpiritBinderParticlePacket(
                                blockEntity.entity!!.id,
                                blockPos,
                                spirit.type.identifier
                            ), player
                        )
                    }
                }

                if (blockEntity.counter > 20 * 5) {
                    blockEntity.counter = 0
                    blockEntity.addSpiritToCharge(blockEntity.entity!!)
                    blockEntity.targetAlpha = Mth.clamp(blockEntity.simpleSpiritCharge.getTotalCharge() / 20f, 0f, 1f)
                    blockEntity.entity!!.hurt(level.damageSources().magic(), blockEntity.entity!!.health * 2)
                    blockEntity.entity = null
                    blockEntity.notifyUpdate()
                }
            }
        }
    }
}