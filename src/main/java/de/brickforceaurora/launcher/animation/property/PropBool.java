package de.brickforceaurora.launcher.animation.property;

public final class PropBool {

    private volatile boolean value = false;

    public PropBool() {}

    public PropBool(final boolean value) {
        this.value = value;
    }

    public boolean get() {
        return value;
    }

    public void set(final boolean value) {
        this.value = value;
    }

    public void toggle() {
        this.value = !this.value;
    }

}
