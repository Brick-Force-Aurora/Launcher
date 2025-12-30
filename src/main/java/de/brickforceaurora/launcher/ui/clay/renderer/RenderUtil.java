package de.brickforceaurora.launcher.ui.clay.renderer;

import de.brickforceaurora.launcher.TextureAtlas.ImTexture;
import imgui.ImDrawList;
import me.lauriichan.snowframe.util.color.SimpleColor;

final class RenderUtil {

    private RenderUtil() {
        throw new UnsupportedOperationException();
    }

    public static void renderImage(ImDrawList drawList, float x, float y, float width, float height, float aspect, ImTexture texture,
        SimpleColor color) {
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
        drawList.addImage(texture.id, x, y, x + width, y + height, uvX, uvY, 1f - uvX, 1f - uvY, color.asABGR());
    }

}
