package de.brickforceaurora.launcher.animation.property;

public final class PropShort {

    private final short min, max;

    private volatile short value;

    public PropShort() {
        this((short) 0);
    }

    public PropShort(final short value) {
        this(value, Short.MIN_VALUE, Short.MAX_VALUE);
    }

    public PropShort(final short min, final short max) {
        this((short) 0, min, max);
    }

    public PropShort(final short value, final short min, final short max) {
        this.min = min;
        this.max = max;
        this.value = clamp(value);
    }

    public void flag(final short flag, final boolean state) {
        if (state) {
            value |= flag;
            return;
        }
        value &= ~flag;
    }

    public boolean flag(final short flag) {
        return (value & flag) == flag;
    }

    public short get() {
        return value;
    }

    public void set(final short value) {
        this.value = clamp(value);
    }

    public short clamp(final short value) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

}
