package de.brickforceaurora.launcher.animation.property;

public final class PropFloat {

    private final float min, max;

    private volatile float value;

    public PropFloat() {
        this(0);
    }

    public PropFloat(final float value) {
        this(value, Float.MIN_VALUE, Float.MAX_VALUE);
    }

    public PropFloat(final float min, final float max) {
        this(0, min, max);
    }

    public PropFloat(final float value, final float min, final float max) {
        this.min = min;
        this.max = max;
        this.value = clamp(value);
    }

    public float get() {
        return value;
    }

    public void set(final float value) {
        this.value = clamp(value);
    }

    public float clamp(final float value) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

}
