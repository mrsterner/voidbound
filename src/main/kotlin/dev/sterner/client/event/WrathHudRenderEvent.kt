package dev.sterner.client.event

import com.mojang.blaze3d.systems.RenderSystem
import com.sammy.malum.common.components.MalumComponents
import com.sammy.malum.common.components.MalumPlayerDataComponent
import dev.sterner.VoidBound
import dev.sterner.api.item.ItemAbility
import dev.sterner.api.util.VoidBoundItemUtils
import dev.sterner.registry.VoidBoundComponentRegistry
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.util.Mth
import net.minecraft.world.entity.ai.attributes.Attributes
import team.lodestar.lodestone.systems.rendering.VFXBuilders
import kotlin.math.max
import kotlin.math.min

object WrathHudRenderEvent {

    fun renderWrathBar(guiGraphics: GuiGraphics, partialTick: Float) {
        val minecraft = Minecraft.getInstance()
        if (minecraft.player == null) {
            return
        }

        val comp = VoidBoundComponentRegistry.VOID_BOUND_PLAYER_ITEM_ABILITY_COMPONENT.get(minecraft.player!!)

        val wrath = comp.getWrath()

        val ability = VoidBoundItemUtils.getActiveAbility(minecraft.player!!.mainHandItem)
        if (ability != ItemAbility.FINALE && ability != ItemAbility.OPENER) {
            return
        }

        if (wrath <= 0) {
            return
        }

        val window = minecraft.window
        val player = minecraft.player!!

        if (player.isCreative() || player.isSpectator()) {
            return
        }

        val absorb = Mth.ceil(player.absorptionAmount).toFloat()
        val maxHealth: Float = player.getAttribute(Attributes.MAX_HEALTH)!!.getValue().toFloat()
        val armor: Float = player.getAttribute(Attributes.ARMOR)!!.getValue().toFloat()
        val left = window.guiScaledWidth / 2 - 89
        var top = window.guiScaledHeight - 45
        if (armor == 0.0f) {
            top += 4
        }
        val soulWardHandler = MalumComponents.MALUM_PLAYER_COMPONENT[player].soulWardHandler
        val soulWard = if(soulWardHandler.soulWard > 0) -9 else 0

        val poseStack = guiGraphics.pose()
        val healthRows = Mth.ceil((maxHealth + absorb) / 2.0f / 10.0f)
        val rowHeight = max((10 - (healthRows - 2)).toDouble(), 3.0).toInt() + soulWard

        poseStack.pushPose()
        RenderSystem.setShaderTexture(0, VoidBound.id("textures/gui/wrath_empty.png"))
        RenderSystem.depthMask(true)
        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        val builder = VFXBuilders.createScreen().setPosColorTexDefaultFormat()
            .setShader { GameRenderer.getPositionColorTexShader() }

        for (i in 0 until 10) {
            val row = (i.toFloat() / 10.0f).toInt()
            val x = left + i % 10 * 8
            val y = top - row * 4 + rowHeight * 2 - 15
            builder.setPositionWithWidth((x - 2).toFloat(), (y - 2).toFloat(), 9f, 9f)
                .setUVWithWidth(0f,0f,9f,9f,9f,9f)
                .draw(poseStack)
        }

        RenderSystem.setShaderTexture(0, VoidBound.id("textures/gui/wrath_full.png"))
        for (i in 0 until wrath) {
            val row = (i.toFloat() / 10.0f).toInt()
            val x = left + i % 10 * 8
            val y = top - row * 4 + rowHeight * 2 - 15
            builder.setPositionWithWidth((x - 2).toFloat(), (y - 2).toFloat(), 9f, 9f)
                .setUVWithWidth(0f,0f,9f,9f,9f,9f)
                .draw(poseStack)
        }

        RenderSystem.depthMask(true)
        RenderSystem.disableBlend()
        poseStack.popPose()
    }
}