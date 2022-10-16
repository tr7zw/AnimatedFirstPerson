package dev.tr7zw.animatedfirstperson;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
                    if (type == null) {
                        System.out.println("Unknown type: " + action);
                        continue;
                    }
                    if (entry.getValue().isJsonArray()) {
                        AnimationSet animSet = loadAnimationSet(entry.getValue().getAsJsonArray());
                        AnimatedFirstPersonShared.animationManager.getAnimationRegistry().registerTagAnimation(tag,
                                type, animSet);
                    } else {
                        System.out.println("Incorrect data type in " + entry.getKey().getPath());
                    }
                    continue;
                }
                if (entry.getKey().getPath().startsWith("groups/")) {
                    String action = entry.getKey().getPath();
                    ResourceLocation itemKey = new ResourceLocation("animatedfirstperson", action.substring(0,action.lastIndexOf("/")+1) + "items");
                    JsonElement itemJson = map.get(itemKey);
                    if(itemJson == null) {
                        System.out.println("No items found at " + itemKey);
                        continue;
                    }
                    Set<Item> items = getItems(itemJson);
                    action = action.substring(action.lastIndexOf("/") + 1);
                    AnimationType type = AnimationTypes.animationTypes.get(action);
                    if (type == null) {
                        System.out.println("Unknown type: " + action);
                        continue;
                    }
                    if (entry.getValue().isJsonArray()) {
                        AnimationSet animSet = loadAnimationSet(entry.getValue().getAsJsonArray());
                        for(Item item : items) {
                            AnimatedFirstPersonShared.animationManager.getAnimationRegistry().registerItemAnimation(item, type, animSet);
                        }
                    } else {
                        System.out.println("Incorrect data type in " + entry.getKey().getPath());
                    }
                    continue;
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
                    if (type == null) {
                        System.out.println("Unknown type: " + action);
                        continue;
                    }
                    if (entry.getValue().isJsonArray()) {
                        AnimationSet animSet = loadAnimationSet(entry.getValue().getAsJsonArray());
                        AnimatedFirstPersonShared.animationManager.getAnimationRegistry().registerItemAnimation(item,
                                type, animSet);
                    } else {
                        System.out.println("Incorrect data type in " + entry.getKey().getPath());
                    }
                    continue;
                }
                if (entry.getKey().getPath().startsWith("default/")) {
                    String action = entry.getKey().getPath();
                    action = action.substring(action.lastIndexOf("/") + 1);
                    AnimationType type = AnimationTypes.animationTypes.get(action);
                    if (type == null) {
                        System.out.println("Unknown type: " + action);
                        continue;
                    }
                    if (entry.getValue().isJsonArray()) {
                        AnimationSet animSet = loadAnimationSet(entry.getValue().getAsJsonArray());
                        AnimatedFirstPersonShared.animationManager.getAnimationRegistry().registerFallbackAnimation(type, animSet);
                    }
                    continue;
                }
                System.out.println("Unknown resource: " + entry.getKey().getPath());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    private Set<Item> getItems(JsonElement json){
        Set<Item> items = new HashSet<>();
        if(json.isJsonArray()) {
            JsonArray array = json.getAsJsonArray();
            array.forEach(el ->{
                Item item = Registry.ITEM.get(new ResourceLocation(el.getAsString()));
                if(item != null) {
                    items.add(item);
                }
            });
        }
        return items;
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
        int weight = rootObject.has("weight")? rootObject.get("weight").getAsInt() : 1;
        int duration = rootObject.has("duration")? rootObject.get("duration").getAsInt() : 20;
        boolean hideArm = false;
        if(rootObject.has("hideArm")) {
            hideArm = rootObject.get("hideArm").getAsBoolean();
        }
        KeyframeAnimation animation = new KeyframeAnimation(weight, duration, hideArm);
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
