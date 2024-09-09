package dev.sterner.common.blockentity

import com.sammy.malum.common.block.MalumBlockEntityInventory
import com.sammy.malum.common.block.storage.jar.SpiritJarBlockEntity
import com.sammy.malum.core.systems.recipe.SpiritWithCount
import com.sammy.malum.core.systems.spirit.MalumSpiritType
import com.sammy.malum.visual_effects.SpiritLightSpecs
import dev.sterner.api.VoidBoundApi
import dev.sterner.api.rift.SimpleSpiritCharge
import dev.sterner.networking.UpdateSpiritAmountPacket
import dev.sterner.registry.VoidBoundBlockEntityTypeRegistry
import dev.sterner.registry.VoidBoundPacketRegistry
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtUtils
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.phys.Vec3
import team.lodestar.lodestone.helpers.BlockHelper
import team.lodestar.lodestone.systems.blockentity.ItemHolderBlockEntity
import team.lodestar.lodestone.systems.blockentity.LodestoneBlockEntity
import team.lodestar.lodestone.systems.particle.ParticleEffectSpawner
import team.lodestar.lodestone.systems.particle.builder.WorldParticleBuilder
import team.lodestar.lodestone.systems.particle.data.GenericParticleData
import team.lodestar.lodestone.systems.particle.data.color.ColorParticleData
import team.lodestar.lodestone.systems.particle.world.LodestoneWorldParticle
import java.util.function.Consumer
import java.util.function.Supplier
import kotlin.math.PI


