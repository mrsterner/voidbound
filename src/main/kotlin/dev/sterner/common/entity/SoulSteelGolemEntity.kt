package dev.sterner.common.entity

import com.sammy.malum.registry.common.item.ItemRegistry
import dev.sterner.api.entity.GolemCore
import dev.sterner.api.util.VoidBoundItemUtils
import dev.sterner.common.entity.ai.behaviour.InsertItemsToStorage
import dev.sterner.common.entity.ai.behaviour.ReturnHomeFromMemory
import dev.sterner.common.entity.ai.behaviour.gather.SetWalkTargetToItem
import dev.sterner.common.entity.ai.behaviour.gather.SetWalkTargetToStorage
import dev.sterner.common.entity.ai.behaviour.guard.SetTargetNearestHostile
import dev.sterner.common.entity.ai.behaviour.harvest.HarvestCrop
import dev.sterner.common.entity.ai.behaviour.harvest.SetCropWalkTarget
import dev.sterner.common.entity.ai.sensor.GolemGatherSensor
import dev.sterner.common.entity.ai.sensor.GolemHarvestSensor
import dev.sterner.common.entity.ai.sensor.GolemStorageSensor
import dev.sterner.common.item.GolemCoreItem
import dev.sterner.registry.VoidBoundEntityTypeRegistry
import dev.sterner.registry.VoidBoundMemoryTypeRegistry
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.tags.ItemTags
import net.minecraft.world.*
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.entity.ai.Brain
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink
import net.minecraft.world.entity.ai.memory.MemoryModuleType
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.GameRules
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import net.tslat.smartbrainlib.api.SmartBrainOwner
import net.tslat.smartbrainlib.api.core.BrainActivityGroup
import net.tslat.smartbrainlib.api.core.SmartBrainProvider
import net.tslat.smartbrainlib.api.core.behaviour.FirstApplicableBehaviour
import net.tslat.smartbrainlib.api.core.behaviour.OneRandomBehaviour
import net.tslat.smartbrainlib.api.core.behaviour.custom.attack.AnimatableMeleeAttack
import net.tslat.smartbrainlib.api.core.behaviour.custom.misc.Idle
import net.tslat.smartbrainlib.api.core.behaviour.custom.move.MoveToWalkTarget
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetRandomWalkTarget
import net.tslat.smartbrainlib.api.core.behaviour.custom.path.SetWalkTargetToAttackTarget
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.InvalidateAttackTarget
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetPlayerLookTarget
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRandomLookTarget
import net.tslat.smartbrainlib.api.core.behaviour.custom.target.SetRetaliateTarget
import net.tslat.smartbrainlib.api.core.sensor.ExtendedSensor
import net.tslat.smartbrainlib.api.core.sensor.vanilla.HurtBySensor
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyHostileSensor
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyLivingEntitySensor
import net.tslat.smartbrainlib.api.core.sensor.vanilla.NearbyPlayersSensor
import net.tslat.smartbrainlib.util.BrainUtils


