package de.brickforceaurora.launcher.animation.trigger;

import imgui.ImGui;
import imgui.flag.ImGuiMouseButton;

public final class MouseDownTrigger implements IAnimationTrigger {

    public static final MouseDownTrigger LEFT = new MouseDownTrigger(ImGuiMouseButton.Left);
    public static final MouseDownTrigger MIDDLE = new MouseDownTrigger(ImGuiMouseButton.Middle);
    public static final MouseDownTrigger RIGHT = new MouseDownTrigger(ImGuiMouseButton.Right);

    private final int button;

    private MouseDownTrigger(final int button) {
        this.button = button;
    }

    @Override
    public boolean isTriggered(final float gx, final float gy, final float width, final float height) {
        return ImGui.isMouseDown(button) && ImGui.isMouseHoveringRect(gx, gy, gx + width, gy + height);
    }

}
