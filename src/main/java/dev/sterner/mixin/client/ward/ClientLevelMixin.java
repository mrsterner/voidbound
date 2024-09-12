package dev.sterner.mixin.client.ward;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import dev.sterner.api.util.VoidBoundPlayerUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ClientLevel.class)
public class ClientLevelMixin {

    @WrapOperation(method = "destroyBlockProgress", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/LevelRenderer;destroyBlockProgress(ILnet/minecraft/core/BlockPos;I)V"))
    private void voidbound$wardDestroyBlockProgress(LevelRenderer instance, int breakerId, BlockPos pos, int progress, Operation<Void> original) {
        ClientLevel clientLevel = ClientLevel.class.cast(this);
        if (Minecraft.getInstance().player == null || VoidBoundPlayerUtils.INSTANCE.canPlayerBreakBlock(clientLevel, Minecraft.getInstance().player, pos)) {
            original.call(instance, breakerId, pos, progress);
        }
    }
}
