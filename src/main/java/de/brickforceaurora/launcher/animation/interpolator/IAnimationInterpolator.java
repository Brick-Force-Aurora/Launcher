package de.brickforceaurora.launcher.animation.interpolator;

import de.brickforceaurora.launcher.animation.property.PropBool;
import de.brickforceaurora.launcher.animation.property.PropByte;
import de.brickforceaurora.launcher.animation.property.PropDouble;
import de.brickforceaurora.launcher.animation.property.PropFloat;
import de.brickforceaurora.launcher.animation.property.PropInt;
import de.brickforceaurora.launcher.animation.property.PropLong;
import de.brickforceaurora.launcher.animation.property.PropPadding;
import de.brickforceaurora.launcher.animation.property.PropShort;
import me.lauriichan.snowframe.util.color.SimpleColor;

public interface IAnimationInterpolator<T> {

    static ColorInterpolator of(final SimpleColor color) {
        return new ColorInterpolator(color);
    }

    static BoolInterpolator of(final PropBool prop) {
        return new BoolInterpolator(prop);
    }

    static BoolInterpolator of(final PropBool prop, final double threshold) {
        return new BoolInterpolator(prop, threshold);
    }

    static ByteInterpolator of(final PropByte prop) {
        return new ByteInterpolator(prop);
    }

    static ShortInterpolator of(final PropShort prop) {
        return new ShortInterpolator(prop);
    }

    static IntInterpolator of(final PropInt prop) {
        return new IntInterpolator(prop);
    }

    static LongInterpolator of(final PropLong prop) {
        return new LongInterpolator(prop);
    }

    static FloatInterpolator of(final PropFloat prop) {
        return new FloatInterpolator(prop);
    }

    static DoubleInterpolator of(final PropDouble prop) {
        return new DoubleInterpolator(prop);
    }

    static PaddingInterpolator of(final PropPadding prop) {
        return new PaddingInterpolator(prop);
    }

    void manipulate(T start, T end, double progress);

}
