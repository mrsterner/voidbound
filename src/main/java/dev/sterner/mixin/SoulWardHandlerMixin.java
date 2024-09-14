package dev.sterner.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.sammy.malum.core.handlers.SoulWardHandler;
import dev.sterner.VoidBound;
import dev.sterner.registry.VoidBoundItemRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(SoulWardHandler.ClientOnly.class)
public class SoulWardHandlerMixin {

    @ModifyReturnValue(method = "getSoulWardTexture", at = @At("RETURN"))
    private static ResourceLocation voidbound$modSoulWardTexture(ResourceLocation original) {
        if (Minecraft.getInstance().player != null) {
            boolean bl = Minecraft.getInstance().player.getItemBySlot(EquipmentSlot.HEAD).is(VoidBoundItemRegistry.INSTANCE.getICHORIUM_CIRCLET().get());
            if (bl) {
                return VoidBound.INSTANCE.id("textures/gui/soul_ward/ichorium.png");
            }
        }
        return original;
    }
/*
    @Inject(method = "renderSoulWard", at = @At("HEAD"), cancellable = true)
    private static void v(GuiGraphics guiGraphics, Window window, CallbackInfo ci){
        Minecraft minecraft = Minecraft.getInstance();
        PoseStack poseStack = guiGraphics.pose();
        if (!minecraft.options.hideGui) {
            LocalPlayer player = minecraft.player;
            if (!player.isCreative() && !player.isSpectator()) {
                SoulWardHandler soulWardHandler = ((MalumPlayerDataComponent) MalumComponents.MALUM_PLAYER_COMPONENT.get(player)).soulWardHandler;
                float soulWard = soulWardHandler.soulWard;
                if (soulWard > 0.0F) {
                    float absorb = (float) Mth.ceil(player.getAbsorptionAmount());
                    float maxHealth = (float)player.getAttribute(Attributes.MAX_HEALTH).getValue();
                    float armor = (float)player.getAttribute(Attributes.ARMOR).getValue();
                    int left = window.getGuiScaledWidth() / 2 - 91;
                    int top = window.getGuiScaledHeight() - 59;
                    if (armor == 0.0F) {
                        top += 4;
                    }

                    int healthRows = Mth.ceil((maxHealth + absorb) / 2.0F / 10.0F);
                    int rowHeight = Math.max(10 - (healthRows - 2), 3);
                    poseStack.pushPose();
                    RenderSystem.setShaderTexture(0, getSoulWardTexture());
                    RenderSystem.depthMask(true);
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    //ExtendedShaderInstance shaderInstance = (ExtendedShaderInstance) LodestoneShaderRegistry.DISTORTED_TEXTURE.getInstance().get();
                    //shaderInstance.safeGetUniform("YFrequency").set(15.0F);
                    //shaderInstance.safeGetUniform("XFrequency").set(15.0F);
                    //shaderInstance.safeGetUniform("Speed").set(550.0F);
                    //shaderInstance.safeGetUniform("Intensity").set(120.0F);
                    VFXBuilders.ScreenVFXBuilder builder = VFXBuilders.createScreen().setPosColorTexDefaultFormat().setShader(() -> {
                        return GameRenderer.getPositionColorTexShader();
                    });
                    int size = 13;
                    boolean forceDisplay = soulWard <= 1.0F;
                    double soulWardAmount = forceDisplay ? 1.0 : Math.ceil(Math.floor((double)soulWard) / 3.0);

                    for(int i = 0; (double)i < soulWardAmount; ++i) {
                        int row = (int)((float)i / 10.0F);
                        int x = left + i % 10 * 8;
                        int y = top - row * 4 + rowHeight * 2 - 15;
                        int progress = Math.min(3, (int)soulWard - i * 3);
                        int xTextureOffset = forceDisplay ? 31 : 1 + (3 - progress) * 15;
                        //shaderInstance.safeGetUniform("UVCoordinates").set(new Vector4f((float)xTextureOffset / 45.0F, (float)(xTextureOffset + size) / 45.0F, 0.0F, 0.33333334F));
                        //shaderInstance.safeGetUniform("TimeOffset").set((float)i * 150.0F);
                        builder.setPositionWithWidth((float)(x - 2), (float)(y - 2), (float)size, (float)size).setUVWithWidth((float)xTextureOffset, 0.0F, (float)size, (float)size, 45.0F).draw(poseStack);
                    }

                    //shaderInstance.setUniformDefaults();
                    RenderSystem.depthMask(true);
                    RenderSystem.disableBlend();
                    poseStack.popPose();
                }
            }
        }
        ci.cancel();
    }

 */

/*

    @Redirect(method = "renderSoulWard", at = @At(value = "INVOKE", target = "Lteam/lodestar/lodestone/systems/rendering/VFXBuilders;createScreen()Lteam/lodestar/lodestone/systems/rendering/VFXBuilders$ScreenVFXBuilder;"))
    private static VFXBuilders.ScreenVFXBuilder voidbound$modSoulWardScreen(){

        return VFXBuilders.createScreen().setPosColorTexDefaultFormat().setShader(() -> {

            return GameRenderer.getPositionTexShader();
        });
    }

 */
}
