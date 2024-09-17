package dev.sterner.client.renderer.entity

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.math.Axis
import com.sammy.malum.client.RenderUtils
import com.sammy.malum.client.SpiritBasedWorldVFXBuilder
import com.sammy.malum.common.entity.FloatingItemEntity
import com.sammy.malum.core.systems.spirit.MalumSpiritType
import com.sammy.malum.registry.client.MalumRenderTypeTokens
import com.sammy.malum.registry.common.SpiritTypeRegistry
import dev.sterner.common.entity.ItemCarrierItemEntity
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.MultiBufferSource
import net.minecraft.client.renderer.entity.EntityRenderer
import net.minecraft.client.renderer.entity.EntityRendererProvider
import net.minecraft.client.renderer.entity.ItemRenderer
import net.minecraft.client.renderer.texture.OverlayTexture
import net.minecraft.client.renderer.texture.TextureAtlas
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.ItemDisplayContext
import net.minecraft.world.level.Level
import team.lodestar.lodestone.helpers.EasingHelper
import team.lodestar.lodestone.registry.client.LodestoneRenderTypeRegistry
import team.lodestar.lodestone.systems.easing.Easing
import team.lodestar.lodestone.systems.rendering.LodestoneRenderType
import team.lodestar.lodestone.systems.rendering.VFXBuilders.WorldVFXBuilder
import java.awt.Color
import kotlin.math.abs
import kotlin.math.sin

class ItemCarrierItemEntityRenderer(context: EntityRendererProvider.Context) :
    EntityRenderer<ItemCarrierItemEntity>(context) {

    val itemRenderer: ItemRenderer = context.itemRenderer

    override fun render(
        entity: ItemCarrierItemEntity,
        entityYaw: Float,
        partialTicks: Float,
        poseStack: PoseStack,
        bufferIn: MultiBufferSource,
        packedLightIn: Int
    ) {
        val spiritType = SpiritTypeRegistry.AQUEOUS_SPIRIT
        val trailBuilder = SpiritBasedWorldVFXBuilder.create(spiritType).setRenderType(TRAIL_TYPE)
        RenderUtils.renderEntityTrail(
            poseStack,
            trailBuilder,
            entity.trailPointBuilder,
            entity,
            spiritType.primaryColor,
            spiritType.secondaryColor,
            1.0f,
            partialTicks
        )
        renderSpiritEntity(
            entity,
            this.itemRenderer, partialTicks, poseStack, bufferIn, packedLightIn
        )
        super.render(entity, entityYaw, partialTicks, poseStack, bufferIn, packedLightIn)
    }

    override fun getTextureLocation(entity: ItemCarrierItemEntity): ResourceLocation {
        return TextureAtlas.LOCATION_BLOCKS
    }

    init {
        this.shadowRadius = 0.0f
        this.shadowStrength = 0.0f
    }

    companion object {
        private val TRAIL_TYPE: LodestoneRenderType =
            LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE_TRIANGLE.applyAndCache(MalumRenderTypeTokens.CONCENTRATED_TRAIL)
        private val TWINKLE: LodestoneRenderType =
            LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE.applyAndCache(MalumRenderTypeTokens.TWINKLE)
        private val STAR: LodestoneRenderType =
            LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE.applyAndCache(MalumRenderTypeTokens.STAR)

        fun renderSpiritEntity(
            entity: ItemCarrierItemEntity,
            itemRenderer: ItemRenderer,
            partialTicks: Float,
            poseStack: PoseStack,
            bufferIn: MultiBufferSource?,
            packedLightIn: Int
        ) {
            val itemStack = entity.itemStack
            val model = itemRenderer.getModel(itemStack, entity.level(), null as LivingEntity?, entity.itemStack.count)
            val yOffset = entity.getYOffset(partialTicks)
            val scale = model.transforms.getTransform(ItemDisplayContext.GROUND).scale.y()
            val rotation = entity.getRotation(partialTicks)
            poseStack.pushPose()
            poseStack.translate(0.0, (yOffset - 0.25f * scale).toDouble(), 0.0)
            poseStack.mulPose(Axis.YP.rotation(rotation))
            itemRenderer.render(
                itemStack,
                ItemDisplayContext.GROUND,
                false,
                poseStack,
                bufferIn,
                packedLightIn,
                OverlayTexture.NO_OVERLAY,
                model
            )
            poseStack.popPose()
            poseStack.pushPose()
            poseStack.translate(0.0, yOffset.toDouble(), 0.0)
            renderSpiritGlimmer(poseStack, SpiritTypeRegistry.AQUEOUS_SPIRIT, partialTicks)
            poseStack.popPose()
        }

        fun renderSpiritGlimmer(poseStack: PoseStack, spiritType: MalumSpiritType, partialTicks: Float) {
            renderSpiritGlimmer(poseStack, spiritType, 1.0f, partialTicks)
        }

        fun renderSpiritGlimmer(poseStack: PoseStack, spiritType: MalumSpiritType, scalar: Float, partialTicks: Float) {
            renderSpiritGlimmer(
                poseStack,
                SpiritBasedWorldVFXBuilder.create(spiritType),
                spiritType.primaryColor,
                spiritType.secondaryColor,
                scalar,
                scalar,
                partialTicks
            )
        }

        fun renderSpiritGlimmer(
            poseStack: PoseStack,
            builder: WorldVFXBuilder,
            primaryColor: Color?,
            secondaryColor: Color?,
            scaleScalar: Float,
            alphaScalar: Float,
            partialTicks: Float
        ) {
            val level: Level? = Minecraft.getInstance().level
            val gameTime = level!!.gameTime.toFloat() + partialTicks
            val sine = abs(sin((gameTime / 80.0f % 360.0f).toDouble()) * 0.07500000298023224)
                .toFloat()
            val bounce =
                EasingHelper.weightedEasingLerp(Easing.BOUNCE_IN_OUT, gameTime % 20.0f / 20.0f, 0.025f, 0.05f, 0.025f)
            val scale = (0.12f + sine + bounce) * scaleScalar
            poseStack.pushPose()
            poseStack.mulPose(Minecraft.getInstance().entityRenderDispatcher.cameraOrientation())
            poseStack.mulPose(Axis.YP.rotationDegrees(180.0f))
            builder.setAlpha(0.6f * alphaScalar).setColor(primaryColor).setRenderType(STAR)
                .renderQuad(poseStack, scale * 0.8f)
            builder.setAlpha(0.8f * alphaScalar).setRenderType(TWINKLE).renderQuad(poseStack, scale * 0.8f)
            builder.setAlpha(0.2f * alphaScalar).setColor(secondaryColor).renderQuad(poseStack, scale * 1.2f)
            poseStack.popPose()
        }
    }
}