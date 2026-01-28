package de.brickforceaurora.launcher.animation.trigger;

public interface IAnimationTrigger {

    static HoverTrigger hover() {
        return HoverTrigger.INSTANCE;
    }

    static MouseDownTrigger mouseDownLeft() {
        return MouseDownTrigger.LEFT;
    }

    static MouseDownTrigger mouseDownMiddle() {
        return MouseDownTrigger.MIDDLE;
    }

    static MouseDownTrigger mouseDownRight() {
        return MouseDownTrigger.RIGHT;
    }

    static DelegateTrigger delegate() {
        return new DelegateTrigger();
    }

    boolean isTriggered(float gx, float gy, float width, float height);

}
