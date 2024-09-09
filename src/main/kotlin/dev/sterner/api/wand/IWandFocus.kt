package dev.sterner.api.wand

import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.phys.HitResult

interface IWandFocus {

    fun onFocusRightClick(stack: ItemStack, level: Level, player: Player, hitResult: HitResult) {

    }

    fun onUsingFocusTick(stack: ItemStack, level: Level, player: Player) {

    }

    fun onPlayerStopUsingFocus(stack: ItemStack, level: Level, player: Player) {

    }
}