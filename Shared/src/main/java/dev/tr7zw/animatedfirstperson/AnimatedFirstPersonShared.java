package dev.tr7zw.animatedfirstperson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.tr7zw.animatedfirstperson.animation.Frame;
import net.minecraft.client.KeyMapping;

public abstract class AnimatedFirstPersonShared {

    public static final Logger LOGGER = LogManager.getLogger("AnimatedFirstPerson");
    public static AnimatedFirstPersonShared instance;
    public static Frame debugFrame = new Frame();
    public static AnimationProvider animationManager = new AnimationProvider();
    public static KeyMapping keyBinding = new KeyMapping("key.afp.inspect", 295, "AnimatedFirstPerson");
    
    public void init() {
        instance = this;
        LOGGER.info("Loading Animated First Person!");
    }
    
    public boolean isInspecting() {
        return keyBinding.isDown();
    }
    
    
}
