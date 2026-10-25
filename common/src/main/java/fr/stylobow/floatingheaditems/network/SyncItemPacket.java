package fr.stylobow.floatingheaditems.network;

import dev.architectury.networking.NetworkManager;
import fr.stylobow.floatingheaditems.FloatingItemMod;
import fr.stylobow.floatingheaditems.registry.ModData;
import fr.stylobow.floatingheaditems.registry.PlayerCosmeticData;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import java.util.UUID;
import java.util.function.Supplier;

public class SyncItemPacket {
    private final UUID playerUuid;
    private final ItemStack itemStack;
    private final float rotationSpeed;
    private final float scale;
    private final float height;
    private final boolean bobbing;

    public SyncItemPacket(UUID playerUuid, ItemStack itemStack, float rotationSpeed, float scale, float height, boolean bobbing) {
        this.playerUuid = playerUuid;
        this.itemStack = itemStack == null ? ItemStack.EMPTY : itemStack;
        this.rotationSpeed = rotationSpeed;
        this.scale = scale;
        this.height = height;
        this.bobbing = bobbing;
    }

    public SyncItemPacket(FriendlyByteBuf buf) {
        this.playerUuid = buf.readUUID();
        this.itemStack = buf.readItem();
        this.rotationSpeed = buf.readFloat();
        this.scale = buf.readFloat();
        this.height = buf.readFloat();
        this.bobbing = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(this.playerUuid);
        buf.writeItem(this.itemStack);
        buf.writeFloat(this.rotationSpeed);
        buf.writeFloat(this.scale);
        buf.writeFloat(this.height);
        buf.writeBoolean(this.bobbing);
    }

    public void handle(Supplier<NetworkManager.PacketContext> contextSupplier) {
        NetworkManager.PacketContext context = contextSupplier.get();
        context.queue(() -> {
            Player player = context.getPlayer();
            PlayerCosmeticData data = new PlayerCosmeticData(this.itemStack, this.rotationSpeed, this.scale, this.height, this.bobbing);
            if (player instanceof ServerPlayer serverPlayer) {
                UUID uuid = serverPlayer.getUUID();
                ModData.setCosmetic(uuid, data);
                SyncItemPacket packet = new SyncItemPacket(uuid, this.itemStack, this.rotationSpeed, this.scale, this.height, this.bobbing);
                if (serverPlayer.getServer() != null) {
                    FloatingItemMod.CHANNEL.sendToPlayers(serverPlayer.getServer().getPlayerList().getPlayers(), packet);
                }
            } else {
                ModData.setCosmetic(this.playerUuid, data);
            }
        });
    }

    public UUID getPlayerUuid() {
        return this.playerUuid;
    }

    public ItemStack getItemStack() {
        return this.itemStack;
    }

    public float getRotationSpeed() {
        return this.rotationSpeed;
    }

    public float getScale() {
        return this.scale;
    }

    public float getHeight() {
        return this.height;
    }

    public boolean isBobbing() {
        return this.bobbing;
    }
}
