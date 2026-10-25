package fr.stylobow.floatingheaditems.registry;

import net.minecraft.world.item.ItemStack;

public class PlayerCosmeticData {
    private final ItemStack itemStack;
    private final float rotationSpeed;
    private final float scale;
    private final float height;
    private final boolean bobbing;

    public PlayerCosmeticData(ItemStack itemStack, float rotationSpeed, float scale, float height, boolean bobbing) {
        this.itemStack = itemStack == null ? ItemStack.EMPTY : itemStack;
        this.rotationSpeed = rotationSpeed;
        this.scale = scale;
        this.height = height;
        this.bobbing = bobbing;
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
