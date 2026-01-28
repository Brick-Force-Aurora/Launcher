package de.brickforceaurora.launcher.animation.interpolator;

import de.brickforceaurora.launcher.animation.property.PropLong;

public final class LongInterpolator implements IAnimationInterpolator<Long> {

    private final PropLong value;

    public LongInterpolator(final PropLong value) {
        this.value = value;
    }

    @Override
    public void manipulate(final Long start, final Long end, final double progress) {
        value.set((long) (start.longValue() * (1 - progress) + end.longValue() * progress));
    }

}
