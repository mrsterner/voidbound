package dev.sterner.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.VoidBound
import net.minecraft.client.model.EntityModel
import net.minecraft.client.model.HumanoidModel
import net.minecraft.client.model.Model
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartNames
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.LivingEntity
import java.util.function.Function

class IchoriumCircletModel<T : LivingEntity>(root: ModelPart) :
    HumanoidModel<T>(root, RenderType::entityTranslucent) {

    private val base: ModelPart = root.getChild("head").getChild("base")

    override fun renderToBuffer(
        poseStack: PoseStack,
        vertexConsumer: VertexConsumer,
        packedLight: Int,
        packedOverlay: Int,
        red: Float,
        green: Float,
        blue: Float,
        alpha: Float
    ) {
        base.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation =
            ModelLayerLocation(VoidBound.id("ichorium_circlet"), "main")

        fun createBodyLayer(): LayerDefinition {
            val data = createMesh(CubeDeformation.NONE, 0f)
            val root = data.root
            val head = root.getChild(PartNames.HEAD)

            val base = head.addOrReplaceChild(
                "base",
                CubeListBuilder.create().texOffs(27, 9)
                    .addBox(-4.5f, -1.0f, -4.5f, 9.0f, 9.0f, 9.0f, CubeDeformation(0.25f))
                    .texOffs(0, 18).addBox(-4.5f, -1.0f, -4.5f, 9.0f, 9.0f, 9.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-4.5f, -1.0f, -4.5f, 9.0f, 9.0f, 9.0f, CubeDeformation(0.5f)),
                PartPose.offset(0.0f, 16.0f, 0.0f)
            )

            return LayerDefinition.create(data, 64, 64)
        }
    }
}