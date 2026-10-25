package fr.stylobow.floatingheaditems;

import dev.architectury.event.events.common.PlayerEvent;
import dev.architectury.networking.NetworkChannel;
import fr.stylobow.floatingheaditems.network.SyncItemPacket;
import fr.stylobow.floatingheaditems.registry.ModData;
import net.minecraft.resources.ResourceLocation;

public class FloatingItemMod {
    public static final String MOD_ID = "floatingheaditems";
    public static final ResourceLocation CHANNEL_NAME = new ResourceLocation(MOD_ID, "main");
    public static final NetworkChannel CHANNEL = NetworkChannel.create(CHANNEL_NAME);

    public static void init() {
        CHANNEL.register(SyncItemPacket.class,
                SyncItemPacket::encode,
                SyncItemPacket::new,
                SyncItemPacket::handle);

        PlayerEvent.PLAYER_JOIN.register(serverPlayer -> {
            ModData.getAll().forEach((uuid, data) -> {
                CHANNEL.sendToPlayer(serverPlayer, new SyncItemPacket(uuid, data.getItemStack(), data.getRotationSpeed(), data.getScale(), data.getHeight(), data.isBobbing()));
            });
        });
    }
}
