package org.rpg;

import org.rpg.events.MobKillHandler;
import org.rpg.events.MiningXpHandler;
import org.rpg.network.RpgNetwork;
import org.rpg.skills.SkillEffectsHandler;
import org.rpg.stats.PlayerStatsManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
public class Rpg implements ModInitializer {
    public static final String MOD_ID = "rpg";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
    @Override
    public void onInitialize() {
        LOGGER.info("[RPG Stats] Initializing RPG Stats & Leveling Mod!");

        RpgNetwork.registerServerPackets();
        ServerLivingEntityEvents.AFTER_DEATH.register(MobKillHandler::onModDeath);
        MiningXpHandler.register();
        SkillEffectsHandler.register();
        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            PlayerStatsManager.syncToClient(handler.player);
        });
        ServerPlayConnectionEvents.DISCONNECT.register((handler, server) -> {
            PlayerStatsManager.remove(handler.player.getUuid());
        });
        LOGGER.info("[RPG Stats] All systems ready!");
    }
}
