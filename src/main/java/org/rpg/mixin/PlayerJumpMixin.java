package org.rpg.mixin;

import org.rpg.skills.SkillEffectsHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public class PlayerJumpMixin {

    @Inject(method = "jump", at = @At("HEAD"))
    private void onJump(CallbackInfo ci) {
        // Only process if this is actually a ServerPlayerEntity
        if ((Object)this instanceof ServerPlayerEntity player) {
            SkillEffectsHandler.markJump(player);
        }
    }
}