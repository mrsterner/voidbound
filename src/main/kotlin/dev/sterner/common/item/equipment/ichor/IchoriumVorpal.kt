package dev.sterner.common.item.equipment.ichor

import com.mojang.datafixers.util.Pair
import dev.sterner.api.item.HammerLikeItem
import dev.sterner.api.item.ItemAbility
import dev.sterner.api.util.VoidBoundItemUtils
import dev.sterner.common.item.equipment.GalesEdgeItem.Companion.ascend
import dev.sterner.mixin.HoeItemTillablesAccessor
import io.github.fabricators_of_create.porting_lib.common.util.IPlantable
import io.github.fabricators_of_create.porting_lib.event.common.BlockEvents.BreakEvent
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.tags.BlockTags
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Tier
import net.minecraft.world.item.context.UseOnContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.levelgen.structure.BoundingBox
import net.minecraft.world.phys.BlockHitResult
import team.lodestar.lodestone.systems.item.tools.magic.MagicSwordItem
import java.util.function.Consumer
import java.util.function.Predicate

class IchoriumVorpal(
    tier: Tier,
    attackDamageModifier: Int,
    attackSpeedModifier: Float,
    magicDamage: Float,
    properties: Properties
) : MagicSwordItem(
    tier, attackDamageModifier,
    attackSpeedModifier,
    magicDamage,
    properties
) {

    override fun getUseDuration(stack: ItemStack): Int {
        return 72000
    }

    override fun canAttackBlock(state: BlockState?, level: Level?, pos: BlockPos?, player: Player): Boolean {
        return VoidBoundItemUtils.getActiveAbility(player.mainHandItem) == ItemAbility.HARVEST
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        if (VoidBoundItemUtils.getActiveAbility(player.mainHandItem) != ItemAbility.HARVEST) {
            player.startUsingItem(usedHand)
        } else {

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
        val pair =
            HoeItemTillablesAccessor.getTILLABLES()[level.getBlockState(blockPos).block] as Pair<Predicate<UseOnContext>, Consumer<UseOnContext>>?
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
        blockPos: BlockPos,
        miningEntity: LivingEntity
    ): Boolean {
        val bl = VoidBoundItemUtils.getActiveAbility(stack) == ItemAbility.HARVEST
        if (bl && tags(state)) {
            val size = 3
            val areaOfEffect = BoundingBox(
                blockPos.x - size,
                blockPos.y - size,
                blockPos.z - size,
                blockPos.x + size,
                blockPos.y + size,
                blockPos.z + size
            )

            var blocksBroken = 0
            val iterator = BlockPos.betweenClosedStream(areaOfEffect).iterator()
            val removedBlocks = mutableSetOf<BlockPos>()

            while (iterator.hasNext()) {
                val pos = iterator.next()

                val targetState = level.getBlockState(pos)
                if (pos == blockPos || removedBlocks.contains(pos)) continue

                val state = level.getBlockState(pos)
                if (tags(state)) {
                    // Trigger block break event
                    BreakEvent.BLOCK_BREAK.invoker().onBlockBreak(BreakEvent(level, pos, targetState, miningEntity as Player))

                    removedBlocks.add(pos)
                    level.destroyBlock(pos, false, miningEntity)

                    if (!miningEntity.isCreative) {
                        HammerLikeItem.handleBlockDrops(targetState, pos, stack, level, miningEntity, Direction.UP)
                    }

                    blocksBroken++
                }
            }
        }
        return super.mineBlock(stack, level, state, blockPos, miningEntity)
    }

    override fun onUseTick(level: Level, player: LivingEntity, stack: ItemStack, remainingUseDuration: Int) {
        if (VoidBoundItemUtils.getActiveAbility(player.mainHandItem) != ItemAbility.HARVEST) {
            ascend(player, stack, this.getUseDuration(stack) - remainingUseDuration)
        }

        super.onUseTick(level, player, stack, remainingUseDuration)
    }

    override fun isCorrectToolForDrops(block: BlockState): Boolean {
        return tags(block)
    }


    fun tags(state: BlockState) : Boolean {
        return state.canBeReplaced() || state.`is`(BlockTags.CROPS) || state.block is IPlantable || state.`is`(BlockTags.FLOWERS)
    }
}