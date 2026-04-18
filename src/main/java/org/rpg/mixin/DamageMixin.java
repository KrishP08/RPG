package org.rpg.mixin;

import org.rpg.skills.SkillEffectsHandler;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntity.class)
public abstract class DamageMixin {

    @ModifyVariable(method = "damage", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    private float modifyDamage(float amount, DamageSource source) {
        LivingEntity self = (LivingEntity)(Object)this;

        if (self instanceof ServerPlayerEntity victim) {
            if (SkillEffectsHandler.tryEvasion(victim)) {
                return 0f;
            }
        }
        if (source.getAttacker() instanceof ServerPlayerEntity attacker) {

            if (source.getSource() == attacker) {
                float multiplier = SkillEffectsHandler.getDamageMultiplier(attacker);
                return amount * multiplier;
            }
        }

        return amount;
    }
}
