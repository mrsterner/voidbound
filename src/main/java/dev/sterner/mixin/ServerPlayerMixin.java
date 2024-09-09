package dev.sterner.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.mixin_logic.ServerPlayerMixinLogic;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin {

    @Inject(method = "onInsideBlock", at = @At("HEAD"))
    private void voidbound$onInsideBlock(BlockState state, CallbackInfo ci) {
        ServerPlayerMixinLogic.INSTANCE.logic1((ServerPlayer) (Object) this, state);
    }

    @Inject(method = "triggerDimensionChangeTriggers", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/advancements/critereon/ChangeDimensionTrigger;trigger(Lnet/minecraft/server/level/ServerPlayer;Lnet/minecraft/resources/ResourceKey;Lnet/minecraft/resources/ResourceKey;)V"

    ))
    private void voidbound$triggerDimensionChangeTriggers(ServerLevel level, CallbackInfo ci, @Local(ordinal = 0) ResourceKey<Level> resourceKey, @Local(ordinal = 1) ResourceKey<Level> resourceKey2) {
        ServerPlayer player = (ServerPlayer) (Object) this;
        ServerPlayerMixinLogic.INSTANCE.logic2(player, level, resourceKey, resourceKey2);
    }
}
