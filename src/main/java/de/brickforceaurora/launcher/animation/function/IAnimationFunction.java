package de.brickforceaurora.launcher.animation.function;

public interface IAnimationFunction {

    static EaseAnimationFunction ease() {
        return new EaseAnimationFunction();
    }

    static FadeAnimationFunction fade() {
        return new FadeAnimationFunction();
    }

    double animate(boolean regressing, double elapsed);

}
