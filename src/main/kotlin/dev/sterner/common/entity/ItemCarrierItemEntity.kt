package dev.sterner.common.entity

import com.sammy.malum.common.entity.FloatingEntity
import com.sammy.malum.registry.common.SpiritTypeRegistry
import com.sammy.malum.registry.common.item.ItemRegistry
import com.sammy.malum.visual_effects.SpiritLightSpecs
import dev.sterner.registry.VoidBoundEntityTypeRegistry
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.syncher.EntityDataAccessor
import net.minecraft.network.syncher.EntityDataSerializers
import net.minecraft.network.syncher.SynchedEntityData
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.Level
import net.minecraft.world.phys.Vec3
import team.lodestar.lodestone.helpers.ItemHelper
import team.lodestar.lodestone.systems.particle.ParticleEffectSpawner
import java.util.*


class ItemCarrierItemEntity(level: Level?) : FloatingEntity(VoidBoundEntityTypeRegistry.ITEM_CARRIER_ENTITY.get(), level) {
    var itemStack: ItemStack = ItemStack(Items.STICK)

    constructor(
        level: Level?,
        ownerUUID: UUID?,
        stack: ItemStack,
        posX: Double,
        posY: Double,
        posZ: Double,
        velX: Double,
        velY: Double,
        velZ: Double
    ): this(level) {

        this.setOwner(ownerUUID)
        this.setItem(stack)
        this.itemStack = stack
        this.setPos(posX, posY, posZ)
        this.setDeltaMovement(velX, velY, velZ)
        this.maxAge = 800
    }

    override fun addAdditionalSaveData(pCompound: CompoundTag) {
        super.addAdditionalSaveData(pCompound)
        val itemstack = this.getItemRaw()
        if (!itemstack.isEmpty) {
            pCompound.put("Item", itemstack.save(CompoundTag()))
        }
    }

    override fun readAdditionalSaveData(pCompound: CompoundTag) {
        super.readAdditionalSaveData(pCompound)
        val itemstack = ItemStack.of(pCompound.getCompound("Item"))
        this.setItem(itemstack)
    }

    fun setItem(pStack: ItemStack) {
        getEntityData().set(DATA_ITEM_STACK, pStack)
    }

    override fun defineSynchedData() {
        getEntityData().define(DATA_ITEM_STACK, ItemStack.EMPTY)
    }

    override fun onSyncedDataUpdated(pKey: EntityDataAccessor<*>) {
        if (DATA_ITEM_STACK == pKey) {
            this.itemStack = getEntityData().get(DATA_ITEM_STACK) as ItemStack
        }

        super.onSyncedDataUpdated(pKey)
    }

    override fun collect() {
        ItemHelper.giveItemToEntity(this.owner, getItem())
    }

    override fun getMotionCoefficient(): Float {
       return 0.1f
    }

    protected fun getItemRaw(): ItemStack {
        return getEntityData().get(DATA_ITEM_STACK) as ItemStack
    }

    protected fun getDefaultItem(): Item {
        return ItemRegistry.ARCANE_SPIRIT.get() as Item
    }

    fun getItem(): ItemStack {
        val itemstack = this.getItemRaw()
        return if (itemstack.isEmpty) ItemStack(this.getDefaultItem()) else itemstack
    }

    override fun spawnParticles(x: Double, y: Double, z: Double) {
        val motion = this.deltaMovement
        val norm = motion.normalize().scale(0.05000000074505806)
        val lightSpecs: ParticleEffectSpawner =
            SpiritLightSpecs.spiritLightSpecs(this.level(), Vec3(x, y, z), SpiritTypeRegistry.AQUEOUS_SPIRIT)
        lightSpecs.builder.setMotion(norm)
        lightSpecs.bloomBuilder.setMotion(norm)
        lightSpecs.spawnParticles()
    }

    companion object {
        var DATA_ITEM_STACK = SynchedEntityData.defineId(ItemCarrierItemEntity::class.java, EntityDataSerializers.ITEM_STACK)
    }
}