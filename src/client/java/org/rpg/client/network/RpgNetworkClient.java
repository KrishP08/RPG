package org.rpg.client.network;

import org.rpg.network.RpgNetwork;
import org.rpg.stats.ClientStatsCache;
import org.rpg.stats.PlayerStats;
import org.rpg.stats.StatType;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.PacketByteBuf;

public class RpgNetworkClient {

    // ── Client: receive stats sync from server ────────────────────
    public static void registerClientPackets() {
        ClientPlayNetworking.registerGlobalReceiver(RpgNetwork.SYNC_STATS_ID,
                (client, handler, buf, sender) -> {
                    int sLvl  = buf.readInt(); int sXp  = buf.readInt();
                    int aLvl  = buf.readInt(); int aXp  = buf.readInt();
                    int eLvl  = buf.readInt(); int eXp  = buf.readInt();
                    int mLvl  = buf.readInt(); int mXp  = buf.readInt();
                    int mgLvl = buf.readInt(); int mgXp = buf.readInt();
                    int skillPoints  = buf.readInt();
                    int sSkills  = buf.readInt();
                    int aSkills  = buf.readInt();
                    int eSkills  = buf.readInt();
                    int mSkills  = buf.readInt();
                    int mgSkills = buf.readInt();

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

    // ── Client: send skill unlock request to server ───────────────
    public static void sendUnlockSkill(StatType stat, int skillBit) {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeByte(stat.toByte());
        buf.writeInt(skillBit);
        ClientPlayNetworking.send(RpgNetwork.UNLOCK_SKILL_ID, buf);
    }
}