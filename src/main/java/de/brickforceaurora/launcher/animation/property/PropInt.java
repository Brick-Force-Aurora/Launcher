package de.brickforceaurora.launcher.animation.property;

public final class PropInt {

    private final int min, max;

    private volatile int value;

    public PropInt() {
        this(0);
    }

    public PropInt(int value) {
        this(value, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public PropInt(int min, int max) {
        this(0, min, max);
    }

    public PropInt(int value, int min, int max) {
        this.min = min;
        this.max = max;
        this.value = clamp(value);
    }

    public void flag(int flag, boolean state) {
        if (state) {
            value |= flag;
            return;
        }
        value &= ~flag;
    }

    public boolean flag(int flag) {
        return (value & flag) == flag;
    }

    public int get() {
        return value;
    }

    public void set(int value) {
        this.value = clamp(value);
    }

    public int clamp(int value) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }

}
