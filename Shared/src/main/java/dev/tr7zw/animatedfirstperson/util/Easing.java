package dev.tr7zw.animatedfirstperson.util;

import java.util.function.Function;

public enum Easing {
INOUTSINE((x) -> (float)-(Math.cos(Math.PI * x) - 1) / 2)
    ;
    
    private final Function<Float, Float> function;
    
    Easing(Function<Float, Float> function){
        this.function = function;
    }

    public Function<Float, Float> getFunction() {
        return function;
    }

}
