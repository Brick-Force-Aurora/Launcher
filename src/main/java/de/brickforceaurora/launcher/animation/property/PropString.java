package de.brickforceaurora.launcher.animation.property;

import java.util.Objects;

public final class PropString {

    private volatile String value = "";

    public PropString() {}

    public PropString(String value) {
        this.value = Objects.requireNonNull(value);
    }

    public String get() {
        return value;
    }

    public void set(String value) {
        this.value = Objects.requireNonNull(value);
    }

}
