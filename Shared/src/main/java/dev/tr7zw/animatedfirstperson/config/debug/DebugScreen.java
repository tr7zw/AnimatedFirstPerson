package dev.tr7zw.animatedfirstperson.config.debug;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import dev.tr7zw.animatedfirstperson.config.CustomConfigScreen;
import net.minecraft.client.Option;
import net.minecraft.client.gui.screens.Screen;

public class DebugScreen {

    public static Screen createDebugGui(Screen parent, Object config) {
        return new CustomConfigScreen(parent, "debug") {

            @Override
            public void initialize() {
                this.background = false;
                this.footer = false;
                List<Option> options = new ArrayList<>();
                for(Field f : config.getClass().getDeclaredFields()) {
                    if(f.getType() == Float.class || f.getType() == float.class) {
                        FloatSetting setting = f.getAnnotation(FloatSetting.class);
                        options.add(getDoubleOption(f.getName(), setting != null ? setting.min() : -1, setting != null ? setting.max() : 1, setting != null ? setting.step() : 1, () -> getFloat(config, f), (v) -> setFloat(config, f, v)));
                    }
                }

                getOptions().addSmall(options.toArray(new Option[0]));

            }

            @Override
            public void save() {
            }
            
        };
    }
    
    private static void setFloat(Object config, Field f, Double value) {
        try {
            f.setFloat(config, value.floatValue());
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
    
    private static Double getFloat(Object config, Field f) {
        try {
            return (double) f.getFloat(config);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return 0d;
    }
    
}
