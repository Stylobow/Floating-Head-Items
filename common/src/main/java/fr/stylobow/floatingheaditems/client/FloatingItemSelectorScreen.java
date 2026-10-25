package fr.stylobow.floatingheaditems.client;

import fr.stylobow.floatingheaditems.FloatingItemMod;
import fr.stylobow.floatingheaditems.config.ModConfig;
import fr.stylobow.floatingheaditems.network.SyncItemPacket;
import fr.stylobow.floatingheaditems.registry.ModData;
import fr.stylobow.floatingheaditems.registry.PlayerCosmeticData;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.ArrayList;
import java.util.List;

public class FloatingItemSelectorScreen extends Screen {
    private static final int COLUMNS = 9;
    private static final int ROWS = 6;
    private static final int SLOT_SIZE = 18;

    private final List<ItemStack> allItems = new ArrayList<>();
    private final List<ItemStack> filteredItems = new ArrayList<>();

    private EditBox searchBox;
    private int scrollOffset;
    private int panelX;
    private int panelY;
    private int panelWidth;
    private int panelHeight;
    private int gridStartX;
    private int gridStartY;

    private int previewX;
    private int previewY;
    private int previewWidth;
    private int previewHeight;

    public FloatingItemSelectorScreen() {
        super(Component.translatable("gui.floatingheaditems.title"));
    }

