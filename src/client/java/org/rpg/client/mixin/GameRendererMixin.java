package org.rpg.client.mixin;

import net.minecraft.client.gui.DrawContext;
import org.rpg.client.ui.RpgHudRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.gui.hud.InGameHud.class)
public class GameRendererMixin {

    @Inject(method = "render", at = @At("TAIL"))
    private void onRender(DrawContext context, float tickDelta, CallbackInfo ci) {
        RpgHudRenderer.render(context);
    }
}