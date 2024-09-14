package dev.sterner.common.item

import dev.sterner.api.revelation.KnowledgeType
import dev.sterner.api.util.VoidBoundPlayerUtils
import dev.sterner.registry.VoidBoundBlockRegistry
import dev.sterner.registry.VoidBoundComponentRegistry
import net.minecraft.client.Minecraft
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.BlockItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level


class GrimBookItem(properties: Properties) : BlockItem(VoidBoundBlockRegistry.GRIMCULT_RITES.get(), properties) {

    override fun useOn(context: UseOnContext): InteractionResult {
        if (context.player?.isShiftKeyDown == true) {
            return super.useOn(context)
        }
        return InteractionResult.PASS
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {

        if (player.isShiftKeyDown) {
            return super.use(level, player, usedHand)
        }

        val item = player.getItemInHand(usedHand)
        if (!item.hasTag()) {
            val tag = CompoundTag()
            item.tag = tag
        }
        var giveAdvancement = false

        if (item.tag!!.contains("open")) {
            if (item.tag!!.getBoolean("open")) {
                player.getItemInHand(usedHand).getOrCreateTag().putBoolean("open", false)
            } else {
                player.getItemInHand(usedHand).getOrCreateTag().putBoolean("open", true)
                giveAdvancement = true
            }

        } else {
            player.getItemInHand(usedHand).getOrCreateTag().putBoolean("open", true)
            giveAdvancement = true
        }

        if (giveAdvancement) {
            VoidBoundComponentRegistry.VOID_BOUND_REVELATION_COMPONENT.get(player).unlockKnowledge(KnowledgeType.GRIMCULT)
            VoidBoundPlayerUtils.addThought(player, Component.translatable("voidbound.grimcultrites"), 20 * 5, 1)
        } else if (item.tag!!.getBoolean("open")) {
            VoidBoundPlayerUtils.addThought(player, Component.translatable("voidbound.no_grimcultrites"))
        }


        return super.use(level, player, usedHand)
    }

    companion object {

        fun isOpen(): Boolean {
            val v: Boolean? =
                Minecraft.getInstance().player?.getItemInHand(InteractionHand.MAIN_HAND)?.tag?.getBoolean("open")
            return v == true
        }
    }
}