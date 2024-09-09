package dev.sterner.mixin.ward;

import dev.sterner.api.VoidBoundApi;
import dev.sterner.registry.VoidBoundComponentRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.core.GlobalPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Level.class)
public class LevelMixin {

    @Inject(method = "destroyBlock", at = @At("RETURN"))
    private void voidbound$destroyBlock(BlockPos pos, boolean dropBlock, Entity entity, int recursionLeft, CallbackInfoReturnable<Boolean> cir) {
        if (cir.getReturnValue()) {
            var level = (Level.class.cast(this));
            var comp = VoidBoundComponentRegistry.Companion.getVOID_BOUND_WORLD_COMPONENT().get(level);
            comp.removePos(GlobalPos.of(level.dimension(), pos));
        }
    }

    @Inject(method = "destroyBlock", at = @At("HEAD"), cancellable = true)
    private void voidbound$destroyBlock2(BlockPos pos, boolean dropBlock, Entity entity, int recursionLeft, CallbackInfoReturnable<Boolean> cir){
        if (!VoidBoundApi.INSTANCE.canBlockBreak((Level) (Object) this, pos)) {
            cir.setReturnValue(false);
        }
    }
}
