package dev.sterner.mixin.ward;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.api.VoidBoundApi;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Set;

@Mixin(Explosion.class)
public class ExplosionMixin {

    @Shadow
    @Final
    private Level level;

    @WrapWithCondition(method = "explode", at = @At(value = "INVOKE", target = "Ljava/util/Set;add(Ljava/lang/Object;)Z"))
    private boolean voidbound$wardExplosion(Set<BlockPos> instance, Object e, @Local BlockPos blockPos) {
        return VoidBoundApi.INSTANCE.canBlockBreak(this.level, blockPos);
    }
}
