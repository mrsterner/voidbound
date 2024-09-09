package dev.sterner.common.rift

import dev.sterner.api.rift.RiftType
import dev.sterner.common.blockentity.SpiritRiftBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.level.Level

class PoolRiftType : RiftType() {

    var cooldown = 0

    override fun tick(level: Level, blockPos: BlockPos, blockEntity: SpiritRiftBlockEntity) {
        cooldown++
        if (cooldown >= 20 * 30) {
            cooldown = 0
            blockEntity.simpleSpiritCharge.rechargeRandomCount()
            blockEntity.notifyUpdate()
        }
        super.tick(level, blockPos, blockEntity)
    }
}