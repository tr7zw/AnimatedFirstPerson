package dev.tr7zw.animatedfirstperson;

import dev.tr7zw.animatedfirstperson.animation.Frame;
import dev.tr7zw.animatedfirstperson.animation.KeyframeAnimation;
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
    private Frame restingFrame = new Frame();
    private KeyframeAnimation hitting = new KeyframeAnimation() {
        {
            Frame preHit = new Frame() {
                {
                    setArmAngleX(-23.5f);
                    setArmAngleY(100);
                    setArmAngleZ(-58f);

                }
            };
            Frame halfHit = new Frame() {
                {
                    setOffsetX(-0.25f);
                    setOffsetY(-0.22f);
                    setOffsetZ(-0.39f);
                    setArmAngleX(-61);
                    setArmAngleY(52);
                    setArmAngleZ(-88f);

                }
            };
            Frame fullHit = new Frame() {
                {
                    setOffsetX(-0.39f);
                    setOffsetY(-0.41f);
                    setOffsetZ(-0.3f);
                    setArmAngleX(-53.5f);
                    setArmAngleY(-20f);
                    setArmAngleZ(-139.5f);

                }
            };
            addKeyframe(0.3f, preHit);
            addKeyframe(0.5f, halfHit);
            addKeyframe(0.6f, fullHit);
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
            hitting.tickFrame(player.attackAnim, targetFrame, restingFrame, restingFrame);
        } else {
            rightHandFrame.copyFrom(restingFrame);
            leftHandFrame.copyFrom(restingFrame);
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
