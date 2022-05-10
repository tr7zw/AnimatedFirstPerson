package dev.tr7zw.animatedfirstperson.animation;

public interface Animation {

    /**
     * @param progress 0-1
     * @param buffer   Frame that get modified with the current info
     * @param before   Frame from before the animation starts
     * @param after    Frame for after the animation ends
     */
    void tickFrame(float progress, Frame buffer, Frame before, Frame after);

    Frame getFirstFrame();
    
    /**
     * The higher the number, the higher the probability relative to the other animations
     * 
     * @return
     */
    int getProbability();
    
    /**
     * Animation length in ticks. Ignored for the hitting animation
     * 
     * @return
     */
    int length();

}