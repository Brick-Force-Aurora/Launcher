package de.brickforceaurora.launcher.animation.interpolator;

import de.brickforceaurora.launcher.animation.property.PropFloat;

public final class FloatInterpolator implements IAnimationInterpolator<Float> {

    private final PropFloat value;

    public FloatInterpolator(final PropFloat value) {
        this.value = value;
    }

    @Override
    public void manipulate(Float start, Float end, double progress) {
        value.set((float) (start.floatValue() * (1 - progress) + end.floatValue() * progress));
    }

}
