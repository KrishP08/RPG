package org.rpg.client;

import org.rpg.client.network.RpgNetworkClient;
import org.rpg.client.ui.SkillTreeScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class RpgClient implements ClientModInitializer {

    public static KeyBinding openSkillTreeKey;

    @Override
    public void onInitializeClient() {
        // Register client network receivers
        RpgNetworkClient.registerClientPackets();

        // Register K keybind
        openSkillTreeKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.rpgstats.open_skill_tree",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_K,
                "category.rpgstats"
        ));

        // Open skill tree on key press
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openSkillTreeKey.wasPressed()) {
                if (client.player != null) {
                    MinecraftClient.getInstance().setScreen(new SkillTreeScreen());
                }
            }
        });
    }
}