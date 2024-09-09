package dev.sterner.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.sterner.client.renderer.GrimcultRitesRenderer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class ItemInHandRendererMixin {

    @Inject(method = "renderItem", at = @At("HEAD"), cancellable = true)
    private void voidbound$renderFirstPersonItem(LivingEntity livingEntity, ItemStack stack, ItemDisplayContext transformType,
                                                 boolean leftHanded, PoseStack poseStack, MultiBufferSource buffers, int light, CallbackInfo ci) {
        if (GrimcultRitesRenderer.renderHand(stack, transformType, leftHanded, poseStack, buffers, light)) {
            ci.cancel();
        }
    }
}