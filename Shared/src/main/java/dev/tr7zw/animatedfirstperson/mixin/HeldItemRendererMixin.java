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
import dev.tr7zw.animatedfirstperson.AnimationProvider;
import dev.tr7zw.animatedfirstperson.animation.Frame;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

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
    private AnimationProvider animationManager = AnimatedFirstPersonShared.animationManager;

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
        boolean bl2 = (humanoidArm == HumanoidArm.RIGHT);
        if (abstractClientPlayer.isAutoSpinAttack()) {
            return;// fall back to vanilla
        } else {

            int armMultiplicator = bl2 ? 1 : -1; //t
            equipProgress = animationManager.getEquipProgress(abstractClientPlayer, humanoidArm, tickDelta); // overwrite with own that ignores the busy flag
            Frame frame = animationManager.getFrame(humanoidArm, swingProgress, tickDelta);
            if(frame == null) {
                return; // fall back to vanilla
            }
            minecraft.gameRenderer.resetProjectionMatrix(minecraft.gameRenderer.getProjectionMatrix(frame.getFov()));

            renderCustomPlayerArm(frame, poseStack, multiBufferSource, light, equipProgress, swingProgress, humanoidArm, !(abstractClientPlayer.isInvisible() || frame.isHideArm()));
            poseStack.translate(armMultiplicator * frame.getItemOffsetX(), frame.getItemOffsetY(), frame.getItemOffsetZ());

            poseStack.mulPose(Vector3f.YP.rotationDegrees(frame.getItemRotationY()));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(frame.getItemRotationX()));
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(frame.getItemRotationZ()));
            renderItem(abstractClientPlayer, itemStack,
                    bl2 ? ItemTransforms.TransformType.THIRD_PERSON_RIGHT_HAND
                            : ItemTransforms.TransformType.THIRD_PERSON_LEFT_HAND,
                    !bl2, poseStack, multiBufferSource, light);
        }
        poseStack.popPose();
        info.cancel();
    }
    
    private void renderCustomPlayerArm(Frame frame, PoseStack poseStack, MultiBufferSource multiBufferSource, int light, float equipProgress, float swingProgress,
            HumanoidArm humanoidArm, boolean doRender) {
        boolean bl = (humanoidArm != HumanoidArm.LEFT);
        float armMultiplicator = bl ? 1.0F : -1.0F;
        AbstractClientPlayer abstractClientPlayer = this.minecraft.player;
        RenderSystem.setShaderTexture(0, abstractClientPlayer.getSkinTextureLocation());
        poseStack.translate((armMultiplicator * (0.64000005F)) + frame.getOffsetX() * armMultiplicator, (-0.6F + equipProgress * -0.6F + frame.getOffsetY()), (-0.71999997F) + frame.getOffsetZ());
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(armMultiplicator * frame.getArmAngleZ()));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(frame.getArmAngleX()));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(armMultiplicator * frame.getArmAngleY())); // angle left right 45.0F
        poseStack.translate((armMultiplicator * -1.0F), 3.5999999046325684D, 3.5D);
        poseStack.mulPose(Vector3f.ZP.rotationDegrees(armMultiplicator * 120.0F));
        poseStack.mulPose(Vector3f.XP.rotationDegrees(200.0F));
        poseStack.mulPose(Vector3f.YP.rotationDegrees(armMultiplicator * -135.0F));
        poseStack.translate((armMultiplicator * 5.6F), 0.0D, 0.0D);
        if(doRender) {
            PlayerRenderer playerRenderer = (PlayerRenderer) this.entityRenderDispatcher
                    .<AbstractClientPlayer>getRenderer(abstractClientPlayer);
            if (bl) {
                playerRenderer.renderRightHand(poseStack, multiBufferSource, light, abstractClientPlayer);
            } else {
                playerRenderer.renderLeftHand(poseStack, multiBufferSource, light, abstractClientPlayer);
            }
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
