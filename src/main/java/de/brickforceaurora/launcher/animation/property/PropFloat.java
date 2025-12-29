package de.brickforceaurora.launcher.animation.property;

public final class PropFloat {
    
    private final float min, max;

    private volatile float value;

    public PropFloat() {
        this(0);
    }

    public PropFloat(float value) {
        this(value, Float.MIN_VALUE, Float.MAX_VALUE);
    }

    public PropFloat(float min, float max) {
        this(0, min, max);
    }

    public PropFloat(float value, float min, float max) {
        this.min = min;
        this.max = max;
        this.value = clamp(value);
    }

    public float get() {
        return value;
    }

    public void set(float value) {
        this.value = clamp(value);
    }

    public float clamp(float value) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }

}
