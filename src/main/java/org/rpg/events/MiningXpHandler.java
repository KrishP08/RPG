package org.rpg.events;

import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.rpg.stats.StatType;
import org.rpg.stats.PlayerStatsManager;

public class MiningXpHandler {
    public static void register(){
        PlayerBlockBreakEvents.AFTER.register(MiningXpHandler::onBlockBreak);
    }
    private static void onBlockBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, BlockEntity blockEntity){
        if (world.isClient()) return;
        if (!(player instanceof ServerPlayerEntity serverPlayer)) return;

        int xp=getMiningXp(state.getBlock());
        if (xp>0){
            PlayerStatsManager.addXp(serverPlayer,StatType.MINING,xp);
        }
    }
    private static int getMiningXp(Block block){
        // Ancient debris & netherite
        if (block == Blocks.ANCIENT_DEBRIS)        return 80;

        // Diamond & Emerald
        if (block == Blocks.DIAMOND_ORE
                || block == Blocks.DEEPSLATE_DIAMOND_ORE) return 50;
        if (block == Blocks.EMERALD_ORE
                || block == Blocks.DEEPSLATE_EMERALD_ORE) return 45;

        // Gold
        if (block == Blocks.GOLD_ORE
                || block == Blocks.DEEPSLATE_GOLD_ORE
                || block == Blocks.NETHER_GOLD_ORE)       return 30;

        // Redstone & Lapis
        if (block == Blocks.REDSTONE_ORE
                || block == Blocks.DEEPSLATE_REDSTONE_ORE) return 20;
        if (block == Blocks.LAPIS_ORE
                || block == Blocks.DEEPSLATE_LAPIS_ORE)    return 20;

        // Iron & Copper
        if (block == Blocks.IRON_ORE
                || block == Blocks.DEEPSLATE_IRON_ORE)     return 15;
        if (block == Blocks.COPPER_ORE
                || block == Blocks.DEEPSLATE_COPPER_ORE)   return 10;

        // Coal
        if (block == Blocks.COAL_ORE
                || block == Blocks.DEEPSLATE_COAL_ORE)     return 8;

        // Quartz (Nether)
        if (block == Blocks.NETHER_QUARTZ_ORE)      return 10;

        // Stone & Deepslate (tiny XP for bulk mining)
        if (block == Blocks.STONE
                || block == Blocks.COBBLESTONE)             return 1;
        if (block == Blocks.DEEPSLATE
                || block == Blocks.COBBLED_DEEPSLATE)       return 2;
        if (block == Blocks.OBSIDIAN)                return 5;
            return 0;
    }
}
