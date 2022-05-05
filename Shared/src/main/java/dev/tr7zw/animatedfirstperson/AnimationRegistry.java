package dev.tr7zw.animatedfirstperson;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import dev.tr7zw.animatedfirstperson.AnimationTypes.AnimationType;
import dev.tr7zw.animatedfirstperson.animation.Frame;
import dev.tr7zw.animatedfirstperson.animation.KeyframeAnimation;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AnimationRegistry {

    private final KeyframeAnimation fallbackHolding = new KeyframeAnimation() {
        {
            addKeyframe(0, new Frame());
        }
    };
    private final KeyframeAnimation fallbackHitting = new KeyframeAnimation() {
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
    private Map<TagKey<Item>, Map<AnimationType, KeyframeAnimation>> tagAnimations = new HashMap<>();
    private Map<Item, Map<AnimationType, KeyframeAnimation>> itemAnimations = new HashMap<>();

    public void update(AbstractClientPlayer player, Frame targetFrame, ItemStack item, HumanoidArm arm, boolean mainHand) {
        Frame holding = getAnimation(item, AnimationTypes.holding).getFirstFrame();
        if (player.swinging && mainHand) {
            KeyframeAnimation hitting = getAnimation(item, AnimationTypes.hitting);
            hitting.tickFrame(player.attackAnim, targetFrame, holding, holding);
            return;
        }
        KeyframeAnimation hitting = getAnimation(item, AnimationTypes.holding);
        hitting.tickFrame(player.attackAnim, targetFrame, holding, holding);
    }
    
    public KeyframeAnimation getAnimation(ItemStack item, AnimationType type) {
        Map<AnimationType, KeyframeAnimation> cache = itemAnimations.computeIfAbsent(item.getItem(), i -> new HashMap<>());
        if(cache.containsKey(type)) {
            return cache.get(type);
        }
        KeyframeAnimation lookedUp = lookupAnimation(item, type);
        cache.put(type, lookedUp);
        return lookedUp;
    }
    
    private KeyframeAnimation lookupAnimation(ItemStack item, AnimationType type) {
        Optional<Map<AnimationType, KeyframeAnimation>> tagData =  tagAnimations.entrySet().stream().filter(entry -> item.is(entry.getKey())).map(entry -> entry.getValue()).findAny();
        if(tagData.isPresent() && tagData.get().containsKey(type)) {
            return tagData.get().get(type);
        }
        // fallback
        if(type == AnimationTypes.hitting) {
            return fallbackHitting;
        }
        return fallbackHolding;
    }
    
    public void registerTagAnimation(TagKey<Item> key, AnimationType type, KeyframeAnimation animation) {
        Map<AnimationType, KeyframeAnimation> map = tagAnimations.computeIfAbsent(key, k -> new HashMap<>());
        map.put(type, animation);
    }
    
    public void reset() {
        tagAnimations.clear();
        itemAnimations.clear();
    }
    
}
