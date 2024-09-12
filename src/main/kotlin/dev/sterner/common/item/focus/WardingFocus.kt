package dev.sterner.common.item.focus

import dev.sterner.api.util.VoidBoundPlayerUtils
import dev.sterner.api.wand.IWandFocus
import dev.sterner.registry.VoidBoundComponentRegistry
import eu.pb4.common.protection.api.CommonProtection
import net.minecraft.core.GlobalPos
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult
import net.minecraft.world.phys.HitResult

class WardingFocus : IWandFocus {

    override fun onFocusRightClick(stack: ItemStack, level: Level, player: Player, hitResult: HitResult) {
        if (hitResult is BlockHitResult) {
            val comp = VoidBoundComponentRegistry.VOID_BOUND_WORLD_COMPONENT.get(level)
            val global = GlobalPos.of(level.dimension(), hitResult.blockPos)

            if (VoidBoundPlayerUtils.canPlayerBreakBlock(level, player, global.pos())) {
                if (comp.hasBlockPos(player, global)) {
                    comp.removePos(player.uuid, global)
                } else {
                    if (CommonProtection.canBreakBlock(level, hitResult.blockPos, player.gameProfile, player)) {
                        comp.addPos(player.uuid, global)
                    }
                }
            }
        }
    }
}