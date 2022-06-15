package dev.tr7zw.animatedfirstperson;

import java.util.HashMap;
import java.util.Map;

public class AnimationTypes {

    public static final Map<String, AnimationType> animationTypes = new HashMap<>();
    public static final AnimationType holding = new AnimationType("holding");
    public static final AnimationType hitting = new AnimationType("hitting");
    public static final AnimationType useNone = new AnimationType("usenone");
    public static final AnimationType useEating = new AnimationType("useeating");
    public static final AnimationType useDrink = new AnimationType("usedrink");
    public static final AnimationType useBlock = new AnimationType("useblock");
    public static final AnimationType useBow = new AnimationType("usebow");
    public static final AnimationType useSpear = new AnimationType("usespear");
    public static final AnimationType inspect = new AnimationType("inspect");
    
    
    public static record AnimationType(String name) {
        public AnimationType(String name) {
            this.name = name;
            animationTypes.put(name, this);
        }
    }
    
}
