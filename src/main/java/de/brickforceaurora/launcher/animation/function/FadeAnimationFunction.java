package de.brickforceaurora.launcher.animation.function;

import java.util.concurrent.TimeUnit;

public final class FadeAnimationFunction implements IAnimationFunction {

    private volatile double fadeIn = 0d;
    private volatile double fadeOut = 0d;

    public double fadeIn() {
        return fadeIn;
    }

    public FadeAnimationFunction fadeIn(final double fadeIn) {
        this.fadeIn = fadeIn;
        return this;
    }

    public FadeAnimationFunction fadeIn(final long fadeIn, final TimeUnit unit) {
        this.fadeIn = fadeIn / (double) unit.convert(1, TimeUnit.SECONDS);
        return this;
    }

    public double fadeOut() {
        return fadeOut;
    }

    public FadeAnimationFunction fadeOut(final double fadeOut) {
        this.fadeOut = fadeOut;
        return this;
    }

    public FadeAnimationFunction fadeOut(final long fadeOut, final TimeUnit unit) {
        this.fadeOut = fadeOut / (double) unit.convert(1, TimeUnit.SECONDS);
        return this;
    }

    @Override
    public double animate(final boolean regressing, final double elapsed) {
        if (regressing) {
            if (fadeOut == 0d) {
                return 0d;
            }
            return (fadeOut - elapsed) / fadeOut;
        }
        if (fadeIn == 0d) {
            return 1d;
        }
        return elapsed / fadeIn;
    }

}
