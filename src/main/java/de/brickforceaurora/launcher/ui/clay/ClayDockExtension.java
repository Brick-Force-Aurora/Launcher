package de.brickforceaurora.launcher.ui.clay;

import java.util.concurrent.TimeUnit;

import de.brickforceaurora.launcher.LauncherApp;
import imgui.ImVec2;
import imgui.internal.ImGui;
import me.lauriichan.applicationbase.app.ui.ImGuiHandle;
import me.lauriichan.applicationbase.app.ui.dock.DockUIExtension;
import me.lauriichan.clay4j.LayoutContext;

public abstract class ClayDockExtension extends DockUIExtension {

    private static final float SECOND_IN_NANOS = TimeUnit.SECONDS.toNanos(1);

    private final LayoutContext layout = new LayoutContext();
    private final RenderManager renderManager;
    private final ImGuiHandle handle;

    private final ImVec2 cursorPos = new ImVec2(), windowPos = new ImVec2(), windowSize = new ImVec2();

    private volatile long lastFrame = 0L;

    public ClayDockExtension(String title, String dockId, LauncherApp app) {
        super(title, dockId);
        this.handle = app.handle();
        this.renderManager = app.renderManager();
        lastFrame = System.nanoTime();
    }

    @Override
    protected final void renderContent(long windowHandle) {
        boolean firstFrame = lastFrame == 0L;

        long now = System.nanoTime();
        float deltaTime = (this.lastFrame - now) / SECOND_IN_NANOS;
        this.lastFrame = now;

        ImGui.getCursorPos(cursorPos);
        ImGui.getWindowPos(windowPos);
        ImGui.getWindowSize(windowSize);
        
        if (!firstFrame) {
            updateContent(deltaTime);
        }
        
        layout.setDimensions(windowSize.x, windowSize.y);

        layout.reset();
        renderContent(layout, windowHandle);
        layout.calculateLayout();
        layout.setPointer(cursorPos.x, cursorPos.y, ImGui.isMouseClicked(0));
        layout.updateScrollContainers(false, handle.scrollDeltaX(), handle.scrollDeltaY(), deltaTime);

        renderManager.render(layout, windowPos, ImGui.getForegroundDrawList());
    }

    protected abstract void renderContent(LayoutContext layout, long windowHandle);

    protected abstract void updateContent(float deltaTime);

}
