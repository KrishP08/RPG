package org.rpg.mixin;

import org.rpg.stats.PlayerStatsManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
@Mixin(ServerPlayerEntity.class)
public class PlayerEntityMixin {
    @Inject(method = "writeCustomDataToNbt",at=@At("TAIL"))
    private void onWriteNbt(NbtCompound nbt, CallbackInfo ci){
        ServerPlayerEntity self=(ServerPlayerEntity)(Object)this;
        PlayerStatsManager.writeToNbt(self,nbt);
    }
    @Inject(method="readCustomDataFromNbt",at=@At("TAIL"))
    private void onReadNbt(NbtCompound nbt,CallbackInfo ci){
        ServerPlayerEntity self=(ServerPlayerEntity)(Object)this;
        PlayerStatsManager.readFromNbt(self,nbt);
        PlayerStatsManager.applyStatBounuses(self,PlayerStatsManager.get(self));
    }
}