package de.brickforceaurora.launcher.animation.property;

public final class PropInt {

    private final int min, max;

    private volatile int value;

    public PropInt() {
        this(0);
    }

    public PropInt(final int value) {
        this(value, Integer.MIN_VALUE, Integer.MAX_VALUE);
    }

    public PropInt(final int min, final int max) {
        this(0, min, max);
    }

    public PropInt(final int value, final int min, final int max) {
        this.min = min;
        this.max = max;
        this.value = clamp(value);
    }

    public void flag(final int flag, final boolean state) {
        if (state) {
            value |= flag;
            return;
        }
        value &= ~flag;
    }

    public boolean flag(final int flag) {
        return (value & flag) == flag;
    }

    public int get() {
        return value;
    }

    public void set(final int value) {
        this.value = clamp(value);
    }

    public int clamp(final int value) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

}
