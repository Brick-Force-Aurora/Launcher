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
    public boolean isTriggered(float gx, float gy, float width, float height) {
        return ImGui.isMouseHoveringRect(gx, gy, gx + width, gy + height);
    }

}
