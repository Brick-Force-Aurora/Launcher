package de.brickforceaurora.launcher.animation.property;

public final class PropLong {

    private final long min, max;

    private volatile long value;

    public PropLong() {
        this(0);
    }

    public PropLong(long value) {
        this(value, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public PropLong(long min, long max) {
        this(0, min, max);
    }

    public PropLong(long value, long min, long max) {
        this.min = min;
        this.max = max;
        this.value = clamp(value);
    }
    
    public void flag(long flag, boolean state) {
        if (state) {
            value |= flag;
            return;
        }
        value &= ~flag;
    }
    
    public boolean flag(long flag) {
        return (value & flag) == flag;
    }

    public long get() {
        return value;
    }

    public void set(long value) {
        this.value = clamp(value);
    }

    public long clamp(long value) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }

}
