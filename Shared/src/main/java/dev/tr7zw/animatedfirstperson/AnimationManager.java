package dev.tr7zw.animatedfirstperson;

import dev.tr7zw.animatedfirstperson.animation.Frame;
import dev.tr7zw.animatedfirstperson.config.CustomConfigScreen;
import dev.tr7zw.animatedfirstperson.util.Easing;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public class AnimationManager {

    private Frame rightHandFrame = new Frame();
    private Frame leftHandFrame = new Frame();
    private Frame rightHandFrameLast = new Frame();
    private Frame leftHandFrameLast = new Frame();

    private Frame tempFrame = new Frame();
    private Frame fullHit = new Frame() {
        {
            setOffsetX(-1f);
            setOffsetY(-1f);
            setOffsetZ(-1.5f);
            setArmAngle(8);
            setArmAngleX(-48);
            setArmAngleZ(-66f);

//            setArmAngle(-10f);
        }
    };

    private ItemStack mainHandItem = ItemStack.EMPTY;
    private ItemStack offHandItem = ItemStack.EMPTY;
    private float mainHandHeight;
    private float oMainHandHeight;
    private float offHandHeight;
    private float oOffHandHeight;

    public Frame getFrame(HumanoidArm humanoidArm, float swingProgress, float delta) {
        if (Minecraft.getInstance().screen != null && Minecraft.getInstance().screen instanceof CustomConfigScreen) {
            return AnimatedFirstPersonShared.debugFrame;
        }
        if (humanoidArm == HumanoidArm.RIGHT) {
            return tempFrame.lerp(rightHandFrame, rightHandFrameLast, delta);
        } else {
            return tempFrame.lerp(leftHandFrame, leftHandFrameLast, delta);
        }
    }

    public float getEquipProgress(AbstractClientPlayer abstractClientPlayer, HumanoidArm arm, float delta) {
        if (arm == abstractClientPlayer.getMainArm()) {
            return 1.0F - Mth.lerp(delta, this.oMainHandHeight, this.mainHandHeight);
        } else {
            return 1.0F - Mth.lerp(delta, this.oOffHandHeight, this.offHandHeight);
        }
    }

    public void tick(AbstractClientPlayer player) {
        tickItemSwapAnimation(player);
        rightHandFrameLast.copyFrom(rightHandFrame);
        leftHandFrameLast.copyFrom(leftHandFrame);
        if (player.swinging) {
            boolean mainHand = player.swingingArm == InteractionHand.MAIN_HAND;
            boolean rightHanded = player.getMainArm() == HumanoidArm.RIGHT;
            Frame targetFrame = mainHand && rightHanded || mainHand && !rightHanded ? rightHandFrame : leftHandFrame;
            float fullHitOn = 0.3f;
            if (player.attackAnim < fullHitOn) {
                float progress = player.attackAnim / fullHitOn;
                targetFrame.createFrame(AnimatedFirstPersonShared.debugFrame, fullHit, progress, Easing.INOUTSINE);
            } else {
                float progress = (player.attackAnim - fullHitOn) / (1 - fullHitOn);
                targetFrame.createFrame(fullHit, AnimatedFirstPersonShared.debugFrame, progress, Easing.INOUTSINE);
            }
        }
    }

    private void tickItemSwapAnimation(AbstractClientPlayer localPlayer) {
        this.oMainHandHeight = this.mainHandHeight;
        this.oOffHandHeight = this.offHandHeight;
        ItemStack itemStack = localPlayer.getMainHandItem();
        ItemStack itemStack2 = localPlayer.getOffhandItem();
        if (!ItemStack.matches(this.mainHandItem, itemStack)) {
            this.mainHandItem = itemStack;
            this.mainHandHeight = -2F;
        }
        if (!ItemStack.matches(this.offHandItem, itemStack2)) {
            this.offHandItem = itemStack2;
            this.offHandHeight = -2F;
        }
//        float f = localPlayer.getAttackStrengthScale(1.0F);
        this.mainHandHeight = Mth.clamp(/*(f * f * f) +*/ this.mainHandHeight + 0.4F, -0.4F, 1F);
        this.offHandHeight = Mth.clamp(this.offHandHeight + 0.4F, -0.4F, 1F);
    }

}
