package dev.tr7zw.animatedfirstperson.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;

import dev.tr7zw.animatedfirstperson.AnimatedFirstPersonShared;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

@Mixin(ItemInHandRenderer.class)
public class HeldItemRendererMixin {

    @Shadow
    private Minecraft minecraft;
    @Shadow
    private ItemStack mainHandItem = ItemStack.EMPTY;
    @Shadow
    private ItemStack offHandItem = ItemStack.EMPTY;
    @Shadow
    private EntityRenderDispatcher entityRenderDispatcher;

    @Inject(at = @At("HEAD"), method = "renderArmWithItem", cancellable = true)
    public void renderFirstPersonItem(AbstractClientPlayer abstractClientPlayer, float tickDelta, float pitch,
            InteractionHand interactionHand, float swingProgress, ItemStack itemStack, float equipProgress,
            PoseStack poseStack, MultiBufferSource multiBufferSource, int light, CallbackInfo info) {
        if (abstractClientPlayer.isScoping())
            return;
        boolean bl = (interactionHand == InteractionHand.MAIN_HAND);
        HumanoidArm humanoidArm = bl ? abstractClientPlayer.getMainArm()
                : abstractClientPlayer.getMainArm().getOpposite();
        poseStack.pushPose();
        if (itemStack.isEmpty()) {
            if (bl && !abstractClientPlayer.isInvisible())
                renderPlayerArm(poseStack, multiBufferSource, light, equipProgress, swingProgress, humanoidArm);
        } else if (itemStack.is(Items.FILLED_MAP)) {
            if (bl && this.offHandItem.isEmpty()) {
                renderTwoHandedMap(poseStack, multiBufferSource, light, pitch, equipProgress, swingProgress);
            } else {
                renderOneHandedMap(poseStack, multiBufferSource, light, equipProgress, humanoidArm, swingProgress,
                        itemStack);
            }
        } else if (itemStack.is(Items.CROSSBOW)) {
            boolean bl2 = CrossbowItem.isCharged(itemStack);
            boolean bl3 = (humanoidArm == HumanoidArm.RIGHT);
            int k = bl3 ? 1 : -1;
            if (abstractClientPlayer.isUsingItem() && abstractClientPlayer.getUseItemRemainingTicks() > 0
                    && abstractClientPlayer.getUsedItemHand() == interactionHand) {
                applyItemArmTransform(poseStack, humanoidArm, equipProgress);
                poseStack.translate((k * -0.4785682F), -0.0943870022892952D, 0.05731530860066414D);
                poseStack.mulPose(Vector3f.XP.rotationDegrees(-11.935F));
                poseStack.mulPose(Vector3f.YP.rotationDegrees(k * 65.3F));
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(k * -9.785F));
                float l = itemStack.getUseDuration() - this.minecraft.player.getUseItemRemainingTicks() - tickDelta
                        + 1.0F;
                float m = l / CrossbowItem.getChargeDuration(itemStack);
                if (m > 1.0F)
                    m = 1.0F;
                if (m > 0.1F) {
                    float n = Mth.sin((l - 0.1F) * 1.3F);
                    float o = m - 0.1F;
                    float p = n * o;
                    poseStack.translate((p * 0.0F), (p * 0.004F), (p * 0.0F));
                }
                poseStack.translate((m * 0.0F), (m * 0.0F), (m * 0.04F));
                poseStack.scale(1.0F, 1.0F, 1.0F + m * 0.2F);
                poseStack.mulPose(Vector3f.YN.rotationDegrees(k * 45.0F));
            } else {
                float l = -0.4F * Mth.sin(Mth.sqrt(swingProgress) * 3.1415927F);
                float m = 0.2F * Mth.sin(Mth.sqrt(swingProgress) * 6.2831855F);
                float n = -0.2F * Mth.sin(swingProgress * 3.1415927F);
                poseStack.translate((k * l), m, n);
                applyItemArmTransform(poseStack, humanoidArm, equipProgress);
                applyItemArmAttackTransform(poseStack, humanoidArm, swingProgress);
                if (bl2 && swingProgress < 0.001F && bl) {
                    poseStack.translate((k * -0.641864F), 0.0D, 0.0D);
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(k * 10.0F));
                }
            }
            renderItem(abstractClientPlayer, itemStack,
                    bl3 ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND
                            : ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND,
                    !bl3, poseStack, multiBufferSource, light);
        } else {
            boolean bl2 = (humanoidArm == HumanoidArm.RIGHT);
            if (abstractClientPlayer.isUsingItem() && abstractClientPlayer.getUseItemRemainingTicks() > 0
                    && abstractClientPlayer.getUsedItemHand() == interactionHand) {
                float r, l;
                int q = bl2 ? 1 : -1;
                switch (itemStack.getUseAnimation()) {
                case NONE:
                    applyItemArmTransform(poseStack, humanoidArm, equipProgress);
                    break;
                case EAT:
                case DRINK:
                    applyEatTransform(poseStack, tickDelta, humanoidArm, itemStack);
                    applyItemArmTransform(poseStack, humanoidArm, equipProgress);
                    break;
                case BLOCK:
                    applyItemArmTransform(poseStack, humanoidArm, equipProgress);
                    break;
                case BOW:
                    applyItemArmTransform(poseStack, humanoidArm, equipProgress);
                    poseStack.translate((q * -0.2785682F), 0.18344387412071228D, 0.15731531381607056D);
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(-13.935F));
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(q * 35.3F));
                    poseStack.mulPose(Vector3f.ZP.rotationDegrees(q * -9.785F));
                    r = itemStack.getUseDuration() - this.minecraft.player.getUseItemRemainingTicks() - tickDelta
                            + 1.0F;
                    l = r / 20.0F;
                    l = (l * l + l * 2.0F) / 3.0F;
                    if (l > 1.0F)
                        l = 1.0F;
                    if (l > 0.1F) {
                        float m = Mth.sin((r - 0.1F) * 1.3F);
                        float n = l - 0.1F;
                        float o = m * n;
                        poseStack.translate((o * 0.0F), (o * 0.004F), (o * 0.0F));
                    }
                    poseStack.translate((l * 0.0F), (l * 0.0F), (l * 0.04F));
                    poseStack.scale(1.0F, 1.0F, 1.0F + l * 0.2F);
                    poseStack.mulPose(Vector3f.YN.rotationDegrees(q * 45.0F));
                    break;
                case SPEAR:
                    applyItemArmTransform(poseStack, humanoidArm, equipProgress);
                    poseStack.translate((q * -0.5F), 0.699999988079071D, 0.10000000149011612D);
                    poseStack.mulPose(Vector3f.XP.rotationDegrees(-55.0F));
                    poseStack.mulPose(Vector3f.YP.rotationDegrees(q * 35.3F));
                    poseStack.mulPose(Vector3f.ZP.rotationDegrees(q * -9.785F));
                    r = itemStack.getUseDuration() - this.minecraft.player.getUseItemRemainingTicks() - tickDelta
                            + 1.0F;
                    l = r / 10.0F;
                    if (l > 1.0F)
                        l = 1.0F;
                    if (l > 0.1F) {
                        float m = Mth.sin((r - 0.1F) * 1.3F);
                        float n = l - 0.1F;
                        float o = m * n;
                        poseStack.translate((o * 0.0F), (o * 0.004F), (o * 0.0F));
                    }
                    poseStack.translate(0.0D, 0.0D, (l * 0.2F));
                    poseStack.scale(1.0F, 1.0F, 1.0F + l * 0.2F);
                    poseStack.mulPose(Vector3f.YN.rotationDegrees(q * 45.0F));
                    break;
                }
                renderItem(abstractClientPlayer, itemStack,
                        bl2 ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND
                                : ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND,
                        !bl2, poseStack, multiBufferSource, light);
            } else if (abstractClientPlayer.isAutoSpinAttack()) {
                applyItemArmTransform(poseStack, humanoidArm, equipProgress);
                int q = bl2 ? 1 : -1;
                poseStack.translate((q * -0.4F), 0.800000011920929D, 0.30000001192092896D);
                poseStack.mulPose(Vector3f.YP.rotationDegrees(q * 65.0F));
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(q * -85.0F));
                renderItem(abstractClientPlayer, itemStack,
                        bl2 ? ItemTransforms.TransformType.FIRST_PERSON_RIGHT_HAND
                                : ItemTransforms.TransformType.FIRST_PERSON_LEFT_HAND,
                        !bl2, poseStack, multiBufferSource, light);
            } else {
                if (!abstractClientPlayer.isInvisible()) {
                    poseStack.pushPose();
                    renderCustomPlayerArm(poseStack, multiBufferSource, light, equipProgress, swingProgress, humanoidArm);
                    poseStack.popPose();
                }
                float s = -0.4F * Mth.sin(Mth.sqrt(swingProgress) * 3.1415927F);
                float r = 0.2F * Mth.sin(Mth.sqrt(swingProgress) * 6.2831855F);
                float l = -0.2F * Mth.sin(swingProgress * 3.1415927F);
                int t = bl2 ? 1 : -1;
                poseStack.translate((t * s), r, l);
                applyItemArmTransform(poseStack, humanoidArm, equipProgress);
                applyItemArmAttackTransform(poseStack, humanoidArm, swingProgress);
//                poseStack.translate(minecraft.player.getX()%1f, minecraft.player.getY()%1f, minecraft.player.getZ()%1f);
                poseStack.translate(AnimatedFirstPersonShared.debugConfig.itemOffsetX, AnimatedFirstPersonShared.debugConfig.itemOffsetY, AnimatedFirstPersonShared.debugConfig.itemOffsetZ);
//                poseStack.mulPose(Vector3f.XP.rotationDegrees(minecraft.player.yHeadRot));
//                poseStack.mulPose(Vector3f.ZP.rotationDegrees(minecraft.player.getXRot()));
                poseStack.mulPose(Vector3f.XP.rotationDegrees(AnimatedFirstPersonShared.debugConfig.itemRotationX));
                poseStack.mulPose(Vector3f.ZP.rotationDegrees(AnimatedFirstPersonShared.debugConfig.itemRotationZ));
                renderItem(abstractClientPlayer, itemStack,
                        bl2 ? ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND
                                : ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND,
                        !bl2, poseStack, multiBufferSource, light);
            }
        }
        poseStack.popPose();
        info.cancel();
        return;
    }

    private boolean debugOffset = false;
    
    private void renderCustomPlayerArm(PoseStack poseStack, MultiBufferSource multiBufferSource, int light, float equipProgress, float swingProgress,
            HumanoidArm humanoidArm) {
        boolean bl = (humanoidArm != HumanoidArm.LEFT);
        float armMultiplicator = bl ? 1.0F : -1.0F;
        float j = Mth.sqrt(swingProgress);
        float k = -0.3F * Mth.sin(j * 3.1415927F);
        float l = 0.4F * Mth.sin(j * 6.2831855F);
        float m = -0.4F * Mth.sin(swingProgress * 3.1415927F);
        poseStack.translate((armMultiplicator * (k + 0.64000005F)), (l + -0.6F + equipProgress * -0.6F), (m + -0.71999997F));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(armMultiplicator * AnimatedFirstPersonShared.debugConfig.armAngle)); // angle left right 45.0F
        poseStack.mulPose(Vector3f.XP.rotationDegrees(AnimatedFirstPersonShared.debugConfig.armAngleX));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(AnimatedFirstPersonShared.debugConfig.armAngleZ));
        // swing
        float n = Mth.sin(swingProgress * swingProgress * 3.1415927F);
        float o = Mth.sin(j * 3.1415927F);
        poseStack.mulPose(Vector3f.YP.rotationDegrees(armMultiplicator * o * 70.0F));
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(armMultiplicator * n * -20.0F));
        AbstractClientPlayer abstractClientPlayer = this.minecraft.player;
        RenderSystem.setShaderTexture(0, abstractClientPlayer.getSkinTextureLocation());
        poseStack.translate((armMultiplicator * -1.0F), 3.5999999046325684D, 3.5D);
        if(debugOffset) { // offset
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(armMultiplicator * (float)minecraft.player.getZ()));//120.0F
            poseStack.mulPose(Vector3f.XP.rotationDegrees((float)minecraft.player.getX()));//200
            poseStack.mulPose(Vector3f.YP.rotationDegrees(armMultiplicator * -(float)minecraft.player.getY()));//-135
        }else {
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(armMultiplicator * 120.0F + AnimatedFirstPersonShared.debugConfig.offsetZ));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(200.0F + AnimatedFirstPersonShared.debugConfig.offsetX));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(armMultiplicator * -135.0F + AnimatedFirstPersonShared.debugConfig.offsetY));
        }
        poseStack.translate((armMultiplicator * 5.6F), 0.0D, 0.0D);
        PlayerRenderer playerRenderer = (PlayerRenderer) this.entityRenderDispatcher
                .<AbstractClientPlayer>getRenderer(abstractClientPlayer);
        if (bl) {
            playerRenderer.renderRightHand(poseStack, multiBufferSource, light, abstractClientPlayer);
        } else {
            playerRenderer.renderLeftHand(poseStack, multiBufferSource, light, abstractClientPlayer);
        }
    }
    
    @Shadow
    private void renderPlayerArm(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, float f, float g,
            HumanoidArm humanoidArm) {
    }

    @Shadow
    private void renderTwoHandedMap(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, float f, float g,
            float h) {
    }

    @Shadow
    private void renderMap(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, ItemStack itemStack) {
    }

    @Shadow
    private void applyItemArmTransform(PoseStack poseStack, HumanoidArm humanoidArm, float f) {
    }

    @Shadow
    private void applyItemArmAttackTransform(PoseStack poseStack, HumanoidArm humanoidArm, float f) {
    }

    @Shadow
    public void renderItem(LivingEntity livingEntity, ItemStack itemStack, ItemTransforms.TransformType transformType,
            boolean bl, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
    }

    @Shadow
    private void applyEatTransform(PoseStack poseStack, float f, HumanoidArm humanoidArm, ItemStack itemStack) {
    }

    @Shadow
    private void renderOneHandedMap(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, float f,
            HumanoidArm humanoidArm, float g, ItemStack itemStack) {
    }

}
