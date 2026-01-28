package de.brickforceaurora.launcher.animation.interpolator;

import de.brickforceaurora.launcher.animation.property.PropInt;

public final class IntInterpolator implements IAnimationInterpolator<Integer> {

    private final PropInt value;

    public IntInterpolator(final PropInt value) {
        this.value = value;
    }

    @Override
    public void manipulate(final Integer start, final Integer end, final double progress) {
        value.set((int) (start.intValue() * (1 - progress) + end.intValue() * progress));
    }

}
