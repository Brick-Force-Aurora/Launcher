package de.brickforceaurora.launcher.animation.property;

public final class PropLong {

    private final long min, max;

    private volatile long value;

    public PropLong() {
        this(0);
    }

    public PropLong(final long value) {
        this(value, Long.MIN_VALUE, Long.MAX_VALUE);
    }

    public PropLong(final long min, final long max) {
        this(0, min, max);
    }

    public PropLong(final long value, final long min, final long max) {
        this.min = min;
        this.max = max;
        this.value = clamp(value);
    }

    public void flag(final long flag, final boolean state) {
        if (state) {
            value |= flag;
            return;
        }
        value &= ~flag;
    }

    public boolean flag(final long flag) {
        return (value & flag) == flag;
    }

    public long get() {
        return value;
    }

    public void set(final long value) {
        this.value = clamp(value);
    }

    public long clamp(final long value) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

}
