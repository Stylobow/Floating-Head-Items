package fr.stylobow.floatingheaditems.registry;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ModData {
    private static final Map<UUID, PlayerCosmeticData> PLAYER_COSMETICS = new ConcurrentHashMap<>();

    public static void setCosmetic(UUID uuid, PlayerCosmeticData data) {
        if (data == null || data.getItemStack().isEmpty()) {
            PLAYER_COSMETICS.remove(uuid);
        } else {
            PLAYER_COSMETICS.put(uuid, data);
        }
    }

    public static PlayerCosmeticData getCosmetic(UUID uuid) {
        return PLAYER_COSMETICS.get(uuid);
    }

    public static void removeCosmetic(UUID uuid) {
        PLAYER_COSMETICS.remove(uuid);
    }

    public static Map<UUID, PlayerCosmeticData> getAll() {
        return Collections.unmodifiableMap(PLAYER_COSMETICS);
    }
}