class OsmoticEnchanterBlockEntity(pos: BlockPos, state: BlockState?) : VoudBoundItemHolderBlockEntity(
    VoidBoundBlockEntityTypeRegistry.OSMOTIC_ENCHANTER.get(), pos,
    state
) {

    data class EnchantmentData(val enchantment: Enchantment, val level: Int)

    var time: Int = 0
    var rot: Float = 0f
    var oRot: Float = 0f
    private var tRot: Float = 0f

    var availableEnchantments: MutableList<EnchantmentData> = mutableListOf()
    var activeEnchantments: MutableList<EnchantmentData> = mutableListOf()

    var activated = false

    var spiritsToConsume: SimpleSpiritCharge = SimpleSpiritCharge()
    var consumedSpirits: SimpleSpiritCharge = SimpleSpiritCharge()

    private var cooldown: Int = 0

    private var foundRiftPos: BlockPos? = null
    private var foundJarPosList: MutableList<BlockPos> = mutableListOf()
    private var jarCooldown = 0

    init {
        inventory = object : MalumBlockEntityInventory(1, 64) {
            override fun onContentsChanged(slot: Int) {
                super.onContentsChanged(slot)
                resetEnchanter()
                needsSync = true
                notifyUpdate()
                BlockHelper.updateAndNotifyState(level, worldPosition)
            }
        }
    }

    override fun onUse(player: Player, hand: InteractionHand?): InteractionResult {
        if (player.mainHandItem.isEnchantable) {
            inventory.interact(player.level(), player, hand)
        }
        return InteractionResult.SUCCESS
    }

    override fun tick() {

        if (level!!.isClientSide) {
            animationTick()
        }
        super.tick()

        if (activated) {
            cooldown++

            if (cooldown > 1) {
                // Variable to track if a spirit has been consumed
                var spiritConsumed = false

                //Look for a nearby Rift
                tickFindRift()

                //Try to consume spirit from saved rift
                spiritConsumed = tickCloseRift()

                //If no spirit was consumed, try to consume from other source
                if (!spiritConsumed) {
                    spiritConsumed = tickOtherSpiritSource()
                }

                // Reset cooldown if a spirit was consumed
                if (spiritConsumed) {
                    cooldown = 0
                }

                // If all the spirits are consumed, perform the enchantment
                if (consumedSpirits.getTotalCharge() >= spiritsToConsume.getTotalCharge()) {
                    doEnchant()
                }

                notifyUpdate()
            }
        }
    }

    private fun tickOtherSpiritSource(range: Int = 6): Boolean {
        val jarList = mutableListOf<BlockPos>()

        jarCooldown++
        if (jarCooldown > 20) {
            jarCooldown = 0
            val pos = blockPos
            for (aroundPos in BlockPos.betweenClosed(
                pos.x - range,
                pos.y,
                pos.z - range,
                pos.x + range,
                pos.y + range,
                pos.z + range
            )) {
                if (level?.getBlockEntity(aroundPos) is SpiritJarBlockEntity) {
                    jarList.add(aroundPos.immutable())
                }
            }
            if (jarList.isNotEmpty()) {
                foundJarPosList = jarList
                return true
            }
        }

        if (foundJarPosList.isNotEmpty()) {
            var i = 0
            while (i < foundJarPosList.size) {
                val jarPos = foundJarPosList[i]
                if (level?.getBlockEntity(jarPos) is SpiritJarBlockEntity) {
                    val jarBlockEntity = level!!.getBlockEntity(jarPos) as SpiritJarBlockEntity

                    val drained = tryDrainSpiritFromJar(jarBlockEntity)
                    if (drained) {
                        break
                    }
                } else {
                    foundJarPosList.removeAt(i)
                    continue  // Skip incrementing i to check the next item
                }
                i++
            }
        }

        return false
    }

    private fun tryDrainSpiritFromJar(jarBlockEntity: SpiritJarBlockEntity): Boolean {
        val spiritsList = spiritsToConsume.getNonEmptyMutableList()

        var i = 0
        while (i < spiritsList.size) {
            val spiritType = spiritsList[i]
            val requiredCharge = spiritsToConsume.getChargeForType(spiritType.type)
            val currentCharge = consumedSpirits.getChargeForType(spiritType.type)

            if (currentCharge < requiredCharge) {
                val spirits = jarBlockEntity.type
                val count = jarBlockEntity.count
                if (count > 0 && spirits == spiritType.type) {
                    spawnSpiritJarParticle(level!!, worldPosition, jarBlockEntity.blockPos!!, spirits)
                    consumedSpirits.addToCharge(spiritType.type, 1)
                    notifyUpdate()

                    jarBlockEntity.extractItem(0, 1)
                    jarBlockEntity.notifyUpdate()

                    // Exit after successful draining
                    return true
                }
            }
            i++
        }

        return false
    }

    private fun spawnSpiritJarParticle(level: Level, worldPosition: BlockPos, blockPos: BlockPos, type: MalumSpiritType) {

        if (!level.isClientSide) {
            return
        }

        val behavior =
            Consumer<WorldParticleBuilder> { b: WorldParticleBuilder ->
                b.addTickActor { p: LodestoneWorldParticle ->
                    val particlePos = Vec3(p.x, p.y, p.z)
                    val direction = worldPosition.center.subtract(particlePos).normalize()
                    p.particleSpeed = direction.scale(0.05)
                }
            }
        val rand = level.random

        val xRand: Double?
        val yRand: Double?
        val zRand: Double?


        xRand = blockPos.x + rand.nextDouble() - 0.5
        yRand = blockPos.y + 0.25 + (rand.nextDouble() - 0.5)
        zRand = blockPos.z + rand.nextDouble() - 0.5


        val startPos = Vec3(xRand, yRand, zRand)

        val distance = startPos.distanceTo(Vec3(worldPosition.x + 0.5, worldPosition.y + 1.5, worldPosition.z + 0.5))
        val baseLifetime = 10
        val speed = 0.05
        val adjustedLifetime = (distance / speed).toInt().coerceAtLeast(baseLifetime) - 20

        val lightSpecs: ParticleEffectSpawner =
            SpiritLightSpecs.spiritLightSpecs(level, Vec3(xRand, yRand, zRand), type)
        lightSpecs.builder.act { b: WorldParticleBuilder ->
            b.act(behavior)
                .modifyColorData { d: ColorParticleData ->
                    d.multiplyCoefficient(0.35f)
                }
                .modifyData(
                    Supplier { b.scaleData },
                    Consumer { d: GenericParticleData ->
                        d.multiplyValue(2.0f).multiplyCoefficient(0.9f)
                    })
                .modifyData(
                    Supplier { b.transparencyData },
                    Consumer { d: GenericParticleData ->
                        d.multiplyCoefficient(0.9f)
                    }).multiplyLifetime(1.5f)
                .setLifetime(b.particleOptions.lifetimeSupplier.get() as Int + adjustedLifetime)
        }

        lightSpecs.bloomBuilder.act { b: WorldParticleBuilder ->
            b.act(behavior).modifyColorData { d: ColorParticleData ->
                d.multiplyCoefficient(0.35f)
            }
                .modifyData(
                    Supplier { b.scaleData },
                    Consumer { d: GenericParticleData ->
                        d.multiplyValue(1.6f).multiplyCoefficient(0.9f)
                    })
                .modifyData(
                    Supplier { b.transparencyData },
                    Consumer { d: GenericParticleData ->
                        d.multiplyCoefficient(0.9f)
                    })
                .setLifetime(((b.particleOptions.lifetimeSupplier.get() as Int).toFloat() + adjustedLifetime).toInt())
        }

        lightSpecs.spawnParticles()

    }

    /**
     * Called in the tick function, looks for a rift close by with a default of 3 blocks in radius
     */
    private fun tickFindRift(range: Int = 3) {
        val pos = blockPos
        for (aroundPos in BlockPos.betweenClosed(
            pos.x - range,
            pos.y,
            pos.z - range,
            pos.x + range,
            pos.y + range,
            pos.z + range
        )) {
            if (level?.getBlockEntity(aroundPos) is SpiritRiftBlockEntity) {
                foundRiftPos = aroundPos
                break
            }
        }
    }

    /**
     * If foundRiftPos has been found, get its charge and start transfer spirits to the enchanter.
     * Returns true if a transaction was successful
     */
    private fun tickCloseRift(): Boolean {
        if (foundRiftPos != null && level!!.getBlockEntity(foundRiftPos!!) is SpiritRiftBlockEntity) {
            val rift = level!!.getBlockEntity(foundRiftPos!!) as SpiritRiftBlockEntity
            val riftSpirits: MutableList<SpiritWithCount> = rift.simpleSpiritCharge.getNonEmptyMutableList()

            var i = 0
            while (i < spiritsToConsume.getNonEmptyMutableList().size) {
                val spiritType = spiritsToConsume.getNonEmptyMutableList()[i]
                val requiredCharge = spiritsToConsume.getChargeForType(spiritType.type)
                val currentCharge = consumedSpirits.getChargeForType(spiritType.type)

                if (currentCharge < requiredCharge) {
                    // Find the corresponding spirit in the rift
                    val riftSpirit = riftSpirits.find { it.type == spiritType.type }

                    if (riftSpirit != null && riftSpirit.count > 0) {
                        // Transfer one spirit at a time
                        consumedSpirits.addToCharge(spiritType.type, 1)
                        rift.simpleSpiritCharge.removeFromCharge(riftSpirit.type, 1)
                        rift.notifyUpdate()

                        // Return true after transferring one spirit to prevent jittering
                        return true
                    }
                } else {
                    // If the current spirit type's charge is full, move to the next one
                    i++
                }
            }
        }
        return false
    }

    /**
     * Handles the book model rotating towards the player withing 3 blocks
     */
    private fun animationTick() {
        oRot = rot
        val player = level!!.getNearestPlayer(
            blockPos.x.toDouble() + 0.5,
            blockPos.y.toDouble() + 0.5,
            blockPos.z.toDouble() + 0.5,
            3.0,
            false
        )

        val dir = blockState.getValue(BlockStateProperties.HORIZONTAL_FACING)

        if (player != null) {

            val d: Double = player.x - (blockPos.x.toDouble() + 0.5)
            val e: Double = player.z - (blockPos.z.toDouble() + 0.5)
            tRot = Mth.atan2(e, d).toFloat() - (PI / 2f).toFloat()

        } else {

            tRot = when (dir) {
                Direction.NORTH -> 180f
                Direction.SOUTH -> 0f
                Direction.WEST -> 90f
                else -> -90f
            } * (PI / 180f).toFloat()
        }

        while (rot >= Math.PI.toFloat()) {
            rot -= (Math.PI * 2).toFloat()
        }

        while (rot < -Math.PI.toFloat()) {
            rot += (Math.PI * 2).toFloat()
        }

        while (tRot >= Math.PI.toFloat()) {
            tRot -= (Math.PI * 2).toFloat()
        }

        while (tRot < -Math.PI.toFloat()) {
            tRot += (Math.PI * 2).toFloat()
        }

        var g: Float = tRot - rot

        while (g >= Math.PI.toFloat()) {
            g -= (Math.PI * 2).toFloat()
        }

        while (g < -Math.PI.toFloat()) {
            g += (Math.PI * 2).toFloat()
        }

        rot += g * 0.4f
        time++
    }

    //ENCHANTING SECTION START

    fun startEnchanting() {
        if (calculateSpiritRequired()) {
            activated = true
            notifyUpdate()
        }
    }

    fun calculateSpiritRequired(): Boolean {
        var bl = false
        val spirits = SimpleSpiritCharge()

        for (enchantmentInfo in activeEnchantments) {
            val sc = VoidBoundApi.getSpiritFromEnchant(enchantmentInfo.enchantment, enchantmentInfo.level)
            for (spirit in sc) {
                spirits.addToCharge(spirit.type, spirit.count)
            }
            bl = true
        }

        spiritsToConsume = spirits
        if (level is ServerLevel) {
            VoidBoundPacketRegistry.VOID_BOUND_CHANNEL.sendToClientsTracking(
                UpdateSpiritAmountPacket(
                    spiritsToConsume,
                    blockPos.asLong()
                ), this
            )
        }
        return bl
    }

    fun updateEnchantmentData(enchantment: Enchantment, level: Int, toActivate: Boolean) {
        activeEnchantments.removeIf { it.enchantment == enchantment }
        availableEnchantments.removeIf { it.enchantment == enchantment }

        if (toActivate) {
            activeEnchantments.add(EnchantmentData(enchantment, level))
        } else {
            availableEnchantments.add(EnchantmentData(enchantment, level))
        }
        calculateSpiritRequired()
        notifyUpdate()
    }


    /**
     * Enchant the item and reset the enchanter
     */
    private fun doEnchant() {
        for (enchantment in this.activeEnchantments) {
            inventory.getStackInSlot(0).enchant(enchantment.enchantment, enchantment.level)
        }
        resetEnchanter()
        activated = false
        cooldown = 0
        notifyUpdate()
    }

    fun resetEnchanter() {
        activeEnchantments.clear()
        availableEnchantments.clear()
        spiritsToConsume = SimpleSpiritCharge()
        consumedSpirits = SimpleSpiritCharge()
        refreshEnchants()
    }

    fun refreshEnchants() {
        availableEnchantments = getValidEnchantments()
        notifyUpdate()
    }

    private fun getValidEnchantments(): MutableList<EnchantmentData> {
        val enchantments: MutableList<EnchantmentData> = ArrayList()
        if (inventory.getStackInSlot(0) == ItemStack.EMPTY) return enchantments

        val item = inventory.getStackInSlot(0)
        for (enchantment in BuiltInRegistries.ENCHANTMENT) {

            if (enchantment.canEnchant(item) && item.isEnchantable && !enchantment.isCurse) {
                enchantments.add(EnchantmentData(enchantment, 1))
            }
        }
        return enchantments
    }

    //ENCHANTING SECTION END

    //DATA SECTION START

    override fun saveAdditional(compound: CompoundTag) {
        compound.putIntArray(
            "AvailableEnchants",
            availableEnchantments.stream().mapToInt { i -> BuiltInRegistries.ENCHANTMENT.getId(i.enchantment) }
                .toArray()
        )
        compound.putIntArray(
            "ActiveEnchants",
            activeEnchantments.stream().mapToInt { i -> BuiltInRegistries.ENCHANTMENT.getId(i.enchantment) }.toArray()
        )
        compound.putIntArray("Level", activeEnchantments.stream().mapToInt { i -> i.level }.toArray())

        spiritsToConsume.serializeNBT(compound)
        val consumedNbt = CompoundTag()
        consumedSpirits.serializeNBT(consumedNbt)
        compound.put("Consumed", consumedNbt)
        compound.putBoolean("Activated", activated)

        if (foundRiftPos != null) {
            compound.put("RiftPos", NbtUtils.writeBlockPos(foundRiftPos!!))
        }

        super.saveAdditional(compound)
    }

    override fun load(compound: CompoundTag) {
        if (compound.contains("ActiveEnchants")) {
            activeEnchantments.clear()

            val enchantmentArray = compound.getIntArray("ActiveEnchants")
            val levelArray = compound.getIntArray("Level")

            for ((index, enchantment) in enchantmentArray.withIndex()) {
                activeEnchantments.add(EnchantmentData(Enchantment.byId(enchantment)!!, levelArray[index]))
            }
        }
        if (compound.contains("AvailableEnchants")) {
            availableEnchantments.clear()

            val enchantmentArray = compound.getIntArray("AvailableEnchants")

            for (enchantment in enchantmentArray) {
                availableEnchantments.add(EnchantmentData(Enchantment.byId(enchantment)!!, 1))
            }
        }

        spiritsToConsume = spiritsToConsume.deserializeNBT(compound)
        if (compound.contains("Consumed")) {
            val c = compound.get("Consumed") as CompoundTag
            consumedSpirits = consumedSpirits.deserializeNBT(c)
        }
        activated = compound.getBoolean("Activated")

        if (compound.contains("RiftPos")) {
            foundRiftPos = NbtUtils.readBlockPos(compound.getCompound("RiftPos"))
        }

        super.load(compound)
    }

    //DATA SECTION END
}