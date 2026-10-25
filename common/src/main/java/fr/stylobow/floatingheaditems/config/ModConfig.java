package fr.stylobow.floatingheaditems.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.architectury.platform.Platform;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;

public class ModConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path CONFIG_PATH = Platform.getConfigFolder().resolve("floatingheaditems.json");

    public String itemId = "";
    public float rotationSpeed = 1.0F;
    public float scale = 1.0F;
    public float height = 1.0F;
    public boolean bobbing = true;
    public boolean showOthers = true;

    private static ModConfig INSTANCE = new ModConfig();

    public static ModConfig getInstance() {
        return INSTANCE;
    }

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try (Reader reader = Files.newBufferedReader(CONFIG_PATH)) {
                ModConfig config = GSON.fromJson(reader, ModConfig.class);
                if (config != null) {
                    INSTANCE = config;
                }
            } catch (Exception ignored) {
            }
        }
    }

    public static void save() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            try (Writer writer = Files.newBufferedWriter(CONFIG_PATH)) {
                GSON.toJson(INSTANCE, writer);
            }
        } catch (Exception ignored) {
        }
    }

    public ItemStack getItemStack() {
        if (itemId == null || itemId.isEmpty()) {
            return ItemStack.EMPTY;
        }
        ResourceLocation id = ResourceLocation.tryParse(itemId);
        if (id == null) {
            return ItemStack.EMPTY;
        }
        Item item = BuiltInRegistries.ITEM.get(id);
        if (item == Items.AIR) {
            return ItemStack.EMPTY;
        }
        return new ItemStack(item);
    }

    public void setItemStack(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            this.itemId = "";
        } else {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
            this.itemId = id != null ? id.toString() : "";
        }
    }
}
