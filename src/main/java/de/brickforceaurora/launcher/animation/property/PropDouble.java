package de.brickforceaurora.launcher.animation.property;

public final class PropDouble {

    private final double min, max;

    private volatile double value;

    public PropDouble() {
        this(0);
    }

    public PropDouble(final double value) {
        this(value, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public PropDouble(final double min, final double max) {
        this(0, min, max);
    }

    public PropDouble(final double value, final double min, final double max) {
        this.min = min;
        this.max = max;
        this.value = clamp(value);
    }

    public double get() {
        return value;
    }

    public void set(final double value) {
        this.value = clamp(value);
    }

    public double clamp(final double value) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

}
