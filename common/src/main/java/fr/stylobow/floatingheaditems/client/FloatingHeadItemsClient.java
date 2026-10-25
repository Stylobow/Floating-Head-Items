package fr.stylobow.floatingheaditems.client;

import dev.architectury.event.events.client.ClientPlayerEvent;
import fr.stylobow.floatingheaditems.FloatingItemMod;
import fr.stylobow.floatingheaditems.config.ModConfig;
import fr.stylobow.floatingheaditems.network.SyncItemPacket;
import fr.stylobow.floatingheaditems.registry.ModData;
import fr.stylobow.floatingheaditems.registry.PlayerCosmeticData;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public class FloatingHeadItemsClient {
    public static void init() {
        ModConfig.load();

        ClientPlayerEvent.CLIENT_PLAYER_JOIN.register(player -> {
            sendStoredCosmetic(player.getUUID());
        });

        ClientPlayerEvent.CLIENT_PLAYER_RESPAWN.register((oldPlayer, newPlayer) -> {
            sendStoredCosmetic(newPlayer.getUUID());
        });
    }

    private static void sendStoredCosmetic(UUID uuid) {
        ModConfig config = ModConfig.getInstance();
        ItemStack stack = config.getItemStack();
        if (!stack.isEmpty()) {
            ModData.setCosmetic(uuid, new PlayerCosmeticData(stack, config.rotationSpeed, config.scale, config.height, config.bobbing));
            FloatingItemMod.CHANNEL.sendToServer(new SyncItemPacket(
                    uuid,
                    stack,
                    config.rotationSpeed,
                    config.scale,
                    config.height,
                    config.bobbing
            ));
        }
    }
}
