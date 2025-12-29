package de.brickforceaurora.launcher.animation.property;

public final class PropShort {

    private final short min, max;

    private volatile short value;

    public PropShort() {
        this((short) 0);
    }

    public PropShort(short value) {
        this(value, Short.MIN_VALUE, Short.MAX_VALUE);
    }

    public PropShort(short min, short max) {
        this((short) 0, min, max);
    }

    public PropShort(short value, short min, short max) {
        this.min = min;
        this.max = max;
        this.value = clamp(value);
    }

    public void flag(short flag, boolean state) {
        if (state) {
            value |= flag;
            return;
        }
        value &= ~flag;
    }

    public boolean flag(short flag) {
        return (value & flag) == flag;
    }

    public short get() {
        return value;
    }

    public void set(short value) {
        this.value = clamp(value);
    }

    public short clamp(short value) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }

}
