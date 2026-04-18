package org.rpg.mixin;



import org.rpg.stats.PlayerStatsManager;
import org.rpg.stats.StatType;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import net.minecraft.block.BlockState;

@Mixin(net.minecraft.server.network.ServerPlayerInteractionManager.class)
public class LivingEntityMixin {

    @Inject(method = "processBlockBreakingAction", at = @At("TAIL"))
    private void onBlockBroken(BlockPos pos, net.minecraft.network.packet.c2s.play.PlayerActionC2SPacket.Action action, net.minecraft.util.math.Direction direction, int worldHeight, int sequence, CallbackInfo ci) {
     }
}