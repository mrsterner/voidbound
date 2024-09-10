package dev.sterner.client.screen

import com.mojang.blaze3d.systems.RenderSystem
import dev.sterner.VoidBound
import dev.sterner.api.VoidBoundApi
import dev.sterner.api.item.ItemAbility
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.Screen
import net.minecraft.network.chat.Component
import net.minecraft.world.item.ItemStack
import kotlin.math.max

class ItemAbilityScreen(stack: ItemStack) : Screen(Component.literal("Ability Selection")) {

    var focus: Boolean = false
    private var yOffset = 0f
    private var selection: Int = 0
    private var initialized = false
    var abilities: List<ItemAbility>? = null

    private var w: Int = 0
    private var h: Int = 0

    init {
        this.minecraft = Minecraft.getInstance()
        abilities = VoidBoundApi.getItemAbility(stack)
        focus = false
        yOffset = 0f
        selection = 0
        initialized = false

        w = 180
        h = 37
    }

    fun cycle(direction: Int) {
        selection += if ((direction < 0)) 1 else -1
        selection = (selection + abilities!!.size) % abilities!!.size
    }

    fun update() {
        if (focus) {
            yOffset += (85 - yOffset) * .1f
        } else {
            yOffset *= .9f
        }
    }

    fun render(guiGraphics: GuiGraphics, partialTick: Float) {

        if (abilities == null) {
            return
        }

        val matrixStack = guiGraphics.pose()
        val mainWindow = minecraft!!.window
        val x = (mainWindow.guiScaledWidth - w) / 2 + 15
        val y = mainWindow.guiScaledHeight - h + 20

        matrixStack.pushPose()
        matrixStack.translate(0f, 20 - yOffset, (if (focus) 100 else 0).toFloat())

        val gray = VoidBound.id("textures/gui/ability_selection.png")
        RenderSystem.enableBlend()
        RenderSystem.setShaderColor(1f, 1f, 1f, if (focus) 7 / 8f else 0f)

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

        for (i in abilities!!.indices) {
            RenderSystem.enableBlend()
            matrixStack.pushPose()

            val alpha = if (focus) 1f else 0.2f

            if (i == selection) {
                matrixStack.translate(0f, -10f, 0f)
            } else {
                matrixStack.translate(0f, -5f, 0f)
            }

            RenderSystem.setShaderColor(1f, 1f, 1f, alpha)

            val abilityX = startX + i * abilityWidth

            if (i == selection) {
                guiGraphics.drawCenteredString(
                    minecraft!!.font,
                    abilities!![i].name.lowercase().replaceFirstChar { it.uppercase() },
                    abilityX + 24,
                    y + 32,
                    0xDDCCFF
                )
            }

            val texture = VoidBound.id("textures/gui/" + abilities!![i].name.lowercase() + ".png")
            guiGraphics.blit(texture, abilityX + 16, y + 16, 0, 0f, 0f, 16, 16, 16, 16)

            matrixStack.popPose()
        }

        RenderSystem.disableBlend()
        matrixStack.popPose()
    }
}