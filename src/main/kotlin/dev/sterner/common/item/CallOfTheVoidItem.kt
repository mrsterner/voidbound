package dev.sterner.common.item

import com.sammy.malum.MalumMod
import net.minecraft.core.BlockPos
import net.minecraft.core.HolderSet
import net.minecraft.core.Registry
import net.minecraft.core.registries.Registries
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResultHolder
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.TooltipFlag
import net.minecraft.world.level.Level
import net.minecraft.world.level.levelgen.structure.Structure
import net.minecraft.world.phys.Vec3
import kotlin.math.acos


class CallOfTheVoidItem : Item(Properties().stacksTo(1)) {

    private var cooldown = 0

    override fun allowNbtUpdateAnimation(
        player: Player?,
        hand: InteractionHand?,
        oldStack: ItemStack?,
        newStack: ItemStack?
    ): Boolean {
        return false
    }

    override fun use(level: Level, player: Player, usedHand: InteractionHand): InteractionResultHolder<ItemStack> {
        var tag = player.getItemInHand(usedHand).tag
        if (tag == null) {
            tag = CompoundTag()
        }

        if (tag.contains("Active") && tag.getBoolean("Active")) {
            tag.putBoolean("Active", false)
            if (tag.contains("StructureLoc")) {
                tag.remove("StructureLoc")
            }
            if (tag.contains("Glowing")) {
                tag.remove("Glowing")
            }

        } else if (tag.contains("Active")) {
            if (tag.getBoolean("Active")) {
                tag.putBoolean("Active", false)
            } else {
                tag.putBoolean("Active", true)
            }
        } else {
            tag.putBoolean("Active", true)
        }

        player.getItemInHand(usedHand).tag = tag
        player.cooldowns.addCooldown(this, 160)
        return super.use(level, player, usedHand)
    }

    private fun locateStructure(level: ServerLevel, stack: ItemStack, entity: Player) {
        val registry: Registry<Structure> = level.registryAccess().registryOrThrow(Registries.STRUCTURE)
        val structureKey: ResourceKey<Structure> =
            ResourceKey.create(Registries.STRUCTURE, MalumMod.malumPath("weeping_well"))
        val featureHolderSet: HolderSet<Structure> = registry.getHolder(structureKey).map { holders ->
            HolderSet.direct(
                holders
            )
        }.orElse(null)

        val pair =
            level.chunkSource
                .generator
                .findNearestMapStructure(
                    level,
                    featureHolderSet,
                    entity.blockPosition(),
                    100,
                    false
                )

        if (pair?.first != null) {
            if (stack.tag == null) {
                stack.tag = CompoundTag()
            }
            val structPos: BlockPos = (pair.first)
            stack.tag!!.putLong("StructureLoc", structPos.asLong())
        }
    }

    override fun inventoryTick(stack: ItemStack, level: Level, entity: Entity, slotId: Int, isSelected: Boolean) {

        if (level is ServerLevel && entity is Player) {

            var stackTag = stack.tag
            if (stackTag == null) {
                stackTag = CompoundTag()
            }

            if (stack.hasTag() && stackTag.contains("Active") && stackTag.getBoolean("Active")) {
                cooldown++
                if (cooldown >= 100) {
                    cooldown = 0
                    if (!stack.tag!!.contains("StructureLoc")) {
                        locateStructure(level, stack, entity)
                    }
                }
                val lookDir: Vec3 = entity.lookAngle
                val playerPos: BlockPos = entity.onPos
                val pos: BlockPos = BlockPos.of(stack.tag!!.getLong("StructureLoc"))
                val margin = 20.0 // margin for error in degrees

                if (isLookingTowards(lookDir, playerPos, pos, margin)) {
                    stack.tag!!.putBoolean("Glowing", true)
                } else {
                    stack.tag!!.putBoolean("Glowing", false)
                }
            }

            if (stack.hasTag() && stackTag.contains("Active") && !stackTag.getBoolean("Active")) {
                if (stack.tag!!.contains("StructureLoc")) {
                    stack.tag!!.remove("StructureLoc")
                }
            }
        }

        super.inventoryTick(stack, level, entity, slotId, isSelected)
    }

    private fun isLookingTowards(
        playerLookDir: Vec3,
        playerPos: BlockPos,
        targetPos: BlockPos,
        margin: Double
    ): Boolean {
        // Calculate the horizontal direction vector from player to target position
        val targetDir = Vec3(
            (targetPos.x - playerPos.x).toDouble(),
            0.0, // Ignore Y component
            (targetPos.z - playerPos.z).toDouble()
        )

        // Create a horizontal look direction vector (ignore Y component)
        val lookDirHorizontal = Vec3(
            playerLookDir.x,
            0.0, // Ignore Y component
            playerLookDir.z
        )

        // Normalize the vectors
        val normalizedLookDir = lookDirHorizontal.normalize()
        val normalizedTargetDir = targetDir.normalize()

        // Calculate the dot product
        val dotProduct = normalizedLookDir.dot(normalizedTargetDir)

        // Convert the dot product to an angle
        val angle = acos(dotProduct) // This is in radians

        // Convert radians to degrees
        val angleInDegrees = Math.toDegrees(angle)

        // Check if the angle is within the margin
        //println(angleInDegrees)
        return angleInDegrees <= margin
    }

    override fun appendHoverText(
        stack: ItemStack,
        level: Level?,
        tooltipComponents: MutableList<Component>,
        isAdvanced: TooltipFlag
    ) {
        var stackTag = stack.tag
        if (stackTag == null) {
            stackTag = CompoundTag()
        }
        if (stackTag.contains("Active") && stackTag.getBoolean("Active")) {
            tooltipComponents.add(Component.translatable("Active"))
        }
        if (stackTag.contains("Active") && !stackTag.getBoolean("Active")) {
            tooltipComponents.add(Component.translatable("Inactive"))
        }

        super.appendHoverText(stack, level, tooltipComponents, isAdvanced)
    }
}