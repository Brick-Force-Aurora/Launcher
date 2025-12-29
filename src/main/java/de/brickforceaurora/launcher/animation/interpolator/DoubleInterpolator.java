package de.brickforceaurora.launcher.animation.interpolator;

import de.brickforceaurora.launcher.animation.property.PropDouble;

public final class DoubleInterpolator implements IAnimationInterpolator<Double> {

    private final PropDouble value;

    public DoubleInterpolator(final PropDouble value) {
        this.value = value;
    }

    @Override
    public void manipulate(Double start, Double end, double progress) {
        value.set(start.doubleValue() * (1 - progress) + end.doubleValue() * progress);
    }

}
