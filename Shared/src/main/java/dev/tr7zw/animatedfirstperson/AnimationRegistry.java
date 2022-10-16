package dev.tr7zw.animatedfirstperson;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicBoolean;

import dev.tr7zw.animatedfirstperson.AnimationTypes.AnimationType;
import dev.tr7zw.animatedfirstperson.animation.Animation;
import dev.tr7zw.animatedfirstperson.animation.AnimationSet;
import dev.tr7zw.animatedfirstperson.animation.AnimationState;
import dev.tr7zw.animatedfirstperson.animation.Frame;
import dev.tr7zw.animatedfirstperson.animation.KeyframeAnimation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AnimationRegistry {

    private final AnimationSet fallbackHolding = new AnimationSet().addAnimation(new KeyframeAnimation(1, 20, false) {
        {
            addKeyframe(0, new Frame(new float[] {0.26999998f, -0.17f, 0.17f, -40.5f, 59.5f, -72.5f, 58.5f, 0.0f, 189.0f, -0.4f, 0.65f, -0.17f, 70.0f}));
        }
    });
    
    private Map<TagKey<Item>, Map<AnimationType, AnimationSet>> tagAnimations = new HashMap<>();
    private Map<Item, Map<AnimationType, AnimationSet>> itemAnimations = new HashMap<>();
    private Map<AnimationType, AnimationSet> fallbackAnimations = new HashMap<>();
    private final AnimationState mainHandState = new AnimationState();
    private final AnimationState offHandState = new AnimationState();

    public AnimationRegistry() {
        reset();
    }
    
    /**
     * Gets called once per tick for each Arm
     * 
     * @param player
     * @param targetFrame
     * @param item
     * @param arm
     * @param mainHand
     */
    public void update(AbstractClientPlayer player, Frame targetFrame, AtomicBoolean fallbackVanilla, ItemStack item, InteractionHand hand,
            HumanoidArm arm, boolean mainHand) {
        AnimationState animationSate = mainHand ? mainHandState : offHandState;
        // TODO temporary workaround till "empty hand" can be assigned animations
        if(item.isEmpty()) {
            cleanupAnimation(animationSate, fallbackVanilla, targetFrame);
            return;
        }
        AnimationSet holding = getAnimationSet(item, AnimationTypes.holding);
        if (player.swinging && mainHand) {
            AnimationSet hitting = getAnimationSet(item, AnimationTypes.hitting);
            if(hitting == null) { // no animation = use vanilla
                cleanupAnimation(animationSate, fallbackVanilla, targetFrame);
                return;
            }
            setupAnimation(animationSate, AnimationTypes.hitting, hitting, player.attackAnim);
        } else if (player.isUsingItem() && player.getUseItemRemainingTicks() > 0 && player.getUsedItemHand() == hand) {
            AnimationType animationType = null;
            switch (item.getUseAnimation()) {
            case EAT:
                animationType = AnimationTypes.useEating;
                break;
            case DRINK:
                animationType = AnimationTypes.useDrink;
                break;
            case BLOCK:
                animationType = AnimationTypes.useBlock;
                break;
            case BOW:
                animationType = AnimationTypes.useBow;
                break;
            case SPEAR:
                animationType = AnimationTypes.useSpear;
                break;
            case NONE:
            default:
                animationType = AnimationTypes.useNone;
            }
            AnimationSet animation = getAnimationSet(item, animationType);
            if(animation == null) { // no animation = use vanilla
                cleanupAnimation(animationSate, fallbackVanilla, targetFrame);
                return;
            }
            setupAnimation(animationSate, animationType, animation, null);
        } else if(AnimatedFirstPersonShared.instance.isInspecting() && mainHand && !item.isEmpty()) {
            AnimationSet animation = getAnimationSet(item, AnimationTypes.inspect);
            if(animation == null) { // no animation = use vanilla
                //TODO default animation
                cleanupAnimation(animationSate, fallbackVanilla, targetFrame);
                return;
            }
            setupAnimation(animationSate, AnimationTypes.inspect, animation, null);
        } else {
            if(holding == null) { // no animation = use vanilla
                if(animationSate.lastAnimationType != null && animationSate.lastAnimationType != AnimationTypes.holding) {
                    // use internal fallback to return to vanilla state
                    holding = fallbackHolding;
                } else {
                    cleanupAnimation(animationSate, fallbackVanilla, targetFrame);
                    return;
                }
            }
            setupAnimation(animationSate, AnimationTypes.holding, holding, null);
        }
        if(holding == null) {
            holding = fallbackHolding; // to get a start/end frame for the animation
        }
        animationSate.animation.tickFrame(animationSate.progress, targetFrame, holding.getFirst().getFirstFrame(),
                holding.getFirst().getFirstFrame());
        targetFrame.setHideArm(animationSate.animation.hideArm());
        fallbackVanilla.set(false);
    }
    
    private void cleanupAnimation(AnimationState state, AtomicBoolean fallbackVanilla, Frame targetFrame) {
        fallbackVanilla.set(true);
        state.animation = null;
        state.animationProgress = 0;
        state.lastAnimationType = null;
        state.lastAnimationType = null;
        state.progress = 0;
        targetFrame.copyFrom(fallbackHolding.getFirst().getFirstFrame());
    }

    private void setupAnimation(AnimationState state, AnimationType type, AnimationSet set, Float forcedProgress) {
        // is this the same animation as last tick?
        if (state.lastAnimationSet == set && state.lastAnimationType == type) { // same animation
            if (forcedProgress != null) { // not timer based animation(hitting)
                if (state.progress > forcedProgress) { // the animation progress ran backwards = reset = pick new
                                                       // animation
                    state.animation = set.getRandomAnimation();
                } // otherwise we still play the same one
                state.progress = forcedProgress.floatValue();
            } else { // timer based animation
                state.animationProgress++;
                if (state.animationProgress > state.animation.length()) { // animation ran out, pick new one
                    state.animation = set.getRandomAnimation();
                    state.animationProgress = 0;
                }
                state.progress = ((float) state.animationProgress) / ((float) state.animation.length());
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
        if (cache.containsKey(type)) {
            return cache.get(type);
        }
        AnimationSet lookedUp = lookupAnimation(item, type);
        cache.put(type, lookedUp);
        return lookedUp;
    }

    private AnimationSet lookupAnimation(ItemStack item, AnimationType type) {
        Optional<Map<AnimationType, AnimationSet>> tagData = tagAnimations.entrySet().stream()
                .filter(entry -> item.is(entry.getKey())).map(entry -> entry.getValue()).findAny();
        if (tagData.isPresent() && tagData.get().containsKey(type)) {
            return tagData.get().get(type);
        }
        // fallback
        AnimationSet animationSet = fallbackAnimations.get(type);
        if(animationSet != null) {
            return animationSet;
        }
        return null;
    }

    public void registerTagAnimation(TagKey<Item> key, AnimationType type, AnimationSet animation) {
        Map<AnimationType, AnimationSet> map = tagAnimations.computeIfAbsent(key, k -> new HashMap<>());
        map.put(type, animation);
    }

    public void registerItemAnimation(Item key, AnimationType type, AnimationSet animation) {
        Map<AnimationType, AnimationSet> map = itemAnimations.computeIfAbsent(key, k -> new HashMap<>());
        map.put(type, animation);
    }
    
    public void registerFallbackAnimation(AnimationType type, AnimationSet animation) {
        fallbackAnimations.put(type, animation);
    }

    public void reset() {
        tagAnimations.clear();
        itemAnimations.clear();
        fallbackAnimations.clear();
    }

    public Animation getMainHandAnimation() {
        return mainHandState.animation;
    }

    public Animation getOffHandAnimation() {
        return offHandState.animation;
    }

}
