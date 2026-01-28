package de.brickforceaurora.launcher.animation.trigger;

import de.brickforceaurora.launcher.animation.property.PropBool;

public final class DelegateTrigger implements IAnimationTrigger {

    public final PropBool active;

    public DelegateTrigger() {
        this.active = new PropBool(false);
    }

    public DelegateTrigger(final PropBool active) {
        this.active = active;
    }

    @Override
    public boolean isTriggered(final float gx, final float gy, final float width, final float height) {
        return active.get();
    }

}
