package de.brickforceaurora.launcher.animation.trigger;

import de.brickforceaurora.launcher.animation.property.PropBool;

public final class DelegateTrigger implements IAnimationTrigger {

    public final PropBool active;

    public DelegateTrigger() {
        this.active = new PropBool(false);
    }

    public DelegateTrigger(PropBool active) {
        this.active = active;
    }

    @Override
    public boolean isTriggered(float gx, float gy, float width, float height) {
        return active.get();
    }

}
