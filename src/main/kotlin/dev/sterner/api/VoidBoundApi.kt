package dev.sterner.api

import com.sammy.malum.client.VoidRevelationHandler
import com.sammy.malum.common.container.WeaversWorkbenchContainer.component
import com.sammy.malum.core.systems.recipe.SpiritWithCount
import com.sammy.malum.core.systems.spirit.MalumSpiritType
import com.sammy.malum.registry.common.SpiritTypeRegistry
import dev.sterner.api.item.ItemAbility
import dev.sterner.listener.EnchantSpiritDataReloadListener
import dev.sterner.registry.VoidBoundComponentRegistry
import dev.sterner.registry.VoidBoundItemRegistry
import net.minecraft.client.Minecraft
import net.minecraft.core.BlockPos
import net.minecraft.core.GlobalPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.chat.Component
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.enchantment.Enchantment
import net.minecraft.world.level.Level
import team.lodestar.lodestone.helpers.TrinketsHelper

object VoidBoundApi {

    /**
     * Returns true if a client player has the hallowed goggles or monocle equipped
     */
    fun hasGoggles(): Boolean {
        val player = Minecraft.getInstance().player
        if (player != null) {
            val bl = TrinketsHelper.hasTrinketEquipped(player, VoidBoundItemRegistry.HALLOWED_MONOCLE.get())
            val bl2 = Minecraft.getInstance().player!!.getItemBySlot(EquipmentSlot.HEAD)
                .`is`(
                    VoidBoundItemRegistry.HALLOWED_GOGGLES.get()
                )
            return bl || bl2
        }
        return false
    }

    /**
     * Returns true if a player has the hallowed goggles or monocle equipped
     */
    fun hasGoggles(player: Player): Boolean {
        val bl = TrinketsHelper.hasTrinketEquipped(player, VoidBoundItemRegistry.HALLOWED_MONOCLE.get())
        val bl2 = Minecraft.getInstance().player!!.getItemBySlot(EquipmentSlot.HEAD)
            .`is`(
                VoidBoundItemRegistry.HALLOWED_GOGGLES.get()
            )
        return bl || bl2
    }

    /**
     * Returns how many spirits of each kind a enchantment is worth for the osmotic enchanter
     */
    fun getSpiritFromEnchant(enchantment: Enchantment, level: Int): List<SpiritWithCount> {

        val reg = BuiltInRegistries.ENCHANTMENT.getKey(enchantment)
        val list = EnchantSpiritDataReloadListener.ENCHANTING_DATA[reg]
        val out = mutableListOf<SpiritWithCount>()
        if (list != null) {
            for (spiritIn in list.spirits) {
                out.add(SpiritWithCount(spiritIn.type, spiritIn.count * level))
            }
        }

        if (out.isEmpty()) {
            out.add(SpiritWithCount(SpiritTypeRegistry.AQUEOUS_SPIRIT, 4 * level))
            out.add(SpiritWithCount(SpiritTypeRegistry.INFERNAL_SPIRIT, 4 * level))
            out.add(SpiritWithCount(SpiritTypeRegistry.EARTHEN_SPIRIT, 4 * level))
            out.add(SpiritWithCount(SpiritTypeRegistry.AERIAL_SPIRIT, 4 * level))

            out.add(SpiritWithCount(SpiritTypeRegistry.ARCANE_SPIRIT, 4 * level))
            out.add(SpiritWithCount(SpiritTypeRegistry.ELDRITCH_SPIRIT, 4 * level))
            out.add(SpiritWithCount(SpiritTypeRegistry.SACRED_SPIRIT, 4 * level))
            out.add(SpiritWithCount(SpiritTypeRegistry.WICKED_SPIRIT, 4 * level))
        }
        return out
    }

    /**
     * Returns false if the block being broken is warded by another player
     */
    fun canPlayerBreakBlock(level: Level, player: Player, blockPos: BlockPos): Boolean {
        val comp = VoidBoundComponentRegistry.VOID_BOUND_WORLD_COMPONENT.get(level)
        if (comp.isEmpty()) {
            return true
        }

        return !comp.isPosBoundToAnotherPlayer(player, GlobalPos.of(player.level().dimension(), blockPos))
    }

    /**
     * Returns false if the block being broken is warded by any player
     */
    fun canBlockBreak(level: Level, blockPos: BlockPos): Boolean {
        val comp = VoidBoundComponentRegistry.VOID_BOUND_WORLD_COMPONENT.get(level)
        if (comp.isEmpty()) {
            return true
        }

        if (comp.hasBlockPos(GlobalPos.of(level.dimension(), blockPos))) {
            return false
        }
        return true
    }

