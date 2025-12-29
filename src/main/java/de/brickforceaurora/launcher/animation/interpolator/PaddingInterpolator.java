package de.brickforceaurora.launcher.animation.interpolator;

import de.brickforceaurora.launcher.animation.property.PropPadding;

public final class PaddingInterpolator implements IAnimationInterpolator<Integer> {

    private final PropPadding value;

    public PaddingInterpolator(final PropPadding value) {
        this.value = value;
    }

    @Override
    public void manipulate(Integer start, Integer end, double progress) {
        value.set((int) Math.round(start.floatValue() * (1 - progress) + end.floatValue() * progress));
    }

}
