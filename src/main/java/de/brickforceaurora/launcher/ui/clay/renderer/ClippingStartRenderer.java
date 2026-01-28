package de.brickforceaurora.launcher.ui.clay.renderer;

import de.brickforceaurora.launcher.ui.clay.ElementRenderer;
import imgui.ImDrawList;
import imgui.ImVec2;
import me.lauriichan.clay4j.BoundingBox;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.RenderCommand;
import me.lauriichan.snowframe.extension.Extension;

@Extension
public class ClippingStartRenderer extends ElementRenderer<Object> {

    public ClippingStartRenderer() {
        super(RenderCommand.CLIPPING_START_ID, Object.class);
    }

    @Override
    public void render(final ImDrawList drawList, final ImVec2 offset, final Element element, final BoundingBox boundingBox,
        final Object data) {
        final float x = offset.x + boundingBox.x(), y = offset.y + boundingBox.y();
        drawList.pushClipRect(x, y, x + boundingBox.width(), y + boundingBox.height());
    }

}
