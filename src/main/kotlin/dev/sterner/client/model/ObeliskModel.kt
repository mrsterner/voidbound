package dev.sterner.client.model

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.blaze3d.vertex.VertexConsumer
import dev.sterner.VoidBound
import net.minecraft.client.model.Model
import net.minecraft.client.model.geom.ModelLayerLocation
import net.minecraft.client.model.geom.ModelPart
import net.minecraft.client.model.geom.PartPose
import net.minecraft.client.model.geom.builders.CubeDeformation
import net.minecraft.client.model.geom.builders.CubeListBuilder
import net.minecraft.client.model.geom.builders.LayerDefinition
import net.minecraft.client.model.geom.builders.MeshDefinition
import net.minecraft.client.renderer.RenderType
import net.minecraft.resources.ResourceLocation
import java.util.function.Function

class ObeliskModel(val root: ModelPart) : Model(Function { location: ResourceLocation ->
    RenderType.entityCutoutNoCull(
        location
    )
}) {

    private val bottom: ModelPart = root.getChild("bottom")
    private val bb_main: ModelPart = root.getChild("bb_main")

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
        bottom.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
        bb_main.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha)
    }

    companion object {
        // This layer location should be baked with EntityRendererProvider.Context in the entity renderer and passed into this model's constructor
        val LAYER_LOCATION: ModelLayerLocation = ModelLayerLocation(VoidBound.id("obelisk"), "main")
        fun createBodyLayer(): LayerDefinition {
            val meshdefinition = MeshDefinition()
            val partdefinition = meshdefinition.root

            val bottom = partdefinition.addOrReplaceChild(
                "bottom",
                CubeListBuilder.create().texOffs(81, 135)
                    .addBox(-7.0f, -9.0f, -1.0f, 8.0f, 8.0f, 8.0f, CubeDeformation(0.0f))
                    .texOffs(81, 144).addBox(-7.0f, -95.0f, -1.0f, 8.0f, 8.0f, 8.0f, CubeDeformation(0.0f))
                    .texOffs(90, 0).addBox(-10.5f, -16.0f, -4.5f, 15.0f, 8.0f, 15.0f, CubeDeformation(0.0f))
                    .texOffs(0, 79).addBox(-10.5f, -88.0f, -4.5f, 15.0f, 8.0f, 15.0f, CubeDeformation(0.0f))
                    .texOffs(30, 0).addBox(4.5f, -80.0f, -4.5f, 0.0f, 64.0f, 15.0f, CubeDeformation(0.0f))
                    .texOffs(0, 0).addBox(-10.5f, -80.0f, -4.5f, 0.0f, 64.0f, 15.0f, CubeDeformation(0.0f))
                    .texOffs(60, 64).addBox(-10.5f, -80.0f, -4.5f, 15.0f, 64.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(60, 0).addBox(-10.5f, -80.0f, 10.5f, 15.0f, 64.0f, 0.0f, CubeDeformation(0.0f))
                    .texOffs(0, 129).addBox(-11.0f, -23.0f, -5.0f, 16.0f, 16.0f, 16.0f, CubeDeformation(0.0f))
                    .texOffs(0, 161).addBox(-11.0f, -89.0f, -5.0f, 16.0f, 16.0f, 16.0f, CubeDeformation(0.0f)),
                PartPose.offset(3.0f, 24.0f, -3.0f)
            )

            val bb_main = partdefinition.addOrReplaceChild(
                "bb_main",
                CubeListBuilder.create().texOffs(87, 148)
                    .addBox(-8.0f, -8.0f, -2.0f, 5.0f, 5.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(91, 153).addBox(2.0f, -2.0f, -6.0f, 5.0f, 5.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(87, 147).addBox(-8.0f, -24.0f, -8.0f, 5.0f, 10.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(83, 144).addBox(4.0f, -24.0f, 5.0f, 4.0f, 8.0f, 3.0f, CubeDeformation(0.0f))
                    .texOffs(87, 147).addBox(-8.0f, -59.0f, 3.0f, 5.0f, 6.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(88, 142).addBox(3.0f, -78.0f, -8.0f, 5.0f, 8.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(91, 153).addBox(2.0f, -93.0f, -6.0f, 5.0f, 5.0f, 5.0f, CubeDeformation(0.0f))
                    .texOffs(87, 148).addBox(-7.0f, -92.0f, -2.0f, 5.0f, 5.0f, 5.0f, CubeDeformation(0.0f)),
                PartPose.offset(0.0f, 24.0f, 0.0f)
            )

            return LayerDefinition.create(meshdefinition, 256, 256)
        }
    }
}