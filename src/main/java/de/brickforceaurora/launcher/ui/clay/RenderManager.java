package de.brickforceaurora.launcher.ui.clay;

import de.brickforceaurora.launcher.LauncherApp;
import imgui.ImDrawList;
import imgui.ImVec2;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import me.lauriichan.clay4j.LayoutContext;
import me.lauriichan.clay4j.RenderCommand;
import me.lauriichan.laylib.logger.ISimpleLogger;

public final class RenderManager {

    private final ISimpleLogger logger;
    private final Object2ObjectArrayMap<String, ElementRenderer<?>> renderers = new Object2ObjectArrayMap<>();

    public RenderManager(LauncherApp app) {
        this.logger = app.logger();
        app.extension(ElementRenderer.class, true).callInstances(renderer -> {
            if (renderers.containsKey(renderer.id())) {
                logger.error("Renderer id '{0}' is already taken.", renderer.id());
                return;
            }
            renderers.put(renderer.id(), renderer);
        });
    }

    public void render(LayoutContext context, ImVec2 windowPos, ImDrawList drawList) {
        ObjectIterator<RenderCommand> iterator = context.renderCommands().iterator();
        RenderCommand command;
        ElementRenderer<?> renderer;
        while (iterator.hasNext()) {
            command = iterator.next();
            renderer = renderers.get(command.id());
            if (renderer == null) {
                // No clue how to render this
                continue;
            }
            renderer.renderInternal(drawList, windowPos, command.element(), command.boundingBox(), command.data());
        }
    }

}
