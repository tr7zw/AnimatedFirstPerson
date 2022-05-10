package dev.tr7zw.animatedfirstperson.animation;

import java.util.ArrayList;
import java.util.List;

public class AnimationSet {

    private List<Animation> animations = new ArrayList<>();
    private int probabilityCounter = 0;
    
    public AnimationSet addAnimation(Animation animation) {
        animations.add(animation);
        probabilityCounter += animation.getProbability();
        return this;
    }
    
    public Animation getFirst() {
        return animations.get(0);
    }
    
    public Animation getRandomAnimation() {
        if(animations.size() == 1) 
            return animations.get(0);
        
        int r = (int) (Math.random() * probabilityCounter) + 1;
        int counterWeight = 0;
        for(Animation anim : animations) {
            counterWeight += anim.getProbability();
            if(counterWeight >= r)
                return anim;
        }
        return null;//Impossible(hopefully)
    }

    @Override
    public String toString() {
        return "AnimationSet [animations=" + animations + ", probabilityCounter=" + probabilityCounter + "]";
    }

}
