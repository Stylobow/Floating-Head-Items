package fr.stylobow.floatingheaditems.forge.client;

import com.mojang.blaze3d.platform.InputConstants;
import fr.stylobow.floatingheaditems.FloatingItemMod;
import fr.stylobow.floatingheaditems.client.FloatingHeadItemsClient;
import fr.stylobow.floatingheaditems.client.FloatingItemLayer;
import fr.stylobow.floatingheaditems.client.FloatingItemSelectorScreen;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(modid = FloatingItemMod.MOD_ID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FloatingHeadItemsForgeClient {
    private static final KeyMapping OPEN_GUI_KEY = new KeyMapping(
            "key.floatingheaditems.open_gui",
            InputConstants.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "key.floatingheaditems.category"
    );

    public static void init() {
        FloatingHeadItemsClient.init();
        MinecraftForge.EVENT_BUS.addListener(FloatingHeadItemsForgeClient::onClientTick);
    }

    @SubscribeEvent
    public static void registerKeyMappings(RegisterKeyMappingsEvent event) {
        event.register(OPEN_GUI_KEY);
    }

    @SubscribeEvent
    public static void registerLayers(EntityRenderersEvent.AddLayers event) {
        for (String skin : event.getSkins()) {
            LivingEntityRenderer<AbstractClientPlayer, PlayerModel<AbstractClientPlayer>> renderer = event.getSkin(skin);
            if (renderer != null) {
                renderer.addLayer(new FloatingItemLayer<>(renderer));
            }
        }
    }

    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            while (OPEN_GUI_KEY.consumeClick()) {
                Minecraft.getInstance().setScreen(new FloatingItemSelectorScreen());
            }
        }
    }
}
