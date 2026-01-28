package de.brickforceaurora.launcher.animation;

import java.util.Objects;

import de.brickforceaurora.launcher.animation.animator.IAnimationAnimator;
import de.brickforceaurora.launcher.animation.function.IAnimationFunction;
import de.brickforceaurora.launcher.animation.trigger.IAnimationTrigger;
import it.unimi.dsi.fastutil.objects.ReferenceArrayList;
import it.unimi.dsi.fastutil.objects.ReferenceList;
import it.unimi.dsi.fastutil.objects.ReferenceLists;

public final class Animation {

    public static Animation.Builder builder() {
        return new Animation.Builder();
    }

    @FunctionalInterface
    public interface IAnimationConsumer<T> {

        void accept(Animation animation, T value);

    }

    public static class Builder {

        private IAnimationTrigger trigger;
        private IAnimationFunction function;

        private final ReferenceArrayList<IAnimationAnimator> animators = new ReferenceArrayList<>();

        private boolean repeating, regressionEnabled = true;

        private IAnimationConsumer<Boolean> onRestart, onActiveChanged;
        private IAnimationConsumer<Boolean> onDone;
        private IAnimationConsumer<Double> onTick;

        public IAnimationTrigger trigger() {
            return trigger;
        }

        public Builder trigger(final IAnimationTrigger trigger) {
            this.trigger = trigger;
            return this;
        }

        public IAnimationFunction function() {
            return function;
        }

        public Builder function(final IAnimationFunction function) {
            this.function = function;
            return this;
        }

        public Builder animator(final IAnimationAnimator animator) {
            Objects.requireNonNull(animator);
            animators.add(animator);
            return this;
        }

        public Builder animators(final IAnimationAnimator[] animators) {
            for (final IAnimationAnimator animator : animators) {
                animator(animator);
            }
            return this;
        }

        public Builder clearAnimators() {
            animators.clear();
            return this;
        }

        public boolean repeating() {
            return repeating;
        }

        public Builder repeating(final boolean repeating) {
            this.repeating = repeating;
            return this;
        }

        public boolean regressionEnabled() {
            return regressionEnabled;
        }

        public Builder regressionEnabled(final boolean regressionEnabled) {
            this.regressionEnabled = regressionEnabled;
            return this;
        }

        public IAnimationConsumer<Boolean> onRestart() {
            return onRestart;
        }

        public Builder onRestart(final IAnimationConsumer<Boolean> onRestart) {
            this.onRestart = onRestart;
            return this;
        }

        public IAnimationConsumer<Boolean> onActiveChanged() {
            return onActiveChanged;
        }

        public Builder onActiveChanged(final IAnimationConsumer<Boolean> onActiveChanged) {
            this.onActiveChanged = onActiveChanged;
            return this;
        }

        public IAnimationConsumer<Boolean> onDone() {
            return onDone;
        }

        public Builder onDone(final IAnimationConsumer<Boolean> onDone) {
            this.onDone = onDone;
            return this;
        }

        public IAnimationConsumer<Double> onTick() {
            return onTick;
        }

        public Builder onTick(final IAnimationConsumer<Double> onTick) {
            this.onTick = onTick;
            return this;
        }

        public Animation build() {
            return new Animation(trigger, function, animators, repeating, regressionEnabled, onRestart, onActiveChanged, onDone, onTick);
        }

    }

    private final IAnimationTrigger trigger;
    private final IAnimationFunction function;

    private final ReferenceList<IAnimationAnimator> animators;

    private final boolean repeating, regressionEnabled;

    private final IAnimationConsumer<Boolean> onActiveChanged, onDone, onRestart;
    private final IAnimationConsumer<Double> onTick;

    private volatile boolean active = false, done = true, regressing = false;
    private volatile double progress = 0d, elapsed = 0d;

    private Animation(final IAnimationTrigger trigger, final IAnimationFunction function,
        final ReferenceArrayList<IAnimationAnimator> animators, final boolean repeating, final boolean regressionEnabled,
        final IAnimationConsumer<Boolean> onRestart, final IAnimationConsumer<Boolean> onActiveChanged,
        final IAnimationConsumer<Boolean> onDone, final IAnimationConsumer<Double> onTick) {
        this.trigger = Objects.requireNonNull(trigger);
        this.function = Objects.requireNonNull(function);
        this.animators = animators.isEmpty() ? ReferenceLists.emptyList()
            : ReferenceLists.unmodifiable(new ReferenceArrayList<>(animators));
        this.repeating = repeating;
        this.regressionEnabled = regressionEnabled;
        this.onRestart = onRestart;
        this.onActiveChanged = onActiveChanged;
        this.onDone = onDone;
        this.onTick = onTick;
    }

    public void trigger() {
        trigger(0f, 0f, 0f, 0f);
    }

    public void trigger(final float gx, final float gy, final float width, final float height) {
        final boolean active = trigger.isTriggered(gx, gy, width, height);
        if (this.active == active) {
            return;
        }
        this.active = active;
        if (regressing == active) {
            this.regressing = !active;
        }
        if (onActiveChanged != null) {
            onActiveChanged.accept(this, active);
        }
        this.elapsed = 0d;
        this.done = false;
    }

    public boolean isRepeating() {
        return repeating;
    }

    public boolean isRegressing() {
        return regressing;
    }

    public boolean isActive() {
        return active;
    }

    public boolean isDone() {
        return done;
    }

    public double progress() {
        return progress;
    }

    public double elapsed() {
        return elapsed;
    }

    public void update(final double delta) {
        if (done) {
            return;
        }
        if ((regressing ? progress == 0d : progress == 1d)) {
            if (!regressing && !regressionEnabled) {
                progress = 0d;
            } else {
                regressing = !regressing;
            }
            if (!active || !repeating) {
                done = true;
                if (onDone != null) {
                    onDone.accept(this, regressing);
                }
                return;
            }
            elapsed = 0d;
            if (onRestart != null) {
                onRestart.accept(this, regressing);
            }
        }
        progress = Math.max(Math.min(function.animate(regressing, elapsed), 1d), 0d);
        if (onTick != null) {
            onTick.accept(this, delta);
        }
        elapsed += delta;
        for (final IAnimationAnimator animator : animators) {
            animator.animate(regressing, progress);
        }
    }

}
