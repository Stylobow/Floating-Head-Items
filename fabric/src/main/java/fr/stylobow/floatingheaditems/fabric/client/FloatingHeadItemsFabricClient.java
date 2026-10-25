package fr.stylobow.floatingheaditems.fabric.client;

import com.mojang.blaze3d.platform.InputConstants;
import fr.stylobow.floatingheaditems.client.FloatingHeadItemsClient;
import fr.stylobow.floatingheaditems.client.FloatingItemLayer;
import fr.stylobow.floatingheaditems.client.FloatingItemSelectorScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.LivingEntityFeatureRendererRegistrationCallback;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import org.lwjgl.glfw.GLFW;

public class FloatingHeadItemsFabricClient implements ClientModInitializer {
    private static KeyMapping openGuiKey;

    @Override
    public void onInitializeClient() {
        FloatingHeadItemsClient.init();

        openGuiKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.floatingheaditems.open_gui",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_H,
                "key.floatingheaditems.category"
        ));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (openGuiKey.consumeClick()) {
                if (client.player != null) {
                    client.setScreen(new FloatingItemSelectorScreen());
                }
            }
        });

        LivingEntityFeatureRendererRegistrationCallback.EVENT.register((entityType, entityRenderer, registrationHelper, context) -> {
            if (entityRenderer instanceof PlayerRenderer playerRenderer) {
                registrationHelper.register(new FloatingItemLayer<>(playerRenderer));
            }
        });
    }
}
