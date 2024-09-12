package dev.sterner.mixin_logic

import com.sammy.malum.common.components.MalumComponents
import com.sammy.malum.common.components.MalumPlayerDataComponent
import com.sammy.malum.registry.common.block.BlockRegistry
import dev.sterner.api.util.VoidBoundPlayerUtils
import dev.sterner.registry.VoidBoundComponentRegistry.Companion.VOID_BOUND_REVELATION_COMPONENT
import dev.sterner.registry.VoidBoundSoundEvents
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

object ServerPlayerMixinLogic {

    fun logic1(player: ServerPlayer, state: BlockState) {
        if (state.`is`(BlockRegistry.PRIMORDIAL_SOUP.get())) {
            val comp = VOID_BOUND_REVELATION_COMPONENT[player]
            if (!comp.hasWellKnowledge) {
                comp.hasWellKnowledge = true
            }
        }
    }

    fun logic2(player: ServerPlayer, level: ServerLevel, resourceKey: ResourceKey<Level>, resourceKey2: ResourceKey<Level>) {
        val comp = VOID_BOUND_REVELATION_COMPONENT[player]

        if (resourceKey2 === Level.NETHER) {
            if (comp.hasWellKnowledge) {
                comp.hasNetherKnowledge = true
                if (!comp.hasReceivedNetherMessage) {
                    VoidBoundPlayerUtils.addThought(player, Component.translatable("voidbound.revelation.nether"), 20 * 5)
                    comp.hasReceivedNetherMessage = true
                }

            } else {
                if (!comp.hasReceivedPreWellNetherMessage) {
                    VoidBoundPlayerUtils.addThought(player, Component.translatable("voidbound.revelation.pre_well_nether"), 20 * 5)
                    comp.hasReceivedPreWellNetherMessage = true
                }
            }
        }
        if (resourceKey2 === Level.END) {
            if (comp.hasWellKnowledge) {
                comp.hasEndKnowledge = true
                if (!comp.hasReceivedEndMessage) {
                    VoidBoundPlayerUtils.addThought(player, Component.translatable("voidbound.revelation.end"), 20 * 5)
                    comp.hasReceivedEndMessage = true
                }

            } else {
                if (!comp.hasReceivedPreWellEndMessage) {
                    VoidBoundPlayerUtils.addThought(player, Component.translatable("voidbound.revelation.pre_well_nether"), 20 * 5)
                    comp.hasReceivedPreWellEndMessage = true
                }
            }
        }
    }
}