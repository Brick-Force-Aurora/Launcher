package de.brickforceaurora.launcher.ui.clay.renderer;

import de.brickforceaurora.launcher.ui.clay.ElementRenderer;
import imgui.ImDrawList;
import imgui.ImVec2;
import me.lauriichan.clay4j.BoundingBox;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.RenderCommand;
import me.lauriichan.snowframe.extension.Extension;

@Extension
public class ClippingEndRenderer extends ElementRenderer<Object> {

    public ClippingEndRenderer() {
        super(RenderCommand.CLIPPING_END_ID, Object.class);
    }

    @Override
    public void render(ImDrawList drawList, ImVec2 offset, Element element, BoundingBox boundingBox, Object data) {
        drawList.popClipRect();
    }

}
