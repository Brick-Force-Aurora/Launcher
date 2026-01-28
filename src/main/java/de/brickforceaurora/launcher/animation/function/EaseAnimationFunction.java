package de.brickforceaurora.launcher.animation.function;

import java.util.concurrent.TimeUnit;

public final class EaseAnimationFunction implements IAnimationFunction {

    private volatile double easeIn = 0d;
    private volatile double easeOut = 0d;

    public double easeIn() {
        return easeIn;
    }

    public EaseAnimationFunction easeIn(final double easeIn) {
        this.easeIn = easeIn;
        return this;
    }

    public EaseAnimationFunction easeIn(final long easeIn, final TimeUnit unit) {
        this.easeIn = easeIn / (double) unit.convert(1, TimeUnit.SECONDS);
        return this;
    }

    public double easeOut() {
        return easeOut;
    }

    public EaseAnimationFunction easeOut(final double easeOut) {
        this.easeOut = easeOut;
        return this;
    }

    public EaseAnimationFunction easeOut(final long easeOut, final TimeUnit unit) {
        this.easeOut = easeOut / (double) unit.convert(1, TimeUnit.SECONDS);
        return this;
    }

    @Override
    public double animate(final boolean regressing, final double elapsed) {
        if (regressing) {
            if (easeOut == 0d) {
                return 0d;
            }
            return ease((easeOut - elapsed) / easeOut);
        }
        if (easeIn == 0d) {
            return 1d;
        }
        return ease(elapsed / easeIn);
    }

    private double ease(final double progress) {
        if (progress > 1d) {
            return 1d;
        }
        if (progress < 0d) {
            return 0d;
        }
        return -(Math.cos(Math.PI * progress) - 1) / 2d;
    }

}
