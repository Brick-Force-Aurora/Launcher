package de.brickforceaurora.launcher.ui.clay.renderer;

import de.brickforceaurora.launcher.ui.clay.ElementRenderer;
import de.brickforceaurora.launcher.ui.clay.config.Panorama;
import imgui.ImDrawList;
import imgui.ImVec2;
import me.lauriichan.clay4j.BoundingBox;
import me.lauriichan.clay4j.Element;
import me.lauriichan.snowframe.extension.Extension;
import me.lauriichan.snowframe.util.color.SimpleColor;

@Extension
public class PanoramaRenderer extends ElementRenderer<Panorama> {

    public PanoramaRenderer() {
        super("panorama", Panorama.class);
    }

    @Override
    public void render(ImDrawList drawList, ImVec2 offset, Element element, BoundingBox boundingBox, Panorama data) {
        float x = offset.x + boundingBox.x(), y = offset.y + boundingBox.y();
        float aspect = boundingBox.width() / boundingBox.height();
        RenderUtil.renderImage(drawList, x, y, boundingBox.width(), boundingBox.height(), aspect,
            data.bundle().textures.get(data.previous()), SimpleColor.sRGB(1f, 1f, 1f, 1f - data.progress()));
        if (data.previous() != data.current()) {
            RenderUtil.renderImage(drawList, x, y, boundingBox.width(), boundingBox.height(), aspect,
                data.bundle().textures.get(data.current()), SimpleColor.sRGB(1f, 1f, 1f, data.progress()));
        }
    }

}
