package dev.tr7zw.animatedfirstperson;

import java.util.Map;
import java.util.Map.Entry;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import dev.tr7zw.animatedfirstperson.AnimationTypes.AnimationType;
import dev.tr7zw.animatedfirstperson.animation.Frame;
import dev.tr7zw.animatedfirstperson.animation.KeyframeAnimation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.tags.TagKey;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.item.Item;

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
                    action = action.substring(action.lastIndexOf("/")+1);
                    AnimationType type = AnimationTypes.animationTypes.get(action);
                    KeyframeAnimation animation =  new KeyframeAnimation();
                    if(entry.getValue().isJsonObject()) {
                        JsonObject rootObject = entry.getValue().getAsJsonObject();
                        if(rootObject.has("frames") && rootObject.get("frames").isJsonObject()) {
                            JsonObject frames = rootObject.get("frames").getAsJsonObject();
                            for(Entry<String, JsonElement> frameEntry : frames.entrySet()) {
                                if(frameEntry.getValue().isJsonArray()) {
                                    JsonArray jArray = frameEntry.getValue().getAsJsonArray();
                                    if(jArray.size() == 13) {
                                        float[] data = new float[13];
                                        for(int i = 0; i < 13; i++) {
                                            data[i] = jArray.get(i).getAsFloat();
                                        }
                                        animation.addKeyframe(Float.parseFloat(frameEntry.getKey()), new Frame(data));
                                    }
                                }
                            }
                        }
                    }
                    AnimatedFirstPersonShared.animationManager.getAnimationRegistry().registerTagAnimation(tag, type, animation);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

}
