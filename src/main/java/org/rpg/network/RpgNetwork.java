package org.rpg.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import org.rpg.Rpg;
import org.rpg.stats.ClientStatsCache;
import org.rpg.stats.PlayerStats;
import org.rpg.stats.PlayerStatsManager;
import org.rpg.stats.StatType;


public class RpgNetwork {
    public static final Identifier SYNC_STATS_ID=
            new Identifier(Rpg.MOD_ID,"sync_stats");
    public static final Identifier UNLOCK_SKILL_ID=
            new Identifier(Rpg.MOD_ID,"unlock_skill");

    public static void registerServerPackets() {
        ServerPlayNetworking.registerGlobalReceiver(UNLOCK_SKILL_ID, (server, player, handler, buf, sender) -> {
            StatType stat = StatType.fromByte(buf.readByte());
            int skillBit = buf.readInt();

            server.execute(() -> PlayerStatsManager.unlockSkill(player, stat, skillBit));
        });
    }
    public static void registerClientPackets() {
        ClientPlayNetworking.registerGlobalReceiver(SYNC_STATS_ID, (client, handler, buf, sender) -> {
            int sLvl = buf.readInt(); int sXp = buf.readInt();
            int aLvl = buf.readInt(); int aXp = buf.readInt();
            int eLvl = buf.readInt(); int eXp = buf.readInt();
            int mLvl = buf.readInt(); int mXp = buf.readInt();
            int mgLvl = buf.readInt(); int mgXp = buf.readInt();
            int skillPoints = buf.readInt();
            int sSkills = buf.readInt(); int aSkills = buf.readInt();
            int eSkills = buf.readInt(); int mSkills = buf.readInt(); int mgSkills = buf.readInt();

            client.execute(() -> {
                PlayerStats s = ClientStatsCache.getStats();
                s.strengthLevel  = sLvl;  s.strengthXp  = sXp;
                s.agilityLevel   = aLvl;  s.agilityXp   = aXp;
                s.enduranceLevel = eLvl;  s.enduranceXp = eXp;
                s.miningLevel    = mLvl;  s.miningXp    = mXp;
                s.magicLevel     = mgLvl; s.magicXp     = mgXp;
                s.skillPoints    = skillPoints;
                s.strengthSkills  = sSkills;
                s.agilitySkills   = aSkills;
                s.enduranceSkills = eSkills;
                s.miningSkills    = mSkills;
                s.magicSkills     = mgSkills;
            });
        });
    }
    public static void sendStatsToClient(ServerPlayerEntity player,PlayerStats s){
        PacketByteBuf buf =PacketByteBufs.create();
        buf.writeInt(s.strengthLevel);  buf.writeInt(s.strengthXp);
        buf.writeInt(s.agilityLevel);   buf.writeInt(s.agilityXp);
        buf.writeInt(s.enduranceLevel); buf.writeInt(s.enduranceXp);
        buf.writeInt(s.miningLevel);    buf.writeInt(s.miningXp);
        buf.writeInt(s.magicLevel);     buf.writeInt(s.magicXp);
        buf.writeInt(s.skillPoints);
        buf.writeInt(s.strengthSkills);
        buf.writeInt(s.agilitySkills);
        buf.writeInt(s.enduranceSkills);
        buf.writeInt(s.miningSkills);
        buf.writeInt(s.magicSkills);

        ServerPlayNetworking.send(player,SYNC_STATS_ID,buf);
    }
    public static void sendUnlockSkill(StatType stat,int skillBit){
        PacketByteBuf buf=PacketByteBufs.create();
        buf.writeByte(stat.toByte());
        buf.writeInt(skillBit);
        ClientPlayNetworking.send(UNLOCK_SKILL_ID,buf);
    }
}
