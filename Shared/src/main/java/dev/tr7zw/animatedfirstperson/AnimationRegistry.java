package dev.tr7zw.animatedfirstperson;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import dev.tr7zw.animatedfirstperson.AnimationTypes.AnimationType;
import dev.tr7zw.animatedfirstperson.animation.Animation;
import dev.tr7zw.animatedfirstperson.animation.AnimationSet;
import dev.tr7zw.animatedfirstperson.animation.AnimationState;
import dev.tr7zw.animatedfirstperson.animation.Frame;
import dev.tr7zw.animatedfirstperson.animation.KeyframeAnimation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AnimationRegistry {

    private final AnimationSet fallbackHolding = new AnimationSet().addAnimation(new KeyframeAnimation(1, 20, false) {
        {
            addKeyframe(0, new Frame());
        }
    });
    private final AnimationSet fallbackHitting = new AnimationSet().addAnimation(new KeyframeAnimation(1, 1, false) {
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
    });
    private Map<TagKey<Item>, Map<AnimationType, AnimationSet>> tagAnimations = new HashMap<>();
    private Map<Item, Map<AnimationType, AnimationSet>> itemAnimations = new HashMap<>();
    private final AnimationState mainHandState = new AnimationState();
    private final AnimationState offHandState = new AnimationState();

    /**
     * Gets called once per tick for each Arm
     * 
     * @param player
     * @param targetFrame
     * @param item
     * @param arm
     * @param mainHand
     */
    public void update(AbstractClientPlayer player, Frame targetFrame, ItemStack item, HumanoidArm arm, boolean mainHand) {
        AnimationState animationSate = mainHand ? mainHandState : offHandState;
        AnimationSet holding = getAnimationSet(item, AnimationTypes.holding);
        if (player.swinging && mainHand) {
            AnimationSet hitting = getAnimationSet(item, AnimationTypes.hitting);
            setupAnimation(animationSate, AnimationTypes.hitting, hitting, player.attackAnim);
        } else {
            setupAnimation(animationSate, AnimationTypes.holding, holding, null);
        }
        animationSate.animation.tickFrame(animationSate.progress, targetFrame, holding.getFirst().getFirstFrame(), holding.getFirst().getFirstFrame());
        targetFrame.setHideArm(animationSate.animation.hideArm());
    }
    
    private void setupAnimation(AnimationState state, AnimationType type, AnimationSet set, Float forcedProgress) {
        // is this the same animation as last tick?
        if(state.lastAnimationSet == set && state.lastAnimationType == type) { //same animation
            if(forcedProgress != null) { // not timer based animation(hitting)
                if(state.progress > forcedProgress) { // the animation progress ran backwards = reset = pick new animation
                    state.animation = set.getRandomAnimation();
                }// otherwise we still play the same one
                state.progress = forcedProgress.floatValue();
            } else { // timer based animation
                state.animationProgress++;
                if(state.animationProgress > state.animation.length()) { // animation ran out, pick new one
                    state.animation = set.getRandomAnimation();
                    state.animationProgress = 0;
                }
                state.progress = ((float)state.animationProgress) / ((float)state.animation.length());
            }
        } else { // animation type changed
            state.lastAnimationType = type;
            state.lastAnimationSet = set;
            state.animation = set.getRandomAnimation(); // pick a new random animation
            state.progress = 0;
            state.animationProgress = 0;
        }
    }
    
    private AnimationSet getAnimationSet(ItemStack item, AnimationType type) {
        Map<AnimationType, AnimationSet> cache = itemAnimations.computeIfAbsent(item.getItem(), i -> new HashMap<>());
        if(cache.containsKey(type)) {
            return cache.get(type);
        }
        AnimationSet lookedUp = lookupAnimation(item, type);
        cache.put(type, lookedUp);
        return lookedUp;
    }
    
    private AnimationSet lookupAnimation(ItemStack item, AnimationType type) {
        Optional<Map<AnimationType, AnimationSet>> tagData =  tagAnimations.entrySet().stream().filter(entry -> item.is(entry.getKey())).map(entry -> entry.getValue()).findAny();
        if(tagData.isPresent() && tagData.get().containsKey(type)) {
            return tagData.get().get(type);
        }
        // fallback
        if(type == AnimationTypes.hitting) {
            return fallbackHitting;
        }
        return fallbackHolding;
    }
    
    public void registerTagAnimation(TagKey<Item> key, AnimationType type, AnimationSet animation) {
        Map<AnimationType, AnimationSet> map = tagAnimations.computeIfAbsent(key, k -> new HashMap<>());
        map.put(type, animation);
    }
    
    public void reset() {
        tagAnimations.clear();
        itemAnimations.clear();
    }

    public Animation getMainHandAnimation() {
        return mainHandState.animation;
    }

    public Animation getOffHandAnimation() {
        return offHandState.animation;
    }
    
}
