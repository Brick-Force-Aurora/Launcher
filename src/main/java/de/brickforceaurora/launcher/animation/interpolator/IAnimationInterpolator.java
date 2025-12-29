package de.brickforceaurora.launcher.animation.interpolator;

import de.brickforceaurora.launcher.animation.property.*;
import me.lauriichan.snowframe.util.color.SimpleColor;

public interface IAnimationInterpolator<T> {

    static ColorInterpolator of(SimpleColor color) {
        return new ColorInterpolator(color);
    }

    static BoolInterpolator of(PropBool prop) {
        return new BoolInterpolator(prop);
    }

    static BoolInterpolator of(PropBool prop, double threshold) {
        return new BoolInterpolator(prop, threshold);
    }

    static ByteInterpolator of(PropByte prop) {
        return new ByteInterpolator(prop);
    }

    static ShortInterpolator of(PropShort prop) {
        return new ShortInterpolator(prop);
    }

    static IntInterpolator of(PropInt prop) {
        return new IntInterpolator(prop);
    }

    static LongInterpolator of(PropLong prop) {
        return new LongInterpolator(prop);
    }

    static FloatInterpolator of(PropFloat prop) {
        return new FloatInterpolator(prop);
    }

    static DoubleInterpolator of(PropDouble prop) {
        return new DoubleInterpolator(prop);
    }

    static PaddingInterpolator of(PropPadding prop) {
        return new PaddingInterpolator(prop);
    }

    void manipulate(T start, T end, double progress);

}
