package org.rpg.skills;

import net.fabricmc.fabric.api.entity.event.v1.ServerLivingEntityEvents;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import org.rpg.stats.PlayerStats;
import org.rpg.stats.PlayerStatsManager;

import java.util.*;

public class SkillEffectsHandler {

    // ── Cooldown maps ─────────────────────────────────────────────
    private static final Map<UUID, Long> immortalCooldown   = new HashMap<>();
    private static final Map<UUID, Long> spellBindCooldown  = new HashMap<>();
    private static final long IMMORTAL_COOLDOWN_TICKS   = 6000L; // 5 minutes
    private static final long SPELLBIND_COOLDOWN_TICKS  = 200L;  // 10 seconds

    private static final Set<BlockPos> veinMinerVisited = new HashSet<>();


    private static final Set<UUID> jumpingPlayers = new HashSet<>();

    public static void markJump(ServerPlayerEntity player) {
        jumpingPlayers.add(player.getUuid());
    }

   public static void register() {
        registerPassiveEffectTick();
        registerImmortality();
        registerVeinMiner();
  }
  private static void registerPassiveEffectTick() {
        net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents.END_SERVER_TICK.register(server -> {
            for (var player : server.getPlayerManager().getPlayerList()) {
                PlayerStats stats = PlayerStatsManager.get(player);
                applyPassiveEffects(player, stats);
                checkProspector(player, stats);
                checkSpellBind(player, stats);
            }
            jumpingPlayers.clear();
        });
    }

    private static void applyPassiveEffects(ServerPlayerEntity player, PlayerStats stats) {
        // ── STRENGTH ─────────────────────────────────────────────
        // Titan: Strength II status effect as a passive visual indicator
        if (hasSkill(stats.strengthSkills, 2)) {
            ensureEffect(player, StatusEffects.STRENGTH, 1); // amplifier 1 = Strength II
        }

        // ── AGILITY ──────────────────────────────────────────────
        // Swift Feet: Speed I
        if (hasSkill(stats.agilitySkills, 0)) {
            ensureEffect(player, StatusEffects.SPEED, 0);
        }
        // Phantom: Jump Boost I
        if (hasSkill(stats.agilitySkills, 2)) {
            ensureEffect(player, StatusEffects.JUMP_BOOST, 0);
        }

        // ── ENDURANCE ────────────────────────────────────────────
        // Iron Skin: Resistance I
        if (hasSkill(stats.enduranceSkills, 0)) {
            ensureEffect(player, StatusEffects.RESISTANCE, 0);
        }
        // Regeneration: Regeneration I
        if (hasSkill(stats.enduranceSkills, 1)) {
            ensureEffect(player, StatusEffects.REGENERATION, 0);
        }
    }

    private static void ensureEffect(ServerPlayerEntity player,
                                     net.minecraft.entity.effect.StatusEffect effect,
                                     int amplifier) {
        StatusEffectInstance existing = player.getStatusEffect(effect);
   if (existing == null || existing.getDuration() < 100) {
            player.addStatusEffect(
                    new StatusEffectInstance(effect, 300, amplifier,
                            true,
                            false,
                            false)
            );
        }
    }

  private static void checkProspector(ServerPlayerEntity player, PlayerStats stats) {
        if (!hasSkill(stats.miningSkills, 1)) return;
        BlockPos pos = player.getBlockPos();
        boolean underground = pos.getY() < 60 || !player.getWorld().isSkyVisible(pos);
        if (underground) {
            ensureEffect(player, StatusEffects.NIGHT_VISION, 0);
        }
    }

  private static void registerImmortality() {
        ServerLivingEntityEvents.ALLOW_DAMAGE.register((entity, source, amount) -> {
            if (!(entity instanceof ServerPlayerEntity player)) return true;
            PlayerStats stats = PlayerStatsManager.get(player);
            if (!hasSkill(stats.enduranceSkills, 2)) return true;

            if (player.getHealth() - amount <= 0) {
                long now     = player.getWorld().getTime();
                long lastUse = immortalCooldown.getOrDefault(
                        player.getUuid(), -IMMORTAL_COOLDOWN_TICKS);
                if (now - lastUse >= IMMORTAL_COOLDOWN_TICKS) {
                    player.setHealth(1.0f);
                    player.getWorld().sendEntityStatus(player, (byte) 35); // totem effect
                    immortalCooldown.put(player.getUuid(), now);
                    player.sendMessage(
                            Text.literal("☠ Immortal saved your life! (5min cooldown)")
                                    .formatted(Formatting.LIGHT_PURPLE, Formatting.BOLD),
                            true
                    );
                    return false;
                }
            }
            return true;
        });
    }
 private static void registerVeinMiner() {
        PlayerBlockBreakEvents.AFTER.register((world, player, pos, state, blockEntity) -> {
            if (world.isClient()) return;
            if (!(player instanceof ServerPlayerEntity serverPlayer)) return;

            PlayerStats stats = PlayerStatsManager.get(serverPlayer);
            if (!hasSkill(stats.miningSkills, 0)) return;
            if (!isOre(state.getBlock())) return;
            if (!(serverPlayer.getMainHandStack().getItem()
                    instanceof net.minecraft.item.PickaxeItem)) return;

            veinMinerVisited.clear();
            veinMinerVisited.add(pos);
            breakVein((ServerWorld) world, serverPlayer, pos, state.getBlock(), 0);
        });
    }

