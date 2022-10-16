package dev.tr7zw.animatedfirstperson.config.debug;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import dev.tr7zw.animatedfirstperson.AnimatedFirstPersonShared;
import dev.tr7zw.animatedfirstperson.AnimationTypes;
import dev.tr7zw.animatedfirstperson.animation.Frame;
import dev.tr7zw.animatedfirstperson.config.CustomConfigScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.Button.OnPress;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DebugScreen {

    public static Screen createDebugGui(Screen parent, Supplier<Object> config) {
        return new CustomConfigScreen(parent, "debug") {

            @Override
            public void initialize() {
                this.background = false;
                this.footer = false;
                List<OptionInstance<?>> options = new ArrayList<>();
                for (Field f : config.get().getClass().getDeclaredFields()) {
                    if (f.getType() == Float.class || f.getType() == float.class) {
                        f.setAccessible(true);
                        FloatSetting setting = f.getAnnotation(FloatSetting.class);
                        options.add(getDoubleOption(f.getName(), setting != null ? setting.min() : -1,
                                setting != null ? setting.max() : 1, setting != null ? setting.step() : 1,
                                () -> getFloat(config.get(), f), (v) -> setFloat(config.get(), f, v)));
                    }
                    if (f.getType() == Boolean.class || f.getType() == boolean.class) {
                        f.setAccessible(true);
                        options.add(getBooleanOption(f.getName(), () -> getBoolean(config.get(), f),
                                (b) -> setBoolean(config.get(), f, b)));
                    }
                }

                getOptions().addSmall(options.toArray(new OptionInstance[0]));

                this.addRenderableWidget(new Button(this.width / 2 - 100, this.height - 27, 100, 20,
                        Component.translatable("Copy Data"), new OnPress() {

                            @Override
                            public void onPress(Button button) {
                                copyData();
                            }
                        }));
                this.addRenderableWidget(new Button(this.width / 2, this.height - 27, 100, 20,
                        Component.translatable("Load Data"), new OnPress() {

                            @Override
                            public void onPress(Button button) {
                                loadData();
                                Minecraft.getInstance().setScreen(createDebugGui(parent, config));
                            }
                        }));
                this.addRenderableWidget(new Button(5, 5, 100, 20, Component.translatable("Reset"), new OnPress() {

                    @Override
                    public void onPress(Button button) {
                        try {
                            AnimatedFirstPersonShared.debugFrame = AnimatedFirstPersonShared.animationManager
                                    .getAnimationRegistry()
                                    .getAnimationSet(Minecraft.getInstance().player.getMainHandItem(),
                                            AnimationTypes.holding)
                                    .getFirst().getFirstFrame();
                        } catch (NullPointerException ex) {
                            // dirty, doesn't matter
                        }
                        if (AnimatedFirstPersonShared.debugFrame == null) {
                            AnimatedFirstPersonShared.debugFrame = new Frame();
                        }
                        Minecraft.getInstance().setScreen(createDebugGui(parent, config));
                    }
                }));

            }

            @Override
            public void save() {
            }

        };
    }

    private static void copyData() {
        String data = Arrays.toString(AnimatedFirstPersonShared.debugFrame.getData());
        System.out.println(data);
        Minecraft.getInstance().keyboardHandler.setClipboard(data);
    }

    private static void loadData() {
        try {
            String data = Minecraft.getInstance().keyboardHandler.getClipboard();
            if (data != null) {
                data = data.replace('[', ' ').replace(']', ' ').replace('\r', ' ').replace('\n', ' ');
                String[] parts = data.split(",");
                if (parts.length == 13) {
                    float[] array = new float[13];
                    for (int i = 0; i < 13; i++) {
                        array[i] = Float.parseFloat(parts[i]);
                    }
                    AnimatedFirstPersonShared.debugFrame = new Frame(array);
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
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

    private static void setBoolean(Object config, Field f, Boolean value) {
        try {
            f.setBoolean(config, value.booleanValue());
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private static Boolean getBoolean(Object config, Field f) {
        try {
            return (boolean) f.getBoolean(config);
        } catch (IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

}
