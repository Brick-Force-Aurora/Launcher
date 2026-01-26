package de.brickforceaurora.launcher.ui.clay.renderer;

import de.brickforceaurora.launcher.ui.clay.ElementRenderer;
import de.brickforceaurora.launcher.ui.clay.config.Rectangle;
import de.brickforceaurora.launcher.Constant;
import imgui.ImDrawList;
import imgui.ImVec2;
import me.lauriichan.clay4j.BoundingBox;
import me.lauriichan.clay4j.Element;
import me.lauriichan.snowframe.extension.Extension;

@Extension
public class RectangleRenderer extends ElementRenderer<Rectangle> {

    public static final Rectangle DEFAULT_CONFIG = Rectangle.filled(Constant.BLACK);

    public RectangleRenderer() {
        super("rectangle", Rectangle.class);
    }

    @Override
    public void render(ImDrawList drawList, ImVec2 offset, Element element, BoundingBox boundingBox, Rectangle data) {
        float x = offset.x + boundingBox.x(), y = offset.y + boundingBox.y();
        if (!data.hollow()) {
            drawList.addRectFilled(x, y, x + boundingBox.width(), y + boundingBox.height(), data.color().asABGR(), data.cornerRadius());
            return;
        }
        drawList.addRect(x, y, x + boundingBox.width(), y + boundingBox.height(), data.color().asABGR(), data.cornerRadius(),
            data.brushSize());
    }

}
