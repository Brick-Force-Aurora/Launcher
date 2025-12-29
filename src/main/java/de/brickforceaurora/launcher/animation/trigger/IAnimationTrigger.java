package de.brickforceaurora.launcher.animation.trigger;

public interface IAnimationTrigger {

    public static HoverTrigger hover() {
        return HoverTrigger.INSTANCE;
    }

    public static MouseDownTrigger mouseDownLeft() {
        return MouseDownTrigger.LEFT;
    }

    public static MouseDownTrigger mouseDownMiddle() {
        return MouseDownTrigger.MIDDLE;
    }

    public static MouseDownTrigger mouseDownRight() {
        return MouseDownTrigger.RIGHT;
    }

    public static DelegateTrigger delegate() {
        return new DelegateTrigger();
    }

    boolean isTriggered(float gx, float gy, float width, float height);

}
