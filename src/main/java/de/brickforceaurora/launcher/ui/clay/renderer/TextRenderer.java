package de.brickforceaurora.launcher.ui.clay.renderer;

import de.brickforceaurora.launcher.ui.clay.ElementRenderer;
import de.brickforceaurora.launcher.ui.clay.FontWrapper;
import de.brickforceaurora.launcher.ui.clay.config.TextColor;
import de.brickforceaurora.launcher.Constant;
import imgui.ImDrawList;
import imgui.ImVec2;
import me.lauriichan.clay4j.BoundingBox;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.IElementConfig.Text;
import me.lauriichan.clay4j.RenderCommand;
import me.lauriichan.snowframe.extension.Extension;
import me.lauriichan.snowframe.util.color.SimpleColor;

@Extension
public final class TextRenderer extends ElementRenderer<String> {

    public TextRenderer() {
        super(RenderCommand.TEXT_RENDERER_ID, String.class);
    }

    @Override
    public void render(ImDrawList drawList, ImVec2 offset, Element element, BoundingBox boundingBox, String data) {
        Text config = element.layout.config(Text.class).get();
        float x = offset.x + boundingBox.x(), y = offset.y + boundingBox.y();
        SimpleColor color = element.layout.config(TextColor.class).map(TextColor::color).orElse(Constant.TEXT_COLOR);
        ((FontWrapper) config.font()).font().renderText(drawList, config.fontSize(), x, y, color.asABGR(), x, y, x + boundingBox.width(),
            y + boundingBox.height(), data, null, false);
    }
}
