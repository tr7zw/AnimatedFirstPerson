package dev.tr7zw.animatedfirstperson.animation;

import dev.tr7zw.animatedfirstperson.config.debug.FloatSetting;
import dev.tr7zw.animatedfirstperson.util.Easing;
import net.minecraft.util.Mth;

public class Frame {

    @FloatSetting(min = -10, max = 10, step = 0.01f)
    private float offsetX = 0.2f;
    @FloatSetting(min = -10, max = 10, step = 0.01f)
    private float offsetY = -0.02f;
    @FloatSetting(min = -10, max = 10, step = 0.01f)
    private float offsetZ = -0.13f;
    @FloatSetting(min = -100, max = 100, step = 0.5f)
    private float armAngleX = -36f;
    @FloatSetting(min = -100, max = 100, step = 0.5f)
    private float armAngleY = 55;
    @FloatSetting(min = -250, max = 250, step = 0.5f)
    private float armAngleZ = -52.5f;
    @FloatSetting(min = -250, max = 250, step = 0.5f)
    private float itemRotationX = 77;
    @FloatSetting(min = -250, max = 250, step = 0.5f)
    private float itemRotationY = 0;
    @FloatSetting(min = -250, max = 250, step = 0.5f)
    private float itemRotationZ = 180f;
    @FloatSetting(min = -1, max = 1, step = 0.01f)
    private float itemOffsetX = -0.4f;
    @FloatSetting(min = -2, max = 2, step = 0.01f)
    private float itemOffsetY = 0.65f;
    @FloatSetting(min = -1, max = 1, step = 0.01f)
    private float itemOffsetZ = -0.17f;
    @FloatSetting(min = -250, max = 250, step = 0.5f)
    private float fov = 70;

    public Frame copyFrom(Frame other) {
        this.offsetX = other.offsetX;
        this.offsetY = other.offsetY;
        this.offsetZ = other.offsetZ;
        this.armAngleY = other.armAngleY;
        this.armAngleX = other.armAngleX;
        this.armAngleZ = other.armAngleZ;
        this.itemRotationX = other.itemRotationX;
        this.itemRotationY = other.itemRotationY;
        this.itemRotationZ = other.itemRotationZ;
        this.itemOffsetX = other.itemOffsetX;
        this.itemOffsetY = other.itemOffsetY;
        this.itemOffsetZ = other.itemOffsetZ;
        this.fov = other.fov;
        return this;
    }

    public Frame lerp(Frame from, Frame to, float delta) {
        this.offsetX = Mth.lerp(delta, to.offsetX, from.offsetX);
        this.offsetY = Mth.lerp(delta, to.offsetY, from.offsetY);
        this.offsetZ = Mth.lerp(delta, to.offsetZ, from.offsetZ);
        this.armAngleY = Mth.lerp(delta, to.armAngleY, from.armAngleY);
        this.armAngleX = Mth.lerp(delta, to.armAngleX, from.armAngleX);
        this.armAngleZ = Mth.lerp(delta, to.armAngleZ, from.armAngleZ);
        this.itemRotationX = Mth.lerp(delta, to.itemRotationX, from.itemRotationX);
        this.itemRotationY = Mth.lerp(delta, to.itemRotationY, from.itemRotationY);
        this.itemRotationZ = Mth.lerp(delta, to.itemRotationZ, from.itemRotationZ);
        this.itemOffsetX = Mth.lerp(delta, to.itemOffsetX, from.itemOffsetX);
        this.itemOffsetY = Mth.lerp(delta, to.itemOffsetY, from.itemOffsetY);
        this.itemOffsetZ = Mth.lerp(delta, to.itemOffsetZ, from.itemOffsetZ);
        this.fov = Mth.lerp(delta, to.fov, from.fov);
        return this;
    }

    public Frame createFrame(Frame from, Frame to, float progress, Easing easing) {
        this.offsetX = ease(easing, progress, from.offsetX, to.offsetX);
        this.offsetY = ease(easing, progress, from.offsetY, to.offsetY);
        this.offsetZ = ease(easing, progress, from.offsetZ, to.offsetZ);
        this.armAngleY = ease(easing, progress, from.armAngleY, to.armAngleY);
        this.armAngleX = ease(easing, progress, from.armAngleX, to.armAngleX);
        this.armAngleZ = ease(easing, progress, from.armAngleZ, to.armAngleZ);
        this.itemRotationX = ease(easing, progress, from.itemRotationX, to.itemRotationX);
        this.itemRotationY = ease(easing, progress, from.itemRotationY, to.itemRotationY);
        this.itemRotationZ = ease(easing, progress, from.itemRotationZ, to.itemRotationZ);
        this.itemOffsetX = ease(easing, progress, from.itemOffsetX, to.itemOffsetX);
        this.itemOffsetY = ease(easing, progress, from.itemOffsetY, to.itemOffsetY);
        this.itemOffsetZ = ease(easing, progress, from.itemOffsetZ, to.itemOffsetZ);
        this.fov = ease(easing, progress, from.fov, to.fov);
        return this;
    }

    private float ease(Easing easing, float progress, float from, float to) {
        float pos = easing.getFunction().apply(progress);
        return (to - from) * pos + from;
    }

    public float getOffsetX() {
        return offsetX;
    }

    public float getOffsetY() {
        return offsetY;
    }

    public float getOffsetZ() {
        return offsetZ;
    }

    public float getArmAngleY() {
        return armAngleY;
    }

    public float getArmAngleX() {
        return armAngleX;
    }

    public float getArmAngleZ() {
        return armAngleZ;
    }

    public float getItemRotationX() {
        return itemRotationX;
    }

    public float getItemRotationY() {
        return itemRotationY;
    }

    public float getItemRotationZ() {
        return itemRotationZ;
    }

    public float getItemOffsetX() {
        return itemOffsetX;
    }

    public float getItemOffsetY() {
        return itemOffsetY;
    }

    public float getItemOffsetZ() {
        return itemOffsetZ;
    }

    public float getFov() {
        return fov;
    }

    public void setOffsetX(float offsetX) {
        this.offsetX = offsetX;
    }

    public void setOffsetY(float offsetY) {
        this.offsetY = offsetY;
    }

    public void setOffsetZ(float offsetZ) {
        this.offsetZ = offsetZ;
    }

    public void setArmAngle(float armAngle) {
        this.armAngleY = armAngle;
    }

    public void setArmAngleX(float armAngleX) {
        this.armAngleX = armAngleX;
    }

    public void setArmAngleZ(float armAngleZ) {
        this.armAngleZ = armAngleZ;
    }

    public void setItemRotationX(float itemRotationX) {
        this.itemRotationX = itemRotationX;
    }

    public void setItemRotationY(float itemRotationY) {
        this.itemRotationY = itemRotationY;
    }

    public void setItemRotationZ(float itemRotationZ) {
        this.itemRotationZ = itemRotationZ;
    }

    public void setItemOffsetX(float itemOffsetX) {
        this.itemOffsetX = itemOffsetX;
    }

    public void setItemOffsetY(float itemOffsetY) {
        this.itemOffsetY = itemOffsetY;
    }

    public void setItemOffsetZ(float itemOffsetZ) {
        this.itemOffsetZ = itemOffsetZ;
    }

    public void setFov(float fov) {
        this.fov = fov;
    }

}