    private static void breakVein(ServerWorld world, ServerPlayerEntity player,
                                  BlockPos center, Block targetBlock, int depth) {
        if (depth > 12) return;
        for (int dx = -1; dx <= 1; dx++) {
            for (int dy = -1; dy <= 1; dy++) {
                for (int dz = -1; dz <= 1; dz++) {
                    if (dx == 0 && dy == 0 && dz == 0) continue;
                    BlockPos neighbor = center.add(dx, dy, dz);
                    if (veinMinerVisited.contains(neighbor)) continue;
                    if (world.getBlockState(neighbor).getBlock() != targetBlock) continue;

                    veinMinerVisited.add(neighbor);
                    world.breakBlock(neighbor, true, player);
                    breakVein(world, player, neighbor, targetBlock, depth + 1);
                }
            }
        }
    }

    private static boolean isOre(Block block) {
        return block == Blocks.COAL_ORE        || block == Blocks.DEEPSLATE_COAL_ORE
                || block == Blocks.IRON_ORE        || block == Blocks.DEEPSLATE_IRON_ORE
                || block == Blocks.COPPER_ORE      || block == Blocks.DEEPSLATE_COPPER_ORE
                || block == Blocks.GOLD_ORE        || block == Blocks.DEEPSLATE_GOLD_ORE
                || block == Blocks.REDSTONE_ORE    || block == Blocks.DEEPSLATE_REDSTONE_ORE
                || block == Blocks.LAPIS_ORE       || block == Blocks.DEEPSLATE_LAPIS_ORE
                || block == Blocks.DIAMOND_ORE     || block == Blocks.DEEPSLATE_DIAMOND_ORE
                || block == Blocks.EMERALD_ORE     || block == Blocks.DEEPSLATE_EMERALD_ORE
                || block == Blocks.NETHER_GOLD_ORE || block == Blocks.NETHER_QUARTZ_ORE
                || block == Blocks.ANCIENT_DEBRIS;
    }

    private static void checkSpellBind(ServerPlayerEntity player, PlayerStats stats) {
        if (!hasSkill(stats.magicSkills, 2)) return;

        boolean isSneakJumping = player.isSneaking()
                && jumpingPlayers.contains(player.getUuid());
        if (!isSneakJumping) return;

        long now     = player.getWorld().getTime();
        long lastUse = spellBindCooldown.getOrDefault(
                player.getUuid(), -SPELLBIND_COOLDOWN_TICKS);
        if (now - lastUse < SPELLBIND_COOLDOWN_TICKS) return;

        var look = player.getRotationVec(1.0f).multiply(8.0);
        double tx = player.getX() + look.x;
        double ty = Math.max(player.getY() + look.y, player.getWorld().getBottomY() + 1);
        double tz = player.getZ() + look.z;

        player.teleport(tx, ty, tz);
        player.getWorld().sendEntityStatus(player, (byte) 46); // ender pearl effect
        spellBindCooldown.put(player.getUuid(), now);
        player.sendMessage(
                Text.literal("✦ Spell Bind! (10s cooldown)").formatted(Formatting.AQUA),
                true
        );
    }
 public static float getDamageMultiplier(ServerPlayerEntity player) {
        PlayerStats stats = PlayerStatsManager.get(player);
        float multiplier = 1.0f;

        if (hasSkill(stats.strengthSkills, 0)) multiplier += 0.20f; // Power Strike
        if (hasSkill(stats.strengthSkills, 1)) {                    // Berserker Rage
            float hpPct = player.getHealth() / player.getMaxHealth();
            if (hpPct < 0.30f) multiplier += 0.40f;
        }
        return multiplier;
    }

    public static boolean tryEvasion(ServerPlayerEntity player) {
        PlayerStats stats = PlayerStatsManager.get(player);
        if (!hasSkill(stats.agilitySkills, 1)) return false;
        if (Math.random() < 0.10) {
            player.sendMessage(Text.literal("⚡ Evaded!").formatted(Formatting.YELLOW), true);
            return true;
        }
        return false;
    }


    public static int applyArcaneBoost(ServerPlayerEntity player, int baseXp) {
        PlayerStats stats = PlayerStatsManager.get(player);
        if (hasSkill(stats.magicSkills, 0)) return (int)(baseXp * 1.30f);
        return baseXp;
    }

    public static boolean hasGoldRush(ServerPlayerEntity player) {
        PlayerStats stats = PlayerStatsManager.get(player);
        return hasSkill(stats.miningSkills, 2);
    }

    public static boolean hasSkill(int skillMask, int bit) {
        return (skillMask & (1 << bit)) != 0;
    }
}