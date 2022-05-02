package dev.tr7zw.animatedfirstperson;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import dev.tr7zw.animatedfirstperson.config.debug.DebugConfig;

public class AnimatedFirstPersonShared {

    public static final Logger LOGGER = LogManager.getLogger("AnimatedFirstPerson");
    public static AnimatedFirstPersonShared instance;
    public static DebugConfig debugConfig = new DebugConfig();
    
    public void init() {
        instance = this;
        LOGGER.info("Loading Animated First Person!");
    }
    
}
