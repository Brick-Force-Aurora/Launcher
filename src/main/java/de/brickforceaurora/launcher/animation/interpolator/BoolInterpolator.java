package de.brickforceaurora.launcher.animation.interpolator;

import de.brickforceaurora.launcher.animation.property.PropBool;

public final class BoolInterpolator implements IAnimationInterpolator<Boolean> {

    private final PropBool value;
    private final double threshold;

    public BoolInterpolator(final PropBool value) {
        this(value, 1d);
    }

    public BoolInterpolator(final PropBool value, final double threshold) {
        this.value = value;
        this.threshold = Math.max(Math.min(threshold, 1d), 0d);
    }

    @Override
    public void manipulate(Boolean start, Boolean end, double progress) {
        value.set(progress >= threshold ? end : start);
    }

}
