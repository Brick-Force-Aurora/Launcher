package de.brickforceaurora.launcher.ui.clay;

import java.util.Objects;

import imgui.ImDrawList;
import imgui.ImVec2;
import me.lauriichan.applicationbase.app.extension.ExtensionPoint;
import me.lauriichan.applicationbase.app.extension.IExtension;
import me.lauriichan.clay4j.BoundingBox;
import me.lauriichan.clay4j.Element;

@ExtensionPoint
public abstract class ElementRenderer<T> implements IExtension {

    private final String id;
    private final Class<T> dataType;

    public ElementRenderer(String id, Class<T> dataType) {
        this.id = id;
        Objects.requireNonNull(dataType);
        this.dataType = dataType == Object.class ? null : dataType;
    }

    public final String id() {
        return id;
    }

    @SuppressWarnings("unchecked")
    final void renderInternal(ImDrawList drawList, ImVec2 offset, Element element, BoundingBox boundingBox, Object data) {
        if (dataType == null) {
            render(drawList, offset, element, boundingBox, (T) data);
            return;
        }
        if (!dataType.isInstance(data)) {
            return;
        }
        render(drawList, offset, element, boundingBox, dataType.cast(data));
    }

    public abstract void render(ImDrawList drawList, ImVec2 offset, Element element, BoundingBox boundingBox, T data);

}
