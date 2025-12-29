package de.brickforceaurora.launcher.ui.clay.renderer;

import de.brickforceaurora.launcher.ui.clay.ElementRenderer;
import de.brickforceaurora.launcher.ui.clay.config.Rectangle;
import de.brickforceaurora.launcher.Constant;
import imgui.ImDrawList;
import imgui.ImVec2;
import me.lauriichan.clay4j.BoundingBox;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.RenderCommand;
import me.lauriichan.snowframe.extension.Extension;

@Extension
public class RectangleRenderer extends ElementRenderer<Object> {

    public static final Rectangle DEFAULT_CONFIG = new Rectangle(0f, Constant.BLACK);

    public RectangleRenderer() {
        super(RenderCommand.RECTANGLE_RENDERER_ID, Object.class);
    }

    @Override
    public void render(ImDrawList drawList, ImVec2 offset, Element element, BoundingBox boundingBox, Object data) {
        Rectangle config = element.layout.config(Rectangle.class).orElse(DEFAULT_CONFIG);
        float x = offset.x + boundingBox.x(), y = offset.y + boundingBox.y();
        drawList.addRectFilled(x, y, x + boundingBox.width(), y + boundingBox.height(), config.color().asABGR(), config.cornerRadius());
    }

}
