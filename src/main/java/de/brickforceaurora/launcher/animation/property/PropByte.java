package de.brickforceaurora.launcher.animation.property;

public final class PropByte {

    private final byte min, max;

    private volatile byte value;

    public PropByte() {
        this((byte) 0);
    }

    public PropByte(final byte value) {
        this(value, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }

    public PropByte(final byte min, final byte max) {
        this((byte) 0, min, max);
    }

    public PropByte(final byte value, final byte min, final byte max) {
        this.min = min;
        this.max = max;
        this.value = clamp(value);
    }

    public void flag(final byte flag, final boolean state) {
        if (state) {
            value |= flag;
            return;
        }
        value &= ~flag;
    }

    public boolean flag(final byte flag) {
        return (value & flag) == flag;
    }

    public byte get() {
        return value;
    }

    public void set(final byte value) {
        this.value = clamp(value);
    }

    public byte clamp(final byte value) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

}
