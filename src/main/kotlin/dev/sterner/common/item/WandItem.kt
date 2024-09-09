package dev.sterner.common.item

import dev.sterner.common.item.focus.AbstractFocusItem
import dev.sterner.registry.VoidBoundWandFocusRegistry
import net.minecraft.core.NonNullList
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.SlotAccess
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.ClickAction
import net.minecraft.world.inventory.Slot
import net.minecraft.world.inventory.tooltip.BundleTooltip
import net.minecraft.world.inventory.tooltip.TooltipComponent
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Rarity
import net.minecraft.world.item.UseAnim
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import java.util.*
import java.util.stream.Stream

class WandItem(properties: Properties) : Item(
    properties
        .stacksTo(1)
        .rarity(Rarity.RARE)
) {

    override fun overrideStackedOnOther(stack: ItemStack, slot: Slot, action: ClickAction, player: Player): Boolean {
        if (action != ClickAction.SECONDARY) {
            return false
        } else {
            val itemStack = slot.item
            if (itemStack.isEmpty) {
                removeOne(stack).ifPresent { itemStack2: ItemStack ->
                    add(stack, slot.safeInsert(itemStack2))
                }
            } else if (itemStack.item.canFitInsideContainerItems() && itemStack.item is AbstractFocusItem) {
                val i = add(stack, itemStack)
                if (i > 0) {
                    itemStack.shrink(i)
                }
            }

            return true
        }
    }

    override fun overrideOtherStackedOnMe(
        stack: ItemStack,
        other: ItemStack,
        slot: Slot,
        action: ClickAction,
        player: Player,
        access: SlotAccess
    ): Boolean {
        if (action == ClickAction.SECONDARY && slot.allowModification(player)) {
            if (other.isEmpty) {
                removeOne(stack).ifPresent { itemStack: ItemStack ->
                    access.set(itemStack)
                }
            } else if (other.item is AbstractFocusItem) {
                val i = add(stack, other)
                if (i > 0) {
                    other.shrink(i)
                }
            }

            return true
        } else {
            return false
        }
    }

    fun getContents(stack: ItemStack): Stream<ItemStack> {
        val compoundTag = stack.tag
        if (compoundTag == null) {
            return Stream.empty()
        } else {
            val listTag = compoundTag.getList("Items", 10)
            return listTag.stream().map { obj: Tag? ->
                CompoundTag::class.java.cast(
                    obj
                )
            }.map { tag: CompoundTag -> ItemStack.of(tag) }
        }
    }

    override fun getTooltipImage(stack: ItemStack): Optional<TooltipComponent> {
        val nonNullList = NonNullList.create<ItemStack>()
        getContents(stack).forEach { e: ItemStack -> nonNullList.add(e) }
        return Optional.of(BundleTooltip(nonNullList, 1))
    }

    private fun add(wandStack: ItemStack, insertedStack: ItemStack): Int {
        if (!insertedStack.isEmpty && insertedStack.item.canFitInsideContainerItems()) {
            val compoundTag = wandStack.getOrCreateTag()

            if (!compoundTag.contains("Items")) {
                compoundTag.put("Items", ListTag())
            }

            val listTag = compoundTag.getList("Items", 10)

            val optional = getMatchingItem(insertedStack, listTag)
            if (optional.isPresent) {
                return 0
            }

            val itemStack2 = insertedStack.copyWithCount(1)
            val compoundTag3 = CompoundTag()
            itemStack2.save(compoundTag3)
            listTag.add(0, compoundTag3)

            // Unbind the currently bound focus, if any
            if (compoundTag.contains("BoundFocusIndex")) {
                val boundIndex = compoundTag.getInt("BoundFocusIndex")
                if (boundIndex >= 0 && boundIndex < listTag.size) {
                    unBindFocus(wandStack)
                }
            }

            // Bind the new focus to the current slot (0 in this case)
            bindFocus(wandStack, insertedStack)

            // Store the new bound index in the NBT tag (slot 0 in this case)
            compoundTag.putInt("BoundFocusIndex", 0)

            return 1
        } else {
            return 0
        }
    }

    fun bindFocus(wandStack: ItemStack, focusItem: ItemStack) {
        if (focusItem.item is AbstractFocusItem) {
            val focus = focusItem.item as AbstractFocusItem
            wandStack.tag!!.putString("FocusName", VoidBoundWandFocusRegistry.WAND_FOCUS.getKey(focus.focus).toString())
        }
    }

    private fun unBindFocus(wandStack: ItemStack) {
        wandStack.tag!!.remove("FocusName")
    }

    private fun getMatchingItem(stack: ItemStack, list: ListTag): Optional<CompoundTag> {
        return list.stream()
            .filter { obj: Tag? ->
                CompoundTag::class.java.isInstance(
                    obj
                )
            }
            .map { obj: Tag? ->
                CompoundTag::class.java.cast(
                    obj
                )
            }
            .filter { compoundTag: CompoundTag ->
                ItemStack.isSameItemSameTags(
                    ItemStack.of(compoundTag),
                    stack
                )
            }
            .findFirst()
    }

    private fun removeOne(stack: ItemStack): Optional<ItemStack> {
        val compoundTag = stack.getOrCreateTag()
        if (!compoundTag.contains("Items")) {
            return Optional.empty()
        } else {
            val listTag = compoundTag.getList("Items", 10)
            if (listTag.isEmpty()) {
                return Optional.empty()
            } else {
                // Remove the first item (index 0)
                val removedCompoundTag = listTag.getCompound(0)
                val removedItemStack = ItemStack.of(removedCompoundTag)
                listTag.removeAt(0)

                // Check if the removed item was the currently bound focus
                if (compoundTag.contains("BoundFocusIndex")) {
                    val boundIndex = compoundTag.getInt("BoundFocusIndex")
                    if (boundIndex == 0) {
                        // The removed item was the bound focus, search for another focus to bind

                        var nextBoundIndex: Int? = null
                        for (i in 0 until listTag.size) {
                            if (!ItemStack.of(listTag.getCompound(i)).isEmpty) {
                                nextBoundIndex = i
                                break
                            }
                        }

                        if (nextBoundIndex != null) {
                            // Bind the next focus with a higher index
                            val nextFocus = ItemStack.of(listTag.getCompound(nextBoundIndex))
                            bindFocus(stack, nextFocus)
                            compoundTag.putInt("BoundFocusIndex", nextBoundIndex)
                        } else {
                            // No other focus found, unbind the current focus
                            compoundTag.remove("BoundFocusIndex")
                            unBindFocus(stack)
                        }
                    } else if (boundIndex > 0) {
                        // Adjust the BoundFocusIndex since the list shifted after removing index 0
                        compoundTag.putInt("BoundFocusIndex", boundIndex - 1)
                    }
                }

                // If the list is now empty, remove the "Items" tag from the stack
                if (listTag.isEmpty()) {
                    stack.removeTagKey("Items")
                }

                return Optional.of(removedItemStack)
            }
        }
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player
        val level = context.level
        val stack = context.itemInHand
        if (stack.tag == null) {
            stack.tag = CompoundTag()
        }

        val focusName = stack.tag?.getString("FocusName")
        val focus = VoidBoundWandFocusRegistry.WAND_FOCUS.getOptional(focusName?.let { ResourceLocation.tryParse(it) })

        if (focus.isPresent && player != null) {
            val blockHit =
                BlockHitResult(context.clickLocation, context.clickedFace.opposite, context.clickedPos, false)
            focus.get().onFocusRightClick(stack, level, player, blockHit)
        }

        return super.useOn(context)
    }

    override fun onUseTick(level: Level, livingEntity: LivingEntity, stack: ItemStack, remainingUseDuration: Int) {
        if (stack.tag == null) {
            stack.tag = CompoundTag()
        }

        val focusName = stack.tag?.getString("FocusName")
        val focus = VoidBoundWandFocusRegistry.WAND_FOCUS.getOptional(focusName?.let { ResourceLocation.tryParse(it) })

        if (focus.isPresent && livingEntity is Player) {
            focus.get().onUsingFocusTick(stack, level, livingEntity)
        }
        super.onUseTick(level, livingEntity, stack, remainingUseDuration)
    }

    override fun releaseUsing(stack: ItemStack, level: Level, livingEntity: LivingEntity, timeCharged: Int) {
        if (stack.tag == null) {
            stack.tag = CompoundTag()
        }

        val focusName = stack.tag?.getString("FocusName")
        val focus = VoidBoundWandFocusRegistry.WAND_FOCUS.getOptional(focusName?.let { ResourceLocation.tryParse(it) })

        if (focus.isPresent && livingEntity is Player) {
            focus.get().onPlayerStopUsingFocus(stack, level, livingEntity)
        }
        super.releaseUsing(stack, level, livingEntity, timeCharged)
    }

    override fun use(pLevel: Level, pPlayer: Player, pHand: InteractionHand): InteractionResultHolder<ItemStack>? {
        val itemstack = pPlayer.getItemInHand(pHand)
        if (pPlayer.cooldowns.isOnCooldown(itemstack.item)) {
            return InteractionResultHolder.fail(itemstack)
        } else {
            pPlayer.startUsingItem(pHand)
            return InteractionResultHolder.consume(itemstack)
        }
    }

    override fun getUseDuration(pStack: ItemStack): Int {
        return 72000
    }

    override fun getUseAnimation(pStack: ItemStack): UseAnim {
        return UseAnim.BOW
    }

    fun updateSelectedFocus(wandStack: ItemStack, selectedFocus: ItemStack) {
        val compoundTag = wandStack.getOrCreateTag()

        if (!compoundTag.contains("Items")) {
            return // No items to update
        }

        val listTag = compoundTag.getList("Items", 10)

        // Search for the matching focus in the NBT list
        var foundIndex: Int? = null
        for (i in 0 until listTag.size) {
            val currentItemStack = ItemStack.of(listTag.getCompound(i))
            if (ItemStack.isSameItemSameTags(currentItemStack, selectedFocus)) {
                foundIndex = i
                break
            }
        }

        if (foundIndex == null) {
            return // Focus not found in the NBT list
        }

        // Get the selected focus from the NBT list
        val selectedFocusTag = listTag.getCompound(foundIndex)

        // Remove the selected focus from its current position
        listTag.removeAt(foundIndex)

        // Insert the selected focus at the first position (index 0)
        listTag.add(0, selectedFocusTag)

        // Bind the new focus
        bindFocus(wandStack, selectedFocus)

        // Update the "BoundFocusIndex" in the NBT tag to 0
        compoundTag.putInt("BoundFocusIndex", 0)
    }
}