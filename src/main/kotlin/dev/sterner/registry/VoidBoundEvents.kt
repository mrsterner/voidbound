package dev.sterner.registry

import com.google.common.collect.Multimap
import com.sammy.malum.common.events.MalumCodexEvents
import dev.sterner.VoidBoundClient
import dev.sterner.api.ClientTickHandler
import dev.sterner.api.item.ItemAbility
import dev.sterner.api.util.VoidBoundItemUtils
import dev.sterner.client.event.*
import dev.sterner.common.components.VoidBoundPlayerComponent
import dev.sterner.common.components.VoidBoundPlayerItemAbilityComponent
import dev.sterner.common.components.VoidBoundWorldComponent
import dev.sterner.common.item.equipment.TidecutterItem
import dev.sterner.common.item.equipment.UpgradableTool
import io.github.fabricators_of_create.porting_lib.entity.events.living.LivingHurtEvent
import io.github.fabricators_of_create.porting_lib.event.common.BlockEvents
import net.fabricmc.api.EnvType
import net.fabricmc.api.Environment
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.fabricmc.fabric.api.item.v1.ModifyItemAttributeModifiersCallback
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.world.entity.EquipmentSlot
import net.minecraft.world.entity.ai.attributes.Attribute
import net.minecraft.world.entity.ai.attributes.AttributeModifier
import net.minecraft.world.entity.ai.attributes.Attributes
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import team.lodestar.lodestone.events.LodestoneInteractionEvent
import java.util.*


object VoidBoundEvents {

    fun init() {
        UseBlockCallback.EVENT.register(VoidBoundPlayerComponent.Companion::useBlock)
        UseEntityCallback.EVENT.register(VoidBoundPlayerComponent.Companion::useEntity)
        BlockEvents.BLOCK_BREAK.register(VoidBoundWorldComponent.Companion::removeWard)
        BlockEvents.BLOCK_BREAK.register(TidecutterItem.Companion::breakBlock)
        LodestoneInteractionEvent.RIGHT_CLICK_ITEM.register(VoidBoundPlayerItemAbilityComponent::onRightClickItem)
        LivingHurtEvent.HURT.register {
            val attacker = it.source.entity
            if (attacker is Player) {
                val item = attacker.mainHandItem
                val bl = VoidBoundItemUtils.getActiveAbility(item) == ItemAbility.VAMPIRISM
                if (bl) {
                    attacker.heal(attacker.random.nextInt(2).toFloat())
                }

                val bl2 = VoidBoundItemUtils.getActiveAbility(item) == ItemAbility.OPENER
                if (bl2 && it.entity.health / it.entity.maxHealth > 0.95) {
                    it.amount *= 1.5f
                }
            }
        }

        /**
         * Add extra damage to UpgradableTools when it has extra damage
         */
        ModifyItemAttributeModifiersCallback.EVENT.register(ModifyItemAttributeModifiersCallback { stack: ItemStack, slot: EquipmentSlot, attributeModifiers: Multimap<Attribute?, AttributeModifier?> ->
            if (stack.item is UpgradableTool && slot == EquipmentSlot.MAINHAND) {
                val tool = stack.item as UpgradableTool
                attributeModifiers.put(
                    Attributes.ATTACK_DAMAGE, AttributeModifier(
                        UUID.fromString("DB3F55D3-645C-4F38-A497-9C13A33DB5CF"),
                        "Weapon modifier2",
                        tool.getExtraDamage(stack).toDouble(),
                        AttributeModifier.Operation.ADDITION
                    )
                )
            }
        })
    }

    @Environment(EnvType.CLIENT)
    fun clientInit() {
        MalumCodexEvents.EVENT.register(MalumCodexEvent::addVoidBoundEntries)
        MalumCodexEvents.VOID_EVENT.register(MalumCodexEvent::addVoidBoundVoidEntries)
        WorldRenderEvents.AFTER_TRANSLUCENT.register(VoidBoundPlayerComponent.Companion::renderCubeAtPos)
        WorldRenderEvents.AFTER_TRANSLUCENT.register(VoidBoundWorldComponent.Companion::renderCubeAtPos)
        HudRenderCallback.EVENT.register(SpiritAltarHudRenderEvent::spiritAltarRecipeHud)
        HudRenderCallback.EVENT.register(RiftHudRenderEvent::spiritRiftHud)
        HudRenderCallback.EVENT.register(SpiritJarHudRenderEvent::spiritJarHud)
        HudRenderCallback.EVENT.register(ThoughtsTextHudRenderEvent::renderThoughts)
        ClientTickEvents.END_CLIENT_TICK.register(ClientTickHandler::clientTickEnd)
        ClientTickEvents.END_CLIENT_TICK.register {
            VoidBoundClient.ITEM_ABILITY_HANDLER.tick()
        }
        HudRenderCallback.EVENT.register(HudRenderCallback { graphics: GuiGraphics, partialTicks: Float ->
            val window = Minecraft.getInstance().window
            VoidBoundClient.ITEM_ABILITY_HANDLER.render(graphics, partialTicks, window.width, window.height)
        })
    }
}