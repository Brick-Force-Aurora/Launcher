package de.brickforceaurora.launcher.animation.animator;

import java.util.Objects;

import de.brickforceaurora.launcher.animation.interpolator.IAnimationInterpolator;

public final class InterpolationAnimator<T> implements IAnimationAnimator {

    public static <T> InterpolationAnimator.Builder<T> builder() {
        return new InterpolationAnimator.Builder<>();
    }

    public static class Builder<C> {

        private IAnimationInterpolator<C> interpolator;
        private C start, end;

        public IAnimationInterpolator<C> interpolator() {
            return interpolator;
        }

        public Builder<C> interpolator(IAnimationInterpolator<C> interpolator) {
            this.interpolator = interpolator;
            return this;
        }

        public C start() {
            return start;
        }

        public Builder<C> start(C start) {
            this.start = start;
            return this;
        }

        public C end() {
            return end;
        }

        public Builder<C> end(C end) {
            this.end = end;
            return this;
        }

        public InterpolationAnimator<C> build() {
            return new InterpolationAnimator<>(interpolator, start, end);
        }
        
    }

    private final IAnimationInterpolator<T> interpolator;
    private final T start, end;

    private InterpolationAnimator(IAnimationInterpolator<T> interpolator, T start, T end) {
        this.interpolator = Objects.requireNonNull(interpolator);
        this.start = Objects.requireNonNull(start);
        this.end = Objects.requireNonNull(end);
    }
    
    @Override
    public void animate(boolean regressing, double progress) {
        interpolator.manipulate(start, end, progress);
    }

}
