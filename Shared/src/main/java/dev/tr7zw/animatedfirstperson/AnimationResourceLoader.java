package dev.tr7zw.animatedfirstperson;

import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.tr7zw.animatedfirstperson.AnimationTypes.AnimationType;
import dev.tr7zw.animatedfirstperson.animation.AnimationSet;
import dev.tr7zw.animatedfirstperson.animation.Frame;
import dev.tr7zw.animatedfirstperson.animation.KeyframeAnimation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

public class AnimationResourceLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

    public AnimationResourceLoader() {
        super(GSON, "animations");

    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager paramResourceManager,
            ProfilerFiller paramProfilerFiller) {
        System.out.println(map);
        AnimatedFirstPersonShared.animationManager.getAnimationRegistry().reset();
        for (Entry<ResourceLocation, JsonElement> entry : map.entrySet()) {
            try {
                if (!entry.getKey().getNamespace().equals("animatedfirstperson"))
                    continue;
                if (entry.getKey().getPath().startsWith("tags/")) {
                    String tagKey = entry.getKey().getPath().replace("tags/", "").replace("-", ":");
                    tagKey = tagKey.substring(0, tagKey.indexOf("/"));
                    ResourceLocation resourceLocation = new ResourceLocation(tagKey);
                    System.out.println("Tag Folder: " + resourceLocation);
                    TagKey<Item> tag = TagKey.create(Registry.ITEM_REGISTRY, resourceLocation);
                    String action = entry.getKey().getPath();
                    action = action.substring(action.lastIndexOf("/") + 1);
                    AnimationType type = AnimationTypes.animationTypes.get(action);
                    if (type == null)
                        continue;
                    if (entry.getValue().isJsonArray()) {
                        AnimationSet animSet = loadAnimationSet(entry.getValue().getAsJsonArray());
                        AnimatedFirstPersonShared.animationManager.getAnimationRegistry().registerTagAnimation(tag,
                                type, animSet);
                    }
                }
                if (entry.getKey().getPath().startsWith("items/")) {
                    String itemKey = entry.getKey().getPath().replace("items/", "").replace("-", ":");
                    itemKey = itemKey.substring(0, itemKey.indexOf("/"));
                    ResourceLocation resourceLocation = new ResourceLocation(itemKey);
                    System.out.println("Item Folder: " + resourceLocation);
                    Item item = Registry.ITEM.get(resourceLocation);
                    if(item == null) {
                        System.out.println("Item " + resourceLocation + " not found");
                        continue;
                    }
                    String action = entry.getKey().getPath();
                    action = action.substring(action.lastIndexOf("/") + 1);
                    AnimationType type = AnimationTypes.animationTypes.get(action);
                    if (type == null)
                        continue;
                    if (entry.getValue().isJsonArray()) {
                        AnimationSet animSet = loadAnimationSet(entry.getValue().getAsJsonArray());
                        AnimatedFirstPersonShared.animationManager.getAnimationRegistry().registerItemAnimation(item,
                                type, animSet);
                    }
                }
                if (entry.getKey().getPath().startsWith("default/")) {
                    String action = entry.getKey().getPath();
                    action = action.substring(action.lastIndexOf("/") + 1);
                    AnimationType type = AnimationTypes.animationTypes.get(action);
                    if (type == null)
                        continue;
                    if (entry.getValue().isJsonArray()) {
                        AnimationSet animSet = loadAnimationSet(entry.getValue().getAsJsonArray());
                        AnimatedFirstPersonShared.animationManager.getAnimationRegistry().registerFallbackAnimation(type, animSet);
                    }
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private AnimationSet loadAnimationSet(JsonArray array) {
        AnimationSet animSet = new AnimationSet();
        AtomicBoolean hasAnimations = new AtomicBoolean(false);
        array.forEach(el -> {
            if (el.isJsonObject()) {
                KeyframeAnimation animation = loadAnimation(el.getAsJsonObject());
                animSet.addAnimation(animation);
                hasAnimations.set(true);
            }
        });
        if (hasAnimations.get())
            return animSet;
        return null;
    }
    
    private KeyframeAnimation loadAnimation(JsonObject rootObject) {
        if (!(rootObject.has("weight") || rootObject.has("duration"))) {
            return null;
        }
        boolean hideArm = false;
        if(rootObject.has("hideArm")) {
            hideArm = rootObject.get("hideArm").getAsBoolean();
        }
        KeyframeAnimation animation = new KeyframeAnimation(rootObject.get("weight").getAsInt(),
                rootObject.get("duration").getAsInt(), hideArm);
        if (rootObject.has("frames") && rootObject.get("frames").isJsonObject()) {
            JsonObject frames = rootObject.get("frames").getAsJsonObject();
            boolean hasFrames = false;
            for (Entry<String, JsonElement> frameEntry : frames.entrySet()) {
                if (frameEntry.getValue().isJsonArray()) {
                    JsonArray jArray = frameEntry.getValue().getAsJsonArray();
                    if (jArray.size() == 13) {
                        float[] data = new float[13];
                        for (int i = 0; i < 13; i++) {
                            data[i] = jArray.get(i).getAsFloat();
                        }
                        animation.addKeyframe(Float.parseFloat(frameEntry.getKey()), new Frame(data));
                        hasFrames = true;
                    }
                }
            }
            if (hasFrames)
                return animation;
        }
        return null;
    }

}
