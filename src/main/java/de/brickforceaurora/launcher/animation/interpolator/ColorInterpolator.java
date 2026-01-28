package de.brickforceaurora.launcher.animation.interpolator;

import me.lauriichan.snowframe.util.color.SimpleColor;

public final class ColorInterpolator implements IAnimationInterpolator<SimpleColor> {

    private final SimpleColor value;

    public ColorInterpolator(final SimpleColor value) {
        this.value = value;
    }

    @Override
    public void manipulate(final SimpleColor start, final SimpleColor target, final double progress) {
        value.interpolate(start, target, progress);
    }

}
