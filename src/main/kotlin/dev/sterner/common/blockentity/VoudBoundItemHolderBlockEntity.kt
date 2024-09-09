package dev.sterner.common.blockentity

import com.sammy.malum.common.block.storage.MalumItemHolderBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.world.Container
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.Vec3

open class VoudBoundItemHolderBlockEntity(type: BlockEntityType<out MalumItemHolderBlockEntity>?, pos: BlockPos?,
                                          state: BlockState?
) : MalumItemHolderBlockEntity(
    type, pos, state
), Container {
    override fun getItemOffset(p0: Float): Vec3 {
        return Vec3.ZERO
    }

    override fun clearContent() {
        inventory.clear()
    }

    override fun getContainerSize(): Int {
        return inventory.stacks.size
    }

    override fun isEmpty(): Boolean {
        return inventory.isEmpty
    }

    override fun getItem(slot: Int): ItemStack {
        return inventory.getStackInSlot(slot)
    }

    override fun removeItem(slot: Int, amount: Int): ItemStack {
        return inventory.extractItem(slot, amount, false)
    }

    override fun removeItemNoUpdate(slot: Int): ItemStack {
        return inventory.extractItem(slot, 64, false)
    }

    override fun setItem(slot: Int, stack: ItemStack) {
        inventory.setStackInSlot(slot, stack)
    }

    override fun stillValid(player: Player): Boolean {
        return true
    }
}