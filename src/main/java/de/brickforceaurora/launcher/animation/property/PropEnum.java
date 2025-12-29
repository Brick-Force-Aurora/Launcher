package de.brickforceaurora.launcher.animation.property;

import java.util.Objects;

public final class PropEnum<E extends Enum<E>> {
    
    private volatile E value;

    public PropEnum(E value) {
        this.value = Objects.requireNonNull(value);
    }

    public E get() {
        return value;
    }

    public void set(E value) {
        this.value = Objects.requireNonNull(value);
    }

}
