package dev.sterner.common.item

import com.sammy.malum.client.VoidRevelationHandler
import dev.sterner.api.revelation.KnowledgeType
import dev.sterner.api.util.VoidBoundPlayerUtils
import dev.sterner.registry.VoidBoundComponentRegistry
import net.minecraft.network.chat.Component
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level

class CheatersCodexItem(properties: Properties) : Item(properties) {

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {

        val component = VoidBoundComponentRegistry.VOID_BOUND_REVELATION_COMPONENT.get(player)

        component.unlockAllKnowledge()

        VoidRevelationHandler.seeTheRevelation(VoidRevelationHandler.RevelationType.VOID_READER)

        VoidBoundPlayerUtils.addThought(player, Component.translatable("You just know everything don't you >:("), 20 * 5, 1)

        return super.use(level, player, usedHand)
    }
}