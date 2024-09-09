package dev.sterner.mixin.ward;

import dev.sterner.api.VoidBoundApi;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.piston.PistonBaseBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PistonBaseBlock.class)
public class PistonBaseBlockMixin {

    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    private static void isPushable(BlockState state, Level level, BlockPos pos, Direction movementDirection, boolean allowDestroy, Direction pistonFacing, CallbackInfoReturnable<Boolean> cir) {
        boolean warded = !VoidBoundApi.INSTANCE.canBlockBreak(level, pos);
        if (warded) {
            cir.setReturnValue(false);
        }
    }
}
