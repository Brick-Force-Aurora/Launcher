package de.brickforceaurora.launcher.ui.clay.renderer;

import de.brickforceaurora.launcher.Constant;
import de.brickforceaurora.launcher.ui.clay.ElementRenderer;
import de.brickforceaurora.launcher.ui.clay.FontWrapper;
import de.brickforceaurora.launcher.ui.clay.config.TextSettings;
import imgui.ImDrawList;
import imgui.ImVec2;
import me.lauriichan.clay4j.BoundingBox;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.IElementConfig.Text;
import me.lauriichan.clay4j.IElementConfig.Text.WrapMode;
import me.lauriichan.clay4j.data.TextElementData;
import me.lauriichan.clay4j.RenderCommand;
import me.lauriichan.snowframe.extension.Extension;

@Extension
public final class TextRenderer extends ElementRenderer<String> {

    public static final TextSettings DEFAULT = new TextSettings(Constant.TEXT_COLOR, null);

    public TextRenderer() {
        super(RenderCommand.TEXT_RENDERER_ID, String.class);
    }

    @Override
    public void render(final ImDrawList drawList, final ImVec2 offset, final Element element, final BoundingBox boundingBox,
        final String data) {
        final Text config = element.layout.config(Text.class).get();
        final float x = offset.x + boundingBox.x(), y = offset.y + boundingBox.y();
        final TextSettings settings = element.layout.config(TextSettings.class).orElse(DEFAULT);
        float scrollOffset = 0f;
        if (config.wrapMode() != WrapMode.WRAP_WORDS && settings.scroll() != null) {
            float preferred = element.data(TextElementData.class).get().preferredWidth;
            TextSettings.Scroll scroll = settings.scroll();
            if (preferred > boundingBox.width()) {
                scroll.maxScroll = preferred - boundingBox.width();
                if (scroll.timer != 0) {
                    scroll.timer--;
                } else if (scroll.reset) {
                    scroll.reset = false;
                    scroll.timer = scroll.waitingFrames;
                    scroll.scroll = 0;
                } else if (scroll.scroll > scroll.maxScroll) {
                    scroll.timer = scroll.waitingFrames;
                    scroll.reset = true;
                } else {
                    scroll.scroll += scroll.sizePerFrame;
                }
            } else {
                scroll.maxScroll = 0;
                scroll.scroll = 0;
                scroll.timer = scroll.waitingFrames;
            }
            scrollOffset = scroll.scroll;
        }
        drawList.addText(((FontWrapper) config.font()).font(), (int) config.fontSize(), x - scrollOffset, y, settings.color().asABGR(),
            data, x, y, x + boundingBox.width(), y + boundingBox.height());
    }
}
