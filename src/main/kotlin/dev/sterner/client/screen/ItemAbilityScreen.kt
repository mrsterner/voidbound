package dev.sterner.client.screen

import com.mojang.blaze3d.systems.RenderSystem
import dev.sterner.VoidBound
import dev.sterner.api.item.ItemAbility
import dev.sterner.api.util.VoidBoundItemUtils
import dev.sterner.networking.AbilityUpdatePacket
import dev.sterner.registry.VoidBoundComponentRegistry
import dev.sterner.registry.VoidBoundItemRegistry
import dev.sterner.registry.VoidBoundPacketRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack

class ItemAbilityScreen(var stack: ItemStack) : Screen(Component.literal("Ability Selection")) {

    var focus: Boolean = false
    private var yOffset = 0f
    private var selection: Int = 0
    private var initialized = false
    var abilities: Set<ItemAbility>? = null

    private var w: Int = 0
    private var h: Int = 0

    init {
        this.minecraft = Minecraft.getInstance()

        focus = false
        yOffset = 0f
        selection = 0
        initialized = false

        w = 180
        h = 41
        val compatibleAbilities = ItemAbility.getAvailableAbilitiesFromItem(stack.item)
        val unlockedAbilities = VoidBoundComponentRegistry.VOID_BOUND_REVELATION_COMPONENT.get(minecraft!!.player!!).unlockedItemAbilities
        abilities = compatibleAbilities.intersect(unlockedAbilities)

        val activeAbility = VoidBoundItemUtils.getActiveAbility(stack)
        if (activeAbility != null && abilities!!.isNotEmpty()) {
            selection = abilities!!.indexOfFirst { it.name == activeAbility.name }.takeIf { it >= 0 } ?: 0
        }
    }

    fun cycle(direction: Int) {
        if (abilities!!.isNotEmpty()) {
            selection += if ((direction < 0)) 1 else -1
            selection = (selection + abilities!!.size) % abilities!!.size
        }
    }

    fun update() {
        if (focus) {
            yOffset += (85 - yOffset) * .1f
        } else {
            yOffset *= .9f
        }
    }

    fun render(guiGraphics: GuiGraphics, partialTick: Float) {

        if (abilities?.isEmpty() == true) {
            return
        }

        val matrixStack = guiGraphics.pose()
        val mainWindow = minecraft!!.window
        val x = (mainWindow.guiScaledWidth - w) / 2 + 15
        val y = mainWindow.guiScaledHeight - h + 20

        matrixStack.pushPose()
        matrixStack.translate(0f, 20 - yOffset, -100f)

        val gray = VoidBound.id("textures/gui/ability_selection.png")
        RenderSystem.enableBlend()
        RenderSystem.setShaderColor(1f, 1f, 1f, if (focus) 7 / 8f else 0.2f)

        guiGraphics.blit(
            gray,
            x - 15,
            y,
            0f,
            0f,
            w,
            h,
            256,
            256
        )

        val abilityWidth = 50  // Width of one ability + some space
        val totalWidth = abilities!!.size * abilityWidth
        val startX = (mainWindow.guiScaledWidth - totalWidth) / 2

        // Render the abilities
        for ((index, ability) in abilities!!.withIndex()) {  // Loop through the Set directly
            RenderSystem.enableBlend()
            matrixStack.pushPose()

            val alpha = if (focus) 1f else 0.2f

            if (index == selection) {
                matrixStack.translate(0f, -10f, 0f)  // Move selected ability
            } else {
                matrixStack.translate(0f, -5f, 0f)   // Move unselected abilities
            }

            RenderSystem.setShaderColor(1f, 1f, 1f, alpha)

            val abilityX = startX + index * abilityWidth

            if (index == selection) {
                guiGraphics.drawCenteredString(
                    minecraft!!.font,
                    Component.translatable("voidbound.ability." + ability.name.lowercase()),
                    abilityX + 24,
                    y + 35,
                    0xDDCCFF
                )
            }

            val texture = VoidBound.id("textures/gui/" + ability.name.lowercase() + ".png")
            guiGraphics.blit(texture, abilityX + 16, y + 18, 0, 0f, 0f, 16, 16, 16, 16)

            matrixStack.popPose()
        }
        if (focus) {
            // Render the item name in the middle of the screen
            val itemName = stack.hoverName // Get the item's display name from the ItemStack
            val itemNameX = mainWindow.guiScaledWidth / 2
            val itemNameY = y - 10 // Adjust the Y position above the abilities

            guiGraphics.drawCenteredString(
                minecraft!!.font,
                itemName, // Use the item name here
                itemNameX,
                itemNameY,
                0xFFFFFF  // White color
            )
        }


        RenderSystem.setShaderColor(1f, 1f, 1f, 1f)
        RenderSystem.disableBlend()
        matrixStack.popPose()
    }

    override fun onClose() {
        if (Minecraft.getInstance().player != null && abilities!!.elementAtOrNull(selection) != null) {

            VoidBoundPacketRegistry.VOID_BOUND_CHANNEL.sendToServer(
                AbilityUpdatePacket(
                    Minecraft.getInstance().player!!.uuid,
                    abilities!!.elementAt(selection),
                    stack.`is`(VoidBoundItemRegistry.ICHORIUM_CIRCLET.get())
                )
            )
        }
    }
}