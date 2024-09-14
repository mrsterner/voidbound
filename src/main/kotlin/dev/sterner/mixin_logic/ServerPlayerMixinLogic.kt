package dev.sterner.mixin_logic

import com.sammy.malum.registry.common.block.BlockRegistry
import dev.sterner.api.revelation.KnowledgeType
import dev.sterner.api.util.VoidBoundPlayerUtils
import dev.sterner.registry.VoidBoundComponentRegistry.Companion.VOID_BOUND_REVELATION_COMPONENT
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState

object ServerPlayerMixinLogic {

    fun logic1(player: ServerPlayer, state: BlockState) {
        if (state.`is`(BlockRegistry.PRIMORDIAL_SOUP.get())) {
            val comp = VOID_BOUND_REVELATION_COMPONENT[player]
            if (!comp.hasKnowledge(KnowledgeType.WEEPING_WELL)) {
                comp.unlockKnowledge(KnowledgeType.WEEPING_WELL)
            }
        }
    }

    fun logic2(
        player: ServerPlayer,
        level: ServerLevel,
        resourceKey: ResourceKey<Level>,
        resourceKey2: ResourceKey<Level>
    ) {
        val comp = VOID_BOUND_REVELATION_COMPONENT[player]

        if (resourceKey2 === Level.NETHER) {
            val bl = comp.unlockKnowledge(KnowledgeType.NETHER, "voidbound.revelation.pre_well_nether")
            if (bl) {
                VoidBoundPlayerUtils.addThought(
                    player,
                    Component.translatable("voidbound.revelation.nether"),
                    20 * 5
                )
            }
        }
        if (resourceKey2 === Level.END) {
            val bl = comp.unlockKnowledge(KnowledgeType.END, "voidbound.revelation.pre_well_nether")
            if (bl) {
                VoidBoundPlayerUtils.addThought(
                    player,
                    Component.translatable("voidbound.revelation.nether"),
                    20 * 5
                )
            }
        }
    }
}