package dev.tr7zw.animatedfirstperson.config.debug;

public class DebugConfig {

    @FloatSetting(min = -100, max = 100, step = 0.5f)
    public float offsetX = -1;
    @FloatSetting(min = -100, max = 100, step = 0.5f)
    public float offsetY = -0.5f;
    @FloatSetting(min = -100, max = 100, step = 0.5f)
    public float offsetZ = -5;
    @FloatSetting(min = -100, max = 100, step = 0.5f)
    public float armAngle = 55;
    @FloatSetting(min = -100, max = 100, step = 0.5f)
    public float armAngleX = 42.5f;
    @FloatSetting(min = -100, max = 100, step = 0.5f)
    public float armAngleZ = -52.5f;
    @FloatSetting(min = -100, max = 100, step = 0.5f)
    public float itemRotationX = 6;
    @FloatSetting(min = -100, max = 100, step = 0.5f)
    public float itemRotationZ = -4.5f;
    @FloatSetting(min = -1, max = 1, step = 0.01f)
    public float itemOffsetX = -0.05f;
    @FloatSetting(min = -1, max = 1, step = 0.01f)
    public float itemOffsetY = 0.17f;
    @FloatSetting(min = -1, max = 1, step = 0.01f)
    public float itemOffsetZ = -0.11f;
}
