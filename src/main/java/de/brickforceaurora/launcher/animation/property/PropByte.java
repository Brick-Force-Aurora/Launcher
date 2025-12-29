package de.brickforceaurora.launcher.animation.property;

public final class PropByte {

    private final byte min, max;

    private volatile byte value;

    public PropByte() {
        this((byte) 0);
    }

    public PropByte(byte value) {
        this(value, Byte.MIN_VALUE, Byte.MAX_VALUE);
    }

    public PropByte(byte min, byte max) {
        this((byte) 0, min, max);
    }

    public PropByte(byte value, byte min, byte max) {
        this.min = min;
        this.max = max;
        this.value = clamp(value);
    }

    public void flag(byte flag, boolean state) {
        if (state) {
            value |= flag;
            return;
        }
        value &= ~flag;
    }

    public boolean flag(byte flag) {
        return (value & flag) == flag;
    }

    public byte get() {
        return value;
    }

    public void set(byte value) {
        this.value = clamp(value);
    }

    public byte clamp(byte value) {
        if (value < min) {
            return min;
        } else if (value > max) {
            return max;
        }
        return value;
    }

}
