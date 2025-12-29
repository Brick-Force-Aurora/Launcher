package de.brickforceaurora.launcher.animation.interpolator;

import de.brickforceaurora.launcher.animation.property.PropByte;

public final class ByteInterpolator implements IAnimationInterpolator<Byte> {

    private final PropByte value;

    public ByteInterpolator(final PropByte value) {
        this.value = value;
    }

    @Override
    public void manipulate(Byte start, Byte end, double progress) {
        value.set((byte) (start.byteValue() * (1 - progress) + end.byteValue() * progress));
    }

}
