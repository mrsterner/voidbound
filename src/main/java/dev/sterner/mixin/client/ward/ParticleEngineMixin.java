package dev.sterner.mixin.client.ward;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import dev.sterner.mixin_logic.ParticleEngineMixinLogic;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(ParticleEngine.class)
public class ParticleEngineMixin {

    @Shadow
    protected ClientLevel level;

    @Shadow
    @Final
    private RandomSource random;

    @WrapWithCondition(method = "crack", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/particle/ParticleEngine;add(Lnet/minecraft/client/particle/Particle;)V"))
    private boolean voidbound$ward(ParticleEngine instance, Particle effect, @Local(argsOnly = true) BlockPos pos, @Local(argsOnly = true) Direction side, @Local BlockState blockState) {
        return ParticleEngineMixinLogic.INSTANCE.logic(level, pos, blockState, random, side);
    }
}
