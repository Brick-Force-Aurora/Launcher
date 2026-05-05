package de.brickforceaurora.launcher.ui.dialog;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

import de.brickforceaurora.launcher.FontAtlas;
import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.ui.clay.AbstractUserInterface;
import imgui.ImGui;
import imgui.flag.ImGuiWindowFlags;
import me.lauriichan.clay4j.LayoutContext;
import me.lauriichan.snowframe.ImGUIModule;

public abstract class AbstractDialog<V> extends AbstractUserInterface {

    public AbstractDialog() {
        super(LauncherApp.get());
    }

    private final AtomicBoolean visible = new AtomicBoolean(true);
    private final AtomicReference<CompletableFuture<V>> reference = new AtomicReference<>();
    private final String dialogId = UUID.randomUUID().toString();

    public final CompletableFuture<V> open() {
        if (visible.compareAndExchange(false, true)) {
            return reference.get();
        }
        LauncherApp.get().dialogManager().open(this);
        CompletableFuture<V> future = new CompletableFuture<V>();
        reference.set(future);
        return future;
    }

    public final void close() {
        close(null);
    }

    protected final void close(V value) {
        if (visible.compareAndExchange(true, false)) {
            return;
        }
        LauncherApp.get().dialogManager().closed(this);
        reference.get().complete(value);
    }
    
    @Override
    public void render() {
        final float deltaTime = ImGUIModule.DELTA_TIME.get() / (float) SECOND_RATIO;

        ImGui.getMousePos(cursorPos);
        LayoutContext layout = layout();

        ImGui.pushFont(FontAtlas.CONSOLE_FONT);
        try {
            ImGui.setNextWindowPos(windowPos);
            ImGui.setNextWindowBgAlpha(0f);
            ImGui.setNextWindowSize(windowSize);
            ImGui.setNextWindowContentSize(windowSize);
            ImGui.begin(dialogId, ImGuiWindowFlags.NoCollapse | ImGuiWindowFlags.NoSavedSettings);
            try {
                updateState(layout, deltaTime);

                layout.setDimensions(windowSize.x, windowSize.y);

                layout.reset();
                createLayout(layout, deltaTime);
                layout.calculateLayout();
                layout.setPointer(cursorPos.x - windowPos.x, cursorPos.y - windowPos.y, ImGui.isMouseDown(0));
                layout.updateScrollContainers(false, guiModule.scrollDeltaX(), guiModule.scrollDeltaY(), deltaTime);

                renderManager().render(layout, windowPos, ImGui.getWindowDrawList());
            } finally {
                ImGui.end();
            }
            
            additionalRender(deltaTime);
        } finally {
            ImGui.popFont();
        }
    }

}
