package dev.tr7zw.animatedfirstperson.util;

import java.util.function.Function;

public enum Easing {
LINEAR((x) -> x),
INOUTSINE((x) -> (float)-(Math.cos(Math.PI * x) - 1) / 2),
INCUBIC((x) -> x * x * x),
INOUTCUBIC((x) -> (float)(x < 0.5 ? 4 * x * x * x : 1 - Math.pow(-2 * x + 2, 3) / 2)),
SMOOTHSTEP((x) -> (3*x*x)-(2*x*x*x)),
    ;
    
    private final Function<Float, Float> function;
    
    Easing(Function<Float, Float> function){
        this.function = function;
    }

    public Function<Float, Float> getFunction() {
        return function;
    }

}
