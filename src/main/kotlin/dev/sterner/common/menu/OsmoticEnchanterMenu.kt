package dev.sterner.common.menu

import dev.sterner.common.blockentity.OsmoticEnchanterBlockEntity
import dev.sterner.registry.VoidBoundMenuTypeRegistry
import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import team.lodestar.lodestone.systems.blockentity.LodestoneBlockEntityInventory

class OsmoticEnchanterMenu(
    i: Int, inventory: Inventory, val pos: BlockPos,
) : AbstractContainerMenu(VoidBoundMenuTypeRegistry.OSMOTIC_ENCHANTER.get(), i) {

    var inventory: LodestoneBlockEntityInventory? = null
    var osmoticEnchanter: OsmoticEnchanterBlockEntity? = null
    var shouldRefresh = true

    constructor(i: Int, inventory: Inventory, buf: FriendlyByteBuf) : this(
        i, inventory, buf.readBlockPos(),
    )

    init {

        if (inventory.player.level().getBlockEntity(pos) is OsmoticEnchanterBlockEntity) {
            osmoticEnchanter = inventory.player.level().getBlockEntity(pos) as OsmoticEnchanterBlockEntity
            this.addSlot(object : Slot(osmoticEnchanter!!, 0, 14 + 18 * 5 + 5, 14 + 18 * 5 - 14 + 24) {

                override fun mayPlace(stack: ItemStack): Boolean {
                    return stack.isEnchantable
                }

                override fun set(stack: ItemStack) {
                    shouldRefresh = true

                    super.set(stack)
                }

                override fun onTake(player: Player, stack: ItemStack) {
                    shouldRefresh = true
                    super.onTake(player, stack)
                }

                override fun mayPickup(player: Player): Boolean {
                    return !osmoticEnchanter!!.activated && super.mayPickup(player)
                }
            })
        }

        var m: Int

        //Player inventory
        var l = 0
        while (l < 3) {
            m = 0
            while (m < 9) {
                this.addSlot(Slot(inventory, m + l * 9 + 9, 36 + m * 18, 3 * 18 + 86 + l * 18 + 25))
                ++m
            }
            ++l
        }

        l = 0
        while (l < 9) {
            this.addSlot(Slot(inventory, l, 36 + l * 18, 3 * 18 + 144 + 25))
            ++l
        }
    }

    override fun quickMoveStack(playerIn: Player, index: Int): ItemStack {
        var itemStack = ItemStack.EMPTY
        val slot = slots[index]
        if (slot.hasItem()) {
            val itemStack1 = slot.item
            itemStack = itemStack1.copy()
            if (index < this.inventory!!.stacks.size) {
                if (!this.moveItemStackTo(
                        itemStack1, this.inventory!!.stacks.size,
                        slots.size, true
                    )
                ) {
                    return ItemStack.EMPTY
                }
            } else if (!this.moveItemStackTo(itemStack1, 0, this.inventory!!.stacks.size, false)) {
                return ItemStack.EMPTY
            }

            if (itemStack1.isEmpty) {
                slot.set(ItemStack.EMPTY)
            } else {
                slot.setChanged()
            }
        }

        return itemStack
    }

    override fun stillValid(player: Player): Boolean {
        return true
    }
}