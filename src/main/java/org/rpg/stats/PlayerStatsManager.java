package org.rpg.stats;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.server.network.ServerPlayerEntity;
import org.rpg.Rpg;
import org.rpg.network.RpgNetwork;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerStatsManager {
    private static final Map<UUID,PlayerStats> cache=new HashMap<>();

    private static final String NBT_KEY="RpgStats";
    public static PlayerStats get(ServerPlayerEntity player){
        UUID uuid=player.getUuid();
        if(!cache.containsKey(uuid)){
            load(player);
        }
        return cache.get(uuid);
    }
    public static void load(ServerPlayerEntity player){
        var persistentData = player.writeNbt(new net.minecraft.nbt.NbtCompound());
        var tag =player.getServer()!=null
                ? readFromPlayerNbt(player)
                : new PlayerStats();
        cache.put(player.getUuid(),tag);
    }
    private static PlayerStats readFromPlayerNbt(ServerPlayerEntity player){
        net.minecraft.nbt.NbtCompound data=new net.minecraft.nbt.NbtCompound();
        var compound=player.writeNbt(new net.minecraft.nbt.NbtCompound());
        if(compound.contains(NBT_KEY)){
            return PlayerStats.fromNbt(compound.getCompound(NBT_KEY));
        }
        return new PlayerStats();
    }

    public static void save(ServerPlayerEntity player){
        PlayerStats stats=get(player);
    }
    public static void addXp(ServerPlayerEntity player,StatType type,int amount){
        PlayerStats stats=get(player);
        int[]xpAndLevel=getXpAndLevel(stats,type);
        int currentXp =xpAndLevel[0];
        int currentLevel=xpAndLevel[1];

        if (currentLevel>=PlayerStats.MAX_LEVEL) return;

        currentXp += amount;
        int needed=PlayerStats.xpForLevel(currentLevel);

        boolean leveledUp=false;
        while(currentXp>=needed && currentLevel < PlayerStats.MAX_LEVEL){
            currentXp-=needed;
            currentLevel++;
            leveledUp=true;
            stats.skillPoints++;
            needed=PlayerStats.xpForLevel(currentLevel);
        }
        setXpAndLevel(stats,type,currentXp,currentLevel);

        if (leveledUp){
            onLevelUp(player,stats,type,currentLevel);
        }

        applyStatBounuses(player,stats);
        syncToClient(player);
    }

    private static void onLevelUp(ServerPlayerEntity player,PlayerStats stats,StatType type,int newLevel){
        String statName=type.getDisplayName();
        player.sendMessage(
                Text.literal("⬆ " + statName + " leveled up to " + newLevel + "!")
                        .formatted(Formatting.GOLD, Formatting.BOLD),
                true
        );
        player.sendMessage(
                Text.literal("You have "+stats.skillPoints+"Skill point(s) available! Press K to open Skill Tree").formatted(Formatting.YELLOW),true
        );

        player.getWorld().sendEntityStatus(player,(byte) 35);
    }
    public static void applyStatBounuses(ServerPlayerEntity player,PlayerStats stats){
        EntityAttributeInstance attackDmg=player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if(attackDmg!=null){
            double strengthBonus=(stats.strengthLevel/5.0)*0.5;
            setModifier(attackDmg,"rpgstats_strength",strengthBonus, EntityAttributeModifier.Operation.ADDITION);
        }

        EntityAttributeInstance speed =player.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if(speed!=null){
            double agilityBonus=(stats.agilityLevel/5.0)*0.1;
            setModifier(speed,"rpgstats_agility",agilityBonus,EntityAttributeModifier.Operation.ADDITION);
        }
        EntityAttributeInstance maxHp=player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH);
        if(maxHp!=null){
            double enduranceBouns=(stats.enduranceLevel/5.0)*2.0;
            setModifier(maxHp,"rpgstats_endurance",enduranceBouns,EntityAttributeModifier.Operation.ADDITION);
        }
    }
    private static void setModifier(EntityAttributeInstance instance,String name,double value,EntityAttributeModifier.Operation op){
        UUID modUUID=UUID.nameUUIDFromBytes(name.getBytes());
        var existing = instance.getModifier(modUUID);
        if (existing !=null) instance.removeModifier(modUUID);
        if (value>0){
            instance.addPersistentModifier(new net.minecraft.entity.attribute.EntityAttributeModifier(modUUID,name,value,op));
        }
    }
    public static boolean unlockSkill(ServerPlayerEntity player,StatType type,int skillBit){
        PlayerStats stats=get(player);
        if (stats.skillPoints<=0)return false;

        int requiredLevel=(skillBit+1)*10;
        if(getLevel(stats,type)<requiredLevel) return false;

        int mark=1<<skillBit;
        if((getSkillMark(stats,type) & mask)!=0) return false;
    }
}
