package fr.stylobow.floatingheaditems.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fr.stylobow.floatingheaditems.config.ModConfig;
import fr.stylobow.floatingheaditems.registry.ModData;
import fr.stylobow.floatingheaditems.registry.PlayerCosmeticData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class FloatingItemLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    public FloatingItemLayer(RenderLayerParent<T, M> renderer) {
        super(renderer);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, T entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        if (entity.isInvisible()) {
            return;
        }
        Minecraft mc = Minecraft.getInstance();
        if (!ModConfig.getInstance().showOthers && mc.player != null && !entity.getUUID().equals(mc.player.getUUID())) {
            return;
        }
        PlayerCosmeticData cosmetic = ModData.getCosmetic(entity.getUUID());
        if (cosmetic == null) {
            return;
        }
        ItemStack stack = cosmetic.getItemStack();
        if (stack == null || stack.isEmpty()) {
            return;
        }

        poseStack.pushPose();

        float bob = cosmetic.isBobbing() ? (float) Math.sin((entity.tickCount + partialTick) * 0.1F) * 0.05F : 0.0F;
        double crouchOffset = entity.isCrouching() ? 0.25D : 0.0D;
        poseStack.translate(0.0D, -cosmetic.getHeight() + crouchOffset + bob, 0.0D);

        float rotation = (entity.tickCount + partialTick) * 3.0F * cosmetic.getRotationSpeed();
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.mulPose(Axis.ZP.rotationDegrees(180.0F));

        float scale = cosmetic.getScale();
        poseStack.scale(scale, scale, scale);

        Minecraft.getInstance().getItemRenderer().renderStatic(entity, stack, ItemDisplayContext.GROUND, false, poseStack, buffer, entity.level(), packedLight, OverlayTexture.NO_OVERLAY, entity.getId());
        poseStack.popPose();
    }
}
