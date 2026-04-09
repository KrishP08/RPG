package org.rpg.events;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.boss.dragon.EnderDragonEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.*;
import net.minecraft.server.network.ServerPlayerEntity;
import org.rpg.stats.PlayerStatsManager;
import org.rpg.stats.StatType;

public class MobKillHandler {
    public static void onModDeath(LivingEntity entity, DamageSource source){
        if (!(source.getAttacker() instanceof ServerPlayerEntity player)) return;

        if(entity instanceof ServerPlayerEntity) return;

        XpReward reward=getReward(entity);
        if (reward == null) return;

        PlayerStatsManager.addXp(player,reward.stat,reward.amount);
    }

    private static XpReward getReward(LivingEntity entity){
        if(entity instanceof EnderDragonEntity)
            return new XpReward(StatType.STRENGTH,500);
        if(entity instanceof WitherEntity)
            return new XpReward(StatType.STRENGTH,400);
        if(entity instanceof ElderGuardianEntity)
            return new XpReward(StatType.MAGIC, 200);
        if(entity instanceof RavagerEntity)
            return new XpReward(StatType.STRENGTH,150);
        if(entity instanceof EvokerEntity)
            return new XpReward(StatType.MAGIC,100);
        // Hostile overworld mobs
        if(entity instanceof ZombieEntity || entity instanceof ZombieVillagerEntity)
            return new XpReward(StatType.STRENGTH,15);
        if(entity instanceof SkeletonEntity || entity instanceof StrayEntity)
            return new XpReward(StatType.AGILITY,15);
        if(entity instanceof CreeperEntity)
            return new XpReward(StatType.ENDURANCE,20);
        if(entity instanceof SpiderEntity || entity instanceof CaveSpiderEntity)
            return new XpReward(StatType.AGILITY,12);
        if(entity instanceof WitchEntity)
            return new XpReward(StatType.MAGIC,30);
        if(entity instanceof EndermanEntity)
            return new XpReward(StatType.MAGIC,25);
        if(entity instanceof HuskEntity)
            return new XpReward(StatType.STRENGTH,18);
        if(entity instanceof DrownedEntity)
            return new XpReward(StatType.STRENGTH,18);
        if(entity instanceof PhantomEntity)
            return new XpReward(StatType.AGILITY,20);
        if(entity instanceof VindicatorEntity)
            return new XpReward(StatType.STRENGTH,30);
        if(entity instanceof PillagerEntity)
            return new XpReward(StatType.AGILITY,20);
        //Nether Mods
        if(entity instanceof ZombifiedPiglinEntity)
            return new XpReward(StatType.STRENGTH,20);
        if(entity instanceof BlazeEntity)
            return new XpReward(StatType.MAGIC,35);
        if(entity instanceof GhastEntity)
            return new XpReward(StatType.MAGIC,40);
        if(entity instanceof MagmaCubeEntity)
            return new XpReward(StatType.ENDURANCE,15);
        if(entity instanceof PiglinEntity)
            return new XpReward(StatType.STRENGTH,20);
        if(entity instanceof PiglinBruteEntity)
            return new XpReward(StatType.STRENGTH,50);
        if(entity instanceof WitherSkeletonEntity)
            return new XpReward(StatType.ENDURANCE,45);
        if(entity instanceof HoglinEntity)
            return new XpReward(StatType.STRENGTH,35);
        //End Mods
        if(entity instanceof EndermiteEntity)
            return new XpReward(StatType.MAGIC,10);
        if(entity instanceof ShulkerEntity)
            return new XpReward(StatType.ENDURANCE,30);
        //Passive Animals
        if(entity instanceof CowEntity || entity instanceof PiglinEntity || entity instanceof SheepEntity || entity instanceof ChickenEntity)
            return new XpReward(StatType.MINING,5);
        //Neutral Mobs
        if(entity instanceof WolfEntity)
            return new XpReward(StatType.AGILITY,8);
        if(entity instanceof IronGolemEntity)
            return new XpReward(StatType.STRENGTH,30);
        //Generic Fallback for any other mob
        return new XpReward(StatType.STRENGTH,10);
    }
    private record XpReward(StatType stat,int amount){}
}
