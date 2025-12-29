package de.brickforceaurora.launcher.animation.function;

import java.util.concurrent.TimeUnit;

public final class FadeAnimationFunction implements IAnimationFunction {

    private volatile double fadeIn = 0d;
    private volatile double fadeOut = 0d;

    public double fadeIn() {
        return fadeIn;
    }

    public FadeAnimationFunction fadeIn(double fadeIn) {
        this.fadeIn = fadeIn;
        return this;
    }

    public FadeAnimationFunction fadeIn(long fadeIn, TimeUnit unit) {
        this.fadeIn = fadeIn / (double) unit.convert(1, TimeUnit.SECONDS);
        return this;
    }

    public double fadeOut() {
        return fadeOut;
    }

    public FadeAnimationFunction fadeOut(double fadeOut) {
        this.fadeOut = fadeOut;
        return this;
    }

    public FadeAnimationFunction fadeOut(long fadeOut, TimeUnit unit) {
        this.fadeOut = fadeOut / (double) unit.convert(1, TimeUnit.SECONDS);
        return this;
    }

    @Override
    public double animate(boolean regressing, double elapsed) {
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
