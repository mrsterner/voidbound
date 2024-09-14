package dev.sterner.common.item

import dev.sterner.registry.VoidBoundComponentRegistry
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class IchorItem(properties: Properties) : Item(properties) {

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {
        if (entity is ServerPlayer) {
            val comp = VoidBoundComponentRegistry.VOID_BOUND_REVELATION_COMPONENT.get(entity)

            if (!comp.hasIchorKnowledge) {
                comp.hasIchorKnowledge = true
            }
        }
        super.inventoryTick(stack, level, entity, slotId, isSelected)
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {

        if (FabricLoader.getInstance().isDevelopmentEnvironment) {
            if (level.isClientSide) {
                var comp = VoidBoundComponentRegistry.VOID_BOUND_REVELATION_COMPONENT.get(player)
                comp.addThought(
                    Component.literal("No cost too great. No mind to think. No will to break"),
                    20 * 5,
                    20 * 2
                )
                comp.addThought(Component.translatable("No voice to cry"), 20 * 6, 20 * 2)
            }
        }

        return super.use(level, player, usedHand)
    }
}