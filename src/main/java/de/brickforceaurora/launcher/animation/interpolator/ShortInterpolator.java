package de.brickforceaurora.launcher.animation.interpolator;

import de.brickforceaurora.launcher.animation.property.PropShort;

public final class ShortInterpolator implements IAnimationInterpolator<Short> {

    private final PropShort value;

    public ShortInterpolator(final PropShort value) {
        this.value = value;
    }

    @Override
    public void manipulate(Short start, Short end, double progress) {
        value.set((short) (start.shortValue() * (1 - progress) + end.shortValue() * progress));
    }

}
