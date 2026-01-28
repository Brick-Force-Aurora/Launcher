package de.brickforceaurora.launcher.ui.clay.renderer;

import de.brickforceaurora.launcher.Constant;
import de.brickforceaurora.launcher.ui.clay.ElementRenderer;
import de.brickforceaurora.launcher.ui.clay.config.BackgroundRectangle;
import imgui.ImDrawList;
import imgui.ImVec2;
import me.lauriichan.clay4j.BoundingBox;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.RenderCommand;
import me.lauriichan.snowframe.extension.Extension;

@Extension
public class BackgroundRectangleRenderer extends ElementRenderer<Object> {

    public static final BackgroundRectangle DEFAULT_CONFIG = new BackgroundRectangle(Constant.BLACK, 0f);

    public BackgroundRectangleRenderer() {
        super(RenderCommand.BACKGROUND_RECTANGLE_RENDERER_ID, Object.class);
    }

    @Override
    public void render(final ImDrawList drawList, final ImVec2 offset, final Element element, final BoundingBox boundingBox,
        final Object data) {
        final BackgroundRectangle config = element.layout.config(BackgroundRectangle.class).orElse(DEFAULT_CONFIG);
        final float x = offset.x + boundingBox.x(), y = offset.y + boundingBox.y();
        drawList.addRectFilled(x, y, x + boundingBox.width(), y + boundingBox.height(), config.color().asABGR(), config.cornerRadius());
    }

}
