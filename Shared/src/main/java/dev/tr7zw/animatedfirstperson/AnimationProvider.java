package dev.tr7zw.animatedfirstperson;

import dev.tr7zw.animatedfirstperson.animation.Frame;
import dev.tr7zw.animatedfirstperson.config.CustomConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.ItemStack;

public class AnimationProvider {

    private Frame rightHandFrame = new Frame();
    private Frame leftHandFrame = new Frame();
    private Frame rightHandFrameLast = new Frame();
    private Frame leftHandFrameLast = new Frame();

    private Frame tempFrame = new Frame();
    private AnimationRegistry animationRegistry = new AnimationRegistry();

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
        boolean rightHanded = player.getMainArm() == HumanoidArm.RIGHT;
        animationRegistry.update(player, rightHandFrame, rightHanded ? player.getMainHandItem() : player.getOffhandItem(), rightHanded ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, HumanoidArm.RIGHT, rightHanded);
        animationRegistry.update(player, leftHandFrame, !rightHanded ? player.getMainHandItem() : player.getOffhandItem(), !rightHanded ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, HumanoidArm.LEFT, !rightHanded);
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

    public AnimationRegistry getAnimationRegistry() {
        return animationRegistry;
    }

}
