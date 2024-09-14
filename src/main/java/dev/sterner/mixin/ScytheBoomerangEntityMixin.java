package dev.sterner.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.sammy.malum.common.entity.boomerang.ScytheBoomerangEntity;
import dev.sterner.api.entity.IchoriumScytheGhost;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ScytheBoomerangEntity.class)
public class ScytheBoomerangEntityMixin implements IchoriumScytheGhost {

    @Unique
    boolean isGhost = false;

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    private boolean voidbound$tick(Level instance, Entity entity, @Local ItemEntity itemEntity) {
        return !isGhost();
    }

    @WrapWithCondition(method = "tick", at = @At(value = "INVOKE", target = "Lio/github/fabricators_of_create/porting_lib/transfer/item/ItemHandlerHelper;giveItemToPlayer(Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/item/ItemStack;I)V"))
    private boolean voidbound$tickGive(Player tx, ItemStack entityitem, int player) {
        return !isGhost();
    }

    @Override
    public boolean isGhost() {
        return isGhost;
    }

    @Override
    public void setGhost(boolean ghost) {
        isGhost = ghost;
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void voidbound$addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("isGhost", isGhost());
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void voidbound$readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        setGhost(tag.getBoolean("Ghost"));
    }
}
