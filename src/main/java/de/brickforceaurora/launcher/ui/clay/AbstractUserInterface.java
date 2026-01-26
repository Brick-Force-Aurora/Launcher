package de.brickforceaurora.launcher.ui.clay;

import java.util.concurrent.TimeUnit;

import de.brickforceaurora.launcher.LauncherApp;
import imgui.ImGui;
import imgui.ImGuiViewport;
import imgui.ImVec2;
import imgui.flag.ImGuiWindowFlags;
import me.lauriichan.clay4j.LayoutContext;
import me.lauriichan.snowframe.ImGUIModule;

public abstract class AbstractUserInterface {

    public static final double SECOND_RATIO = TimeUnit.SECONDS.toNanos(1);

    private final LayoutContext layout = new LayoutContext();
    private final RenderManager renderManager;

    private final ImVec2 cursorPos = new ImVec2(), windowPos = new ImVec2(), windowSize = new ImVec2();

    protected final ImGUIModule guiModule;

    public AbstractUserInterface(LauncherApp app) {
        this.renderManager = app.renderManager();
        this.guiModule = app.snowFrame().module(ImGUIModule.class);
    }

    public void render() {
        float deltaTime = ImGUIModule.DELTA_TIME.get() / ((float) SECOND_RATIO);

        ImGuiViewport viewport = ImGui.getMainViewport();

        ImGui.getMousePos(cursorPos);
        viewport.getWorkPos(windowPos);
        viewport.getWorkSize(windowSize);

        ImGui.setNextWindowPos(0, 0);
        ImGui.setNextWindowBgAlpha(0f);
        ImGui.setNextWindowSize(windowSize);
        ImGui.begin("Launcher", ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoDecoration | ImGuiWindowFlags.NoSavedSettings);
        try {
            updateState(layout, deltaTime);

            layout.setDimensions(windowSize.x, windowSize.y);

            layout.reset();
            createLayout(layout, deltaTime);
            layout.calculateLayout();
            layout.setPointer(cursorPos.x, cursorPos.y, ImGui.isMouseDown(0));
            layout.updateScrollContainers(false, guiModule.scrollDeltaX(), guiModule.scrollDeltaY(), deltaTime);

            renderManager.render(layout, windowPos, ImGui.getWindowDrawList());
        } finally {
            ImGui.end();
        }
    }

    protected abstract void updateState(LayoutContext layout, float deltaTime);

    protected abstract void createLayout(LayoutContext layout, float deltaTime);

}
