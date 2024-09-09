package dev.sterner.mixin.client;

import dev.sterner.api.ClientTickHandler;
import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public abstract class MinecraftMixin {
    @Shadow
    public abstract boolean isPaused();

    @Shadow
    public float pausePartialTick;

    @Shadow
    public abstract float getFrameTime();


    @Inject(method = "runTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GameRenderer;render(FJZ)V"))
    private void voidbound$onFrameStart(boolean tick, CallbackInfo ci) {
        ClientTickHandler.INSTANCE.renderTick(isPaused() ? pausePartialTick : getFrameTime());
    }
}