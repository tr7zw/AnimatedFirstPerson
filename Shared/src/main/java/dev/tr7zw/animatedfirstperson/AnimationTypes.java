package dev.tr7zw.animatedfirstperson;

import java.util.HashMap;
import java.util.Map;

public class AnimationTypes {

    public static final Map<String, AnimationType> animationTypes = new HashMap<>();
    public static final AnimationType holding = new AnimationType("holding");
    public static final AnimationType hitting = new AnimationType("hitting");
    
    
    public static record AnimationType(String name) {
        public AnimationType(String name) {
            this.name = name;
            animationTypes.put(name, this);
        }
    }
    
}
