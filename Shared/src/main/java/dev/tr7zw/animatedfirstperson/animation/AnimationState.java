package dev.tr7zw.animatedfirstperson.animation;

import dev.tr7zw.animatedfirstperson.AnimationTypes.AnimationType;
import net.minecraft.world.item.Item;

public class AnimationState {

    public Item lastItem = null;
    public float progress = 0;
    public AnimationSet lastAnimationSet = null;
    public AnimationType lastAnimationType = null;
    public Animation animation = null;
    public int animationProgress = 0;
    
}
