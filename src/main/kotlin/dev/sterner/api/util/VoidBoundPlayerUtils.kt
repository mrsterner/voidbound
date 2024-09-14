package dev.sterner.api.util

import dev.sterner.api.item.ItemAbility
import dev.sterner.api.revelation.KnowledgeType
import dev.sterner.registry.VoidBoundComponentRegistry
import dev.sterner.registry.VoidBoundItemRegistry
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import team.lodestar.lodestone.helpers.TrinketsHelper

object VoidBoundPlayerUtils {

    /**
     * Returns true if a client player has the hallowed goggles or monocle equipped
     */
    fun hasGoggles(): Boolean {
        val player = Minecraft.getInstance().player
        if (player != null) {
            val bl = TrinketsHelper.hasTrinketEquipped(player, VoidBoundItemRegistry.HALLOWED_MONOCLE.get())
            val bl2 = Minecraft.getInstance().player!!.getItemBySlot(EquipmentSlot.HEAD)
                .`is`(
                    VoidBoundItemRegistry.HALLOWED_GOGGLES.get()
                )
            val bl3 = VoidBoundItemUtils.getActiveAbility(player.mainHandItem) == ItemAbility.SPIRIT_VISION

            return bl || bl2 || bl3
        }
        return false
    }

    /**
     * Returns false if the block being broken is warded by another player
     */
    fun canPlayerBreakBlock(level: Level, player: Player, blockPos: BlockPos): Boolean {
        val comp = VoidBoundComponentRegistry.VOID_BOUND_WORLD_COMPONENT.get(level)
        if (comp.isEmpty()) {
            return true
        }

        return !comp.isPosBoundToAnotherPlayer(player, GlobalPos.of(player.level().dimension(), blockPos))
    }

    fun hasKnowledge(knowledge: KnowledgeType): Boolean {
        val player = Minecraft.getInstance().player
        if (player != null) {
            val comp = VoidBoundComponentRegistry.VOID_BOUND_REVELATION_COMPONENT.get(player)
            return comp.hasKnowledge(knowledge)
        }
        return false
    }

    fun addThought(player: Player, text: Component, duration: Int = 20 * 5, delay: Int = 20 * 5) {
        VoidBoundComponentRegistry.VOID_BOUND_REVELATION_COMPONENT.maybeGet(player).ifPresent {
            it.addThought(text, duration, delay)
        }
    }

    fun hasThoughtSentOrUnlocked(knowledge: KnowledgeType) : Boolean {
        val component =
            VoidBoundComponentRegistry.VOID_BOUND_REVELATION_COMPONENT.maybeGet(Minecraft.getInstance().player)
        if (component.isPresent) {
            if (component.get().hasKnowledge(knowledge)) {
                return true
            }

            return component.get().hasThoughtSentForKnowledge(knowledge)
        }
        return false
    }
}