package dev.tr7zw.animatedfirstperson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.tr7zw.animatedfirstperson.animation.Frame;

public class AnimatedFirstPersonShared {

    public static final Logger LOGGER = LogManager.getLogger("AnimatedFirstPerson");
    public static AnimatedFirstPersonShared instance;
    public static Frame debugFrame = new Frame();
    public static AnimationManager animationManager = new AnimationManager();
    
    public void init() {
        instance = this;
        LOGGER.info("Loading Animated First Person!");
    }
    
}
