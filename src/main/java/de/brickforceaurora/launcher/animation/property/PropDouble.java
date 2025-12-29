package de.brickforceaurora.launcher.animation.property;

public final class PropDouble {
    
    private final double min, max;

    private volatile double value;

    public PropDouble() {
        this(0);
    }

    public PropDouble(double value) {
        this(value, Double.MIN_VALUE, Double.MAX_VALUE);
    }

    public PropDouble(double min, double max) {
        this(0, min, max);
    }

    public PropDouble(double value, double min, double max) {
        this.min = min;
        this.max = max;
        this.value = clamp(value);
    }

    public double get() {
        return value;
    }

    public void set(double value) {
        this.value = clamp(value);
    }

    public double clamp(double value) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }

}
