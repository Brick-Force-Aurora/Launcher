package de.brickforceaurora.launcher.ui.clay.renderer;

import de.brickforceaurora.launcher.ui.clay.ElementRenderer;
import de.brickforceaurora.launcher.ui.clay.config.Image;
import imgui.ImDrawList;
import imgui.ImVec2;
import me.lauriichan.clay4j.BoundingBox;
import me.lauriichan.clay4j.Element;
import me.lauriichan.snowframe.extension.Extension;

@Extension
public class ImageRenderer extends ElementRenderer<Image> {

    public ImageRenderer() {
        super("image", Image.class);
    }

    @Override
    public void render(final ImDrawList drawList, final ImVec2 offset, final Element element, final BoundingBox boundingBox,
        final Image data) {
        RenderUtil.renderImage(drawList, offset.x + boundingBox.x(), offset.y + boundingBox.y(), boundingBox.width(), boundingBox.height(),
            boundingBox.width() / boundingBox.height(), data.texture(), data.color());
    }

}
