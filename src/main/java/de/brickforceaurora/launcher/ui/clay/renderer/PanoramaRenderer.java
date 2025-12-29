package de.brickforceaurora.launcher.ui.clay.renderer;

import de.brickforceaurora.launcher.TextureAtlas.ImTexture;
import de.brickforceaurora.launcher.ui.clay.ElementRenderer;
import de.brickforceaurora.launcher.ui.clay.config.Panorama;
import imgui.ImDrawList;
import imgui.ImVec2;
import me.lauriichan.applicationbase.app.extension.Extension;
import me.lauriichan.applicationbase.app.util.color.SimpleColor;
import me.lauriichan.clay4j.BoundingBox;
import me.lauriichan.clay4j.Element;

@Extension
public class PanoramaRenderer extends ElementRenderer<Panorama> {

    public PanoramaRenderer() {
        super("panorama", Panorama.class);
    }

    @Override
    public void render(ImDrawList drawList, ImVec2 offset, Element element, BoundingBox boundingBox, Panorama data) {
        float x = offset.x + boundingBox.x(), y = offset.y + boundingBox.y();
        renderImage(drawList, x, y, boundingBox.width(), boundingBox.height(), data.bundle().textures.get(data.previous()),
            1f - data.progress());
        if (data.previous() != data.current()) {
            renderImage(drawList, x, y, boundingBox.width(), boundingBox.height(), data.bundle().textures.get(data.current()),
                data.progress());
        }
    }

    private void renderImage(ImDrawList drawList, float x, float y, float width, float height, ImTexture texture, float alpha) {
        float uvX = Math.min(1f - (width / texture.width), 0) / -2f;
        float uvY = Math.min(1f - (height / texture.height), 0) / -2f;
        drawList.addImage(texture.id, x, y, x + width, y + height, uvX, uvY, 1f - uvX, 1f - uvY, SimpleColor.sRGB(1f, 1f, 1f, alpha).asABGR());
    }

}