    fun hasTearKnowledgeClient(): Boolean {
        val player = Minecraft.getInstance().player
        if (player != null) {
            val comp = VoidBoundComponentRegistry.VOID_BOUND_REVELATION_COMPONENT.get(player)
            return comp.isTearKnowledgeComplete()
        }
        return false
    }

    fun hasIchorKnowledgeClient(): Boolean {
        val player = Minecraft.getInstance().player
        if (player != null) {
            val comp = VoidBoundComponentRegistry.VOID_BOUND_REVELATION_COMPONENT.get(player)
            return comp.hasIchorKnowledge && comp.hasGrimcultKnowledge && comp.hasWellKnowledge && comp.hasEndKnowledge && comp.hasNetherKnowledge
        }
        return false
    }

    fun hasGrimcultKnowledgeClient(): Boolean {
        val player = Minecraft.getInstance().player
        if (player != null) {
            val comp = VoidBoundComponentRegistry.VOID_BOUND_REVELATION_COMPONENT.get(player)
            return comp.hasGrimcultKnowledge && comp.hasWellKnowledge && comp.hasEndKnowledge && comp.hasNetherKnowledge
        }
        return false
    }

    fun addThought(player: Player, text: Component, duration: Int = 20 * 5){
        VoidBoundComponentRegistry.VOID_BOUND_REVELATION_COMPONENT.maybeGet(player).ifPresent {
            it.addThought(text, duration, 20 * 5)
        }
    }

    fun hasNetherMessage() : Boolean {
        val component = VoidBoundComponentRegistry.VOID_BOUND_REVELATION_COMPONENT.maybeGet(Minecraft.getInstance().player)
        if (component.isPresent) {
            return component.get().hasReceivedNetherMessage
        }
        return false
    }

    fun hasEndMessage() : Boolean {
        val component = VoidBoundComponentRegistry.VOID_BOUND_REVELATION_COMPONENT.maybeGet(Minecraft.getInstance().player)
        if (component.isPresent) {
            return component.get().hasReceivedEndMessage
        }
        return false
    }

    // Function to retrieve abilities from the ItemStack's NBT, returns a Set
    fun getItemAbilities(stack: ItemStack): Set<ItemAbility> {
        val abilities = mutableSetOf<ItemAbility>()
        val tag = stack.tag ?: return abilities // Return empty if no NBT

        val abilitiesTag = tag.getList("Abilities", 10) // 10 is the NBT type for CompoundTag
        for (i in 0 until abilitiesTag.size) {
            val abilityTag = abilitiesTag.getCompound(i)
            val ability = ItemAbility.readNbt(abilityTag)
            abilities.add(ability) // Add to the set, prevents duplicates
        }

        return abilities
    }

    // Function to add an ItemAbility to the ItemStack's NBT
    fun addItemAbility(stack: ItemStack, ability: ItemAbility, makeActive: Boolean = false) {
        val tag = stack.orCreateTag // Ensures the stack has NBT
        val abilitiesTag = tag.getList("Abilities", 10) // Fetch or create list

        // Check if ability already exists, if so, exit
        for (i in 0 until abilitiesTag.size) {
            val abilityTag = abilitiesTag.getCompound(i)
            val existingAbility = ItemAbility.readNbt(abilityTag)
            if (existingAbility == ability) {
                return // Ability already exists, exit without adding
            }
        }

        // Add new ability
        abilitiesTag.add(ability.writeNbt())
        tag.put("Abilities", abilitiesTag)

        // Optionally set the new ability as the active one
        if (makeActive) {
            setActiveAbility(stack, ability)
        }
    }

    // Function to set the active ability in NBT
    fun setActiveAbility(stack: ItemStack, ability: ItemAbility) {
        val tag = stack.orCreateTag
        val abilities = getItemAbilities(stack)

        if (abilities.contains(ability)) {
            // Store the active ability as a string in NBT
            tag.putString("ActiveAbility", ability.name)
        }
    }

    // Function to get the active ability from the ItemStack's NBT
    fun getActiveAbility(stack: ItemStack): ItemAbility? {
        val tag = stack.tag ?: return null
        val activeAbilityName = tag.getString("ActiveAbility")

        // Check if the active ability is one of the stored abilities
        val abilities = getItemAbilities(stack)
        return abilities.firstOrNull { it.name == activeAbilityName }
    }
}