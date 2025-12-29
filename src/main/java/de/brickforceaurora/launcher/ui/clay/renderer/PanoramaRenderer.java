package de.brickforceaurora.launcher.ui.clay.renderer;

import de.brickforceaurora.launcher.ui.clay.ElementRenderer;
import de.brickforceaurora.launcher.ui.clay.config.Panorama;
import de.brickforceaurora.launcher.TextureAtlas.ImTexture;
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
        renderImage(drawList, x, y, boundingBox.width(), boundingBox.height(), aspect, data.bundle().textures.get(data.previous()),
            1f - data.progress());
        if (data.previous() != data.current()) {
            renderImage(drawList, x, y, boundingBox.width(), boundingBox.height(), aspect, data.bundle().textures.get(data.current()),
                data.progress());
        }
    }

    private void renderImage(ImDrawList drawList, float x, float y, float width, float height, float aspect, ImTexture texture, float alpha) {
        float scale;
        if (texture.aspect > aspect) {
            scale = height / texture.height;
        } else {
            scale = width / texture.width;
        }
        float resized = texture.width * scale;
        float uvX = ((resized - width) / 2f) / resized;
        resized = texture.height * scale;
        float uvY = ((resized - height) / 2f) / resized;
        drawList.addImage(texture.id, x, y, x + width, y + height, uvX, uvY, 1f - uvX, 1f - uvY, SimpleColor.sRGB(1f, 1f, 1f, alpha).asABGR());
    }

}