    @Override
    protected void init() {
        allItems.clear();
        for (Item item : BuiltInRegistries.ITEM) {
            if (item != Items.AIR) {
                allItems.add(new ItemStack(item));
            }
        }

        panelWidth = 390;
        panelHeight = 186;
        panelX = (this.width - panelWidth) / 2;
        panelY = (this.height - panelHeight) / 2;
        gridStartX = panelX + 10;
        gridStartY = panelY + 44;

        previewX = panelX + 178;
        previewY = panelY + 22;
        previewWidth = 80;
        previewHeight = 146;

        searchBox = new EditBox(this.font, panelX + 10, panelY + 22, 162, 16, Component.translatable("gui.floatingheaditems.search"));
        searchBox.setResponder(this::updateFilter);
        searchBox.setHint(Component.translatable("gui.floatingheaditems.search"));
        this.addRenderableWidget(searchBox);

        int rightX = panelX + 266;
        int rightWidth = 114;

        double initialSpeed = Mth.clamp(ModConfig.getInstance().rotationSpeed / 3.0, 0.0, 1.0);
        this.addRenderableWidget(new FloatingItemSlider(
                rightX, panelY + 22, rightWidth, 18, initialSpeed,
                val -> Component.translatable("gui.floatingheaditems.speed", String.format("%.1fx", val * 3.0)),
                val -> {
                    ModConfig.getInstance().rotationSpeed = (float) (val * 3.0);
                    updateLiveCosmetic();
                },
                this::sendSync
        ));

        double initialScale = Mth.clamp((ModConfig.getInstance().scale - 0.5) / 1.5, 0.0, 1.0);
        this.addRenderableWidget(new FloatingItemSlider(
                rightX, panelY + 43, rightWidth, 18, initialScale,
                val -> Component.translatable("gui.floatingheaditems.size", String.format("%.2fx", 0.5 + val * 1.5)),
                val -> {
                    ModConfig.getInstance().scale = (float) (0.5 + val * 1.5);
                    updateLiveCosmetic();
                },
                this::sendSync
        ));

        double initialHeight = Mth.clamp((ModConfig.getInstance().height - 0.5) / 0.75, 0.0, 1.0);
        this.addRenderableWidget(new FloatingItemSlider(
                rightX, panelY + 64, rightWidth, 18, initialHeight,
                val -> Component.translatable("gui.floatingheaditems.height", String.format("%.2f", 0.5 + val * 0.75)),
                val -> {
                    ModConfig.getInstance().height = (float) (0.5 + val * 0.75);
                    updateLiveCosmetic();
                },
                this::sendSync
        ));

        this.addRenderableWidget(Button.builder(
                Component.translatable(ModConfig.getInstance().bobbing ? "gui.floatingheaditems.bobbing_on" : "gui.floatingheaditems.bobbing_off"),
                btn -> {
                    ModConfig.getInstance().bobbing = !ModConfig.getInstance().bobbing;
                    btn.setMessage(Component.translatable(ModConfig.getInstance().bobbing ? "gui.floatingheaditems.bobbing_on" : "gui.floatingheaditems.bobbing_off"));
                    updateLiveCosmetic();
                    sendSync();
                }
        ).bounds(rightX, panelY + 85, rightWidth, 18).build());

        this.addRenderableWidget(Button.builder(
                Component.translatable(ModConfig.getInstance().showOthers ? "gui.floatingheaditems.others_on" : "gui.floatingheaditems.others_off"),
                btn -> {
                    ModConfig.getInstance().showOthers = !ModConfig.getInstance().showOthers;
                    btn.setMessage(Component.translatable(ModConfig.getInstance().showOthers ? "gui.floatingheaditems.others_on" : "gui.floatingheaditems.others_off"));
                    ModConfig.save();
                }
        ).bounds(rightX, panelY + 106, rightWidth, 18).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.floatingheaditems.clear"), btn -> {
            ModConfig.getInstance().setItemStack(ItemStack.EMPTY);
            sendSync();
            this.onClose();
        }).bounds(rightX, panelY + 130, rightWidth, 18).build());

        this.addRenderableWidget(Button.builder(Component.translatable("gui.floatingheaditems.done"), btn -> {
            this.onClose();
        }).bounds(rightX, panelY + 150, rightWidth, 18).build());

        updateFilter(searchBox.getValue());
    }

    private void updateLiveCosmetic() {
        if (this.minecraft != null && this.minecraft.player != null) {
            ModConfig config = ModConfig.getInstance();
            ModData.setCosmetic(this.minecraft.player.getUUID(), new PlayerCosmeticData(
                    config.getItemStack(),
                    config.rotationSpeed,
                    config.scale,
                    config.height,
                    config.bobbing
            ));
        }
    }

    private void updateFilter(String query) {
        filteredItems.clear();
        String lower = query == null ? "" : query.trim().toLowerCase();
        for (ItemStack stack : allItems) {
            if (lower.isEmpty()) {
                filteredItems.add(stack);
            } else {
                ResourceLocation id = BuiltInRegistries.ITEM.getKey(stack.getItem());
                String name = stack.getHoverName().getString().toLowerCase();
                String idStr = id.toString().toLowerCase();
                String path = id.getPath().toLowerCase();
                if (name.contains(lower) || idStr.contains(lower) || path.contains(lower)) {
                    filteredItems.add(stack);
                }
            }
        }
        scrollOffset = 0;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
        int maxScroll = Math.max(0, (filteredItems.size() + COLUMNS - 1) / COLUMNS - ROWS);
        if (maxScroll > 0) {
            scrollOffset = Mth.clamp(scrollOffset - (int) Math.signum(delta), 0, maxScroll);
            return true;
        }
        return super.mouseScrolled(mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            int col = (int) (mouseX - gridStartX) / SLOT_SIZE;
            int row = (int) (mouseY - gridStartY) / SLOT_SIZE;
            if (mouseX >= gridStartX && mouseX < gridStartX + COLUMNS * SLOT_SIZE
                    && mouseY >= gridStartY && mouseY < gridStartY + ROWS * SLOT_SIZE
                    && col >= 0 && col < COLUMNS && row >= 0 && row < ROWS) {
                int index = (scrollOffset + row) * COLUMNS + col;
                if (index >= 0 && index < filteredItems.size()) {
                    ModConfig.getInstance().setItemStack(filteredItems.get(index));
                    sendSync();
                    this.onClose();
                    return true;
                }
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    private void sendSync() {
        ModConfig.save();
        if (this.minecraft != null && this.minecraft.player != null) {
            ModConfig config = ModConfig.getInstance();
            PlayerCosmeticData data = new PlayerCosmeticData(
                    config.getItemStack(),
                    config.rotationSpeed,
                    config.scale,
                    config.height,
                    config.bobbing
            );
            ModData.setCosmetic(this.minecraft.player.getUUID(), data);
            FloatingItemMod.CHANNEL.sendToServer(new SyncItemPacket(
                    this.minecraft.player.getUUID(),
                    config.getItemStack(),
                    config.rotationSpeed,
                    config.scale,
                    config.height,
                    config.bobbing
            ));
        }
    }

    @Override
    public void onClose() {
        sendSync();
        super.onClose();
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);

        guiGraphics.fill(panelX, panelY, panelX + panelWidth, panelY + panelHeight, 0xD0101010);
        guiGraphics.fill(panelX, panelY, panelX + panelWidth, panelY + 1, 0xFF404040);
        guiGraphics.fill(panelX, panelY + panelHeight - 1, panelX + panelWidth, panelY + panelHeight, 0xFF404040);
        guiGraphics.fill(panelX, panelY, panelX + 1, panelY + panelHeight, 0xFF404040);
        guiGraphics.fill(panelX + panelWidth - 1, panelY, panelX + panelWidth, panelY + panelHeight, 0xFF404040);

        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, panelY + 8, 0xFFFFFF);

        guiGraphics.fill(previewX, previewY, previewX + previewWidth, previewY + previewHeight, 0x50000000);
        guiGraphics.fill(previewX, previewY, previewX + previewWidth, previewY + 1, 0xFF404040);
        guiGraphics.fill(previewX, previewY + previewHeight - 1, previewX + previewWidth, previewY + previewHeight, 0xFF404040);
        guiGraphics.fill(previewX, previewY, previewX + 1, previewY + previewHeight, 0xFF404040);
        guiGraphics.fill(previewX + previewWidth - 1, previewY, previewX + previewWidth, previewY + previewHeight, 0xFF404040);

        if (this.minecraft != null && this.minecraft.player != null) {
            float lookX = (float) (previewX + previewWidth / 2) - mouseX;
            float lookY = (float) (previewY + 45) - mouseY;
            InventoryScreen.renderEntityInInventoryFollowsMouse(
                    guiGraphics,
                    previewX + previewWidth / 2,
                    previewY + 132,
                    42,
                    lookX,
                    lookY,
                    this.minecraft.player
            );
        }

        super.render(guiGraphics, mouseX, mouseY, partialTick);

        ItemStack hovered = null;
        for (int r = 0; r < ROWS; r++) {
            for (int c = 0; c < COLUMNS; c++) {
                int x = gridStartX + c * SLOT_SIZE;
                int y = gridStartY + r * SLOT_SIZE;
                int index = (scrollOffset + r) * COLUMNS + c;

                guiGraphics.fill(x, y, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0x40000000);

                if (index < filteredItems.size()) {
                    ItemStack stack = filteredItems.get(index);
                    guiGraphics.renderItem(stack, x + 1, y + 1);

                    if (mouseX >= x && mouseX < x + SLOT_SIZE && mouseY >= y && mouseY < y + SLOT_SIZE) {
                        guiGraphics.fill(x, y, x + SLOT_SIZE - 1, y + SLOT_SIZE - 1, 0x80FFFFFF);
                        hovered = stack;
                    }
                }
            }
        }

        if (hovered != null) {
            guiGraphics.renderTooltip(this.font, hovered, mouseX, mouseY);
        }
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