open class SoulSteelGolemEntity(level: Level) :
    AbstractGolemEntity(VoidBoundEntityTypeRegistry.SOUL_STEEL_GOLEM_ENTITY.get(), level),
    SmartBrainOwner<SoulSteelGolemEntity> {

    val inventory = SimpleContainer(8)

    override fun mobInteract(player: Player, hand: InteractionHand): InteractionResult {
        val stack = player.getItemInHand(hand)
        if (stack.item is GolemCoreItem) {
            val item: GolemCoreItem = stack.item as GolemCoreItem
            if (getGolemCore() == GolemCore.NONE) {

                setGolemCore(item.core)
                player.getItemInHand(hand).shrink(1)

                return InteractionResult.SUCCESS
            } else {

                dropCore(level(), position())
                setGolemCore(item.core)
                player.getItemInHand(hand).shrink(1)

                return InteractionResult.SUCCESS
            }
        } else if (stack.`is`(ItemTags.SWORDS) || stack.`is`(ItemTags.AXES)) {
            if (mainHandItem.isEmpty) {
                setItemInHand(InteractionHand.MAIN_HAND, stack)
            } else {
                Containers.dropItemStack(level(), position().x, position().y, position().z, mainHandItem)
                setItemInHand(InteractionHand.MAIN_HAND, stack)
            }
            return InteractionResult.SUCCESS
        } else if (stack.`is`(ItemRegistry.TUNING_FORK.get()) && player.isShiftKeyDown) {
            onPickUpGolem(level(), position())
            return InteractionResult.SUCCESS
        }

        return super.mobInteract(player, hand)
    }

    override fun onPickUpGolem(level: Level, pos: Vec3) {
        dropCore(level, pos)
        Containers.dropContents(level(), this, inventory)
        super.onPickUpGolem(level, pos)
    }

    override fun wantsToPickUp(stack: ItemStack): Boolean {
        return level().gameRules.getBoolean(GameRules.RULE_MOBGRIEFING) && this.canPickUpLoot() && getGolemCore() == GolemCore.GATHER && canAddToInventory(
            stack
        )
    }

    private fun canAddToInventory(stack: ItemStack): Boolean {
        return inventory.canAddItem(stack)
    }

    override fun pickUpItem(itemEntity: ItemEntity) {
        VoidBoundItemUtils.pickUpItem(this, itemEntity)
    }

    override fun dropAllDeathLoot(damageSource: DamageSource) {
        dropCore(level(), position())
        Containers.dropContents(level(), this, inventory)
        super.dropAllDeathLoot(damageSource)
    }

    override fun addAdditionalSaveData(tag: CompoundTag) {
        super.addAdditionalSaveData(tag)
        tag.put("Inventory", this.inventory.createTag())
    }

    override fun readAdditionalSaveData(tag: CompoundTag) {
        super.readAdditionalSaveData(tag)
        if (tag.contains("Inventory", 9)) {
            this.inventory.fromTag(tag.getList("Inventory", 10))
        }
    }

    //BRAINNNNZZZ

    fun handleTuningFork(player: Player, blockPos: BlockPos) {
        if (getOwner().isPresent && getOwner().get() == player.uuid) {
            if (player.level().getBlockEntity(blockPos) is Container) {
                if (player.isShiftKeyDown) {
                    player.sendSystemMessage(Component.translatable("output_storage_selected"))
                    BrainUtils.setMemory(this, VoidBoundMemoryTypeRegistry.OUTPUT_STORAGE_LOCATION.get(), blockPos)
                } else {
                    player.sendSystemMessage(Component.translatable("input_storage_selected"))
                    BrainUtils.setMemory(this, VoidBoundMemoryTypeRegistry.INPUT_STORAGE_LOCATION.get(), blockPos)
                }
            } else {
                player.sendSystemMessage(Component.translatable("home_selected"))
                BrainUtils.setMemory(this, MemoryModuleType.HOME, GlobalPos.of(level().dimension(), blockPos))
            }
        }
    }

    override fun customServerAiStep() {
        tickBrain(this)
    }

    override fun brainProvider(): Brain.Provider<*> {
        return SmartBrainProvider(this) as Brain.Provider<*>
    }

    override fun getSensors(): MutableList<out ExtendedSensor<out SoulSteelGolemEntity>> {
        return ObjectArrayList.of(
            NearbyPlayersSensor(),
            NearbyLivingEntitySensor<SoulSteelGolemEntity>().setRadius(24.0, 16.0),

            //Golem Harvester
            GolemHarvestSensor().setPredicate { _, u -> u.getGolemCore() == GolemCore.HARVEST },
            //Golem Gatherer
            GolemGatherSensor().setPredicate { _, u -> u.getGolemCore() == GolemCore.GATHER },
            //Golem Guard
            NearbyHostileSensor<SoulSteelGolemEntity>().setPredicate { _, u -> u.getGolemCore() == GolemCore.GUARD },
            HurtBySensor<SoulSteelGolemEntity>().setPredicate { _, u -> u.getGolemCore() == GolemCore.GUARD },
            //Golem Fill/Empty
            GolemStorageSensor().setPredicate { _, u -> u.getGolemCore() == GolemCore.FILL || u.getGolemCore() == GolemCore.EMPTY }
        )
    }

    override fun getCoreTasks(): BrainActivityGroup<out SoulSteelGolemEntity> {
        return BrainActivityGroup.coreTasks(
            LookAtTargetSink(40, 300),
            MoveToWalkTarget<SoulSteelGolemEntity>(),

            //Golem Harvester
            SetCropWalkTarget().startCondition { it.getGolemCore() == GolemCore.HARVEST },
            HarvestCrop().startCondition { it.getGolemCore() == GolemCore.HARVEST },

            //Golem Gatherer
            SetWalkTargetToItem().startCondition { it.getGolemCore() == GolemCore.GATHER },
            SetWalkTargetToStorage().startCondition { it.getGolemCore() == GolemCore.GATHER && !it.inventory.isEmpty },
            InsertItemsToStorage().startCondition { it.getGolemCore() == GolemCore.GATHER && !it.inventory.isEmpty },

            //Golem Guard
            SetWalkTargetToAttackTarget<SoulSteelGolemEntity>().startCondition { it.getGolemCore() == GolemCore.GUARD },
            SetRetaliateTarget<SoulSteelGolemEntity>().startCondition { it.getGolemCore() == GolemCore.GUARD },
            SetTargetNearestHostile().startCondition { it.getGolemCore() == GolemCore.GUARD },

            //Fill Golem
            //InsertItemsToStorage().fill().startCondition { it.getGolemCore() == GolemCore.FILL && !it.inventory.isEmpty },
            //ExtractItemFromStorage().fill().startCondition { it.getGolemCore() == GolemCore.FILL && it.inventory.isEmpty },
        )
    }

    override fun getIdleTasks(): BrainActivityGroup<out SoulSteelGolemEntity> {
        return BrainActivityGroup.idleTasks(
            FirstApplicableBehaviour(
                SetRandomLookTarget(),
                SetPlayerLookTarget<SoulSteelGolemEntity?>().predicate { it.distanceToSqr(this.position()) < 6 }
            ),

            //Guard Golem
            ReturnHomeFromMemory(1f, 2, 150, 1200).startCondition { it.getGolemCore() == GolemCore.GUARD },
            OneRandomBehaviour(
                SetRandomWalkTarget(),
                Idle<SoulSteelGolemEntity>().runFor { it.random.nextInt(30, 60) }
            ).startCondition { it.getGolemCore() == GolemCore.GUARD }
        )
    }

    override fun getFightTasks(): BrainActivityGroup<out SoulSteelGolemEntity> {
        return BrainActivityGroup.fightTasks(
            //Golem Guard
            InvalidateAttackTarget<SoulSteelGolemEntity>(),
            AnimatableMeleeAttack<SoulSteelGolemEntity>(0).startCondition { it.getGolemCore() == GolemCore.GUARD }
                .whenStarting {
                    it.isAggressive = true
                    this.attackAnimationTick = 10
                }
                .whenStarting {
                    it.isAggressive = false
                }
        )
    }
}