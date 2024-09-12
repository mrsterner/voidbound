package dev.sterner.common.item.tool.ichor

import com.mojang.datafixers.util.Pair
import dev.sterner.api.item.ItemAbility
import dev.sterner.api.util.VoidBoundItemUtils
import dev.sterner.common.item.tool.GalesEdgeItem.Companion.ascend
import dev.sterner.mixin.HoeItemTillablesAccessor
import net.minecraft.core.BlockPos
import net.minecraft.core.NonNullList
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.BlockTags
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.HoeItem
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Tier
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.BlockHitResult
import team.lodestar.lodestone.systems.item.tools.magic.MagicSwordItem
import java.util.function.Consumer
import java.util.function.Predicate

class IchoriumVorpal(tier: Tier, attackDamageModifier: Int, attackSpeedModifier: Float, magicDamage: Float, properties: Properties) : MagicSwordItem(tier, attackDamageModifier,
    attackSpeedModifier,
    magicDamage,
    properties
) {

    override fun getUseDuration(stack: ItemStack): Int {
        return 72000
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (VoidBoundItemUtils.getActiveAbility(player.mainHandItem) != ItemAbility.HARVEST) {
            player.startUsingItem(usedHand)
        }

        return super.use(level, player, usedHand)
    }

    override fun useOn(context: UseOnContext): InteractionResult {
        val player = context.player

        if (player!!.isShiftKeyDown) {
            return super.useOn(context)
        }
        if (VoidBoundItemUtils.getActiveAbility(player.mainHandItem) == ItemAbility.HARVEST) {
            for (xx in -1..1) {
                for (zz in -1..1) {
                    useHoeOn(
                        UseOnContext(
                            player, player.usedItemHand,
                            BlockHitResult(
                                context.clickLocation,
                                context.horizontalDirection,
                                context.clickedPos.offset(xx, 0, zz),
                                context.isInside
                            )
                        )
                    )
                }
            }
            return InteractionResult.SUCCESS
        }

        return InteractionResult.PASS
    }


    fun useHoeOn(context: UseOnContext): InteractionResult {
        val level = context.level
        val blockPos = context.clickedPos
        val pair = HoeItemTillablesAccessor.getTILLABLES()[level.getBlockState(blockPos).block] as Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>?
        if (pair == null) {
            return InteractionResult.PASS
        } else {
            val predicate = pair.first
            val consumer = pair.second
            if (predicate.test(context)) {
                val player = context.player
                level.playSound(player, blockPos, SoundEvents.HOE_TILL, SoundSource.BLOCKS, 1.0f, 1.0f)
                if (!level.isClientSide) {
                    consumer.accept(context)
                    if (player != null) {
                        context.itemInHand.hurtAndBreak(
                            1, player
                        ) { playerx: Player ->
                            playerx.broadcastBreakEvent(
                                context.hand
                            )
                        }
                    }
                }

                return InteractionResult.sidedSuccess(level.isClientSide)
            } else {
                return InteractionResult.PASS
            }
        }
    }

    override fun mineBlock(
        stack: ItemStack,
        level: Level,
        state: BlockState,
        pos: BlockPos,
        miningEntity: LivingEntity
    ): Boolean {
        if (level is ServerLevel && state.`is`(BlockTags.CROPS)) {
            val list = NonNullList.create<ItemStack>()
            list.addAll(Block.getDrops(state, level, pos, null, miningEntity, stack))
            Containers.dropContents(level, pos, list)
        }
        return super.mineBlock(stack, level, state, pos, miningEntity)
    }

    override fun onUseTick(level: Level, player: LivingEntity, stack: ItemStack, remainingUseDuration: Int) {
        if (VoidBoundItemUtils.getActiveAbility(player.mainHandItem) != ItemAbility.HARVEST) {
            ascend(player, stack, this.getUseDuration(stack) - remainingUseDuration)
        }

        super.onUseTick(level, player, stack, remainingUseDuration)
    }

}