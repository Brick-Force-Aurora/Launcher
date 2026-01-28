package de.brickforceaurora.launcher.animation.trigger;

import imgui.ImGui;

public final class HoverTrigger implements IAnimationTrigger {

    public static final HoverTrigger INSTANCE = new HoverTrigger();

    private HoverTrigger() {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public boolean isTriggered(final float gx, final float gy, final float width, final float height) {
        return ImGui.isMouseHoveringRect(gx, gy, gx + width, gy + height);
    }

}
