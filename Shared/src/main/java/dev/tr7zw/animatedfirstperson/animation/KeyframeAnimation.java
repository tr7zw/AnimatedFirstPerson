package dev.tr7zw.animatedfirstperson.animation;

import java.util.ArrayList;
import java.util.List;

import dev.tr7zw.animatedfirstperson.util.Easing;

public class KeyframeAnimation {

    private final static Frame fallback = new Frame();
    private List<Keyframe> keyframes = new ArrayList<>();

    /**
     * @param progress 0-1
     * @param buffer   Frame that get modified with the current info
     * @param before   Frame from before the animation starts
     * @param after    Frame for after the animation ends
     */
    public void tickFrame(float progress, Frame buffer, Frame before, Frame after) {
        // Find the correct frame
        Keyframe next = null;
        Keyframe current = null;
        for (Keyframe keyframe : keyframes) {
            if (progress >= keyframe.progress) {
                current = keyframe;
            } else {
                next = keyframe;
                break;
            }
        }
        float endProgress = next != null ? next.progress : 1f;
        float startProgress = current != null ? current.progress : 0;
        float offsetProgress = progress - startProgress;
        float offsetEnd = endProgress - startProgress;
        float scaledProgress = offsetProgress / offsetEnd;
        buffer.createFrame(current != null ? current.frame : before, next != null ? next.frame : after, scaledProgress, Easing.INOUTSINE);
    }

    public KeyframeAnimation addKeyframe(float progress, Frame frame) {
        keyframes.add(new Keyframe(progress, frame));
        keyframes.sort((a, b) -> Float.compare(a.progress, b.progress));
        return this;
    }
    
    public Frame getFirstFrame() {
        if(keyframes.isEmpty())return fallback;
        return keyframes.get(0).frame;
    }

    private class Keyframe {
        public final float progress;
        public final Frame frame;

        public Keyframe(float progress, Frame frame) {
            this.progress = progress;
            this.frame = frame;
        }

        @Override
        public String toString() {
            return "Keyframe [progress=" + progress + ", frame=" + frame + "]";
        }
        
    }

}
