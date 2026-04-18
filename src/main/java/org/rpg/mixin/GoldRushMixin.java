package org.rpg.mixin;

import org.rpg.skills.SkillEffectsHandler;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(Block.class)
public class GoldRushMixin {

    @Inject(method = "dropStacks(Lnet/minecraft/block/BlockState;Lnet/minecraft/world/World;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/entity/BlockEntity;Lnet/minecraft/entity/Entity;Lnet/minecraft/item/ItemStack;)V",
            at = @At("HEAD"))
    private static void onDropStacks(BlockState state, World world, BlockPos pos,
                                     net.minecraft.block.entity.BlockEntity blockEntity,
                                     net.minecraft.entity.Entity entity, ItemStack tool,
                                     CallbackInfo ci) {
        if (world.isClient()) return;
        if (!(entity instanceof ServerPlayerEntity player)) return;
        if (!SkillEffectsHandler.hasGoldRush(player)) return;
        if (!isValuedOre(state.getBlock())) return;

        Block.dropStacks(state, world, pos, blockEntity, null, tool);
    }

    private static boolean isValuedOre(Block block) {
        return block == Blocks.DIAMOND_ORE || block == Blocks.DEEPSLATE_DIAMOND_ORE
                || block == Blocks.GOLD_ORE || block == Blocks.DEEPSLATE_GOLD_ORE
                || block == Blocks.EMERALD_ORE || block == Blocks.DEEPSLATE_EMERALD_ORE
                || block == Blocks.NETHER_GOLD_ORE || block == Blocks.ANCIENT_DEBRIS;
    }
}