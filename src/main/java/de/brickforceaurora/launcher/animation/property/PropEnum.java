package de.brickforceaurora.launcher.animation.property;

import java.util.Objects;

public final class PropEnum<E extends Enum<E>> {

    private volatile E value;

    public PropEnum(final E value) {
        this.value = Objects.requireNonNull(value);
    }

    public E get() {
        return value;
    }

    public void set(final E value) {
        this.value = Objects.requireNonNull(value);
    }

}
