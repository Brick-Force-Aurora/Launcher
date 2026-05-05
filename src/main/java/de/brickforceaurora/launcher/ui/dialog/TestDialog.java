package de.brickforceaurora.launcher.ui.dialog;

import de.brickforceaurora.launcher.Constant;
import de.brickforceaurora.launcher.FontAtlas;
import de.brickforceaurora.launcher.ui.RenderContext;
import de.brickforceaurora.launcher.ui.clay.FontWrapper;
import de.brickforceaurora.launcher.ui.clay.config.BackgroundRectangle;
import de.brickforceaurora.launcher.ui.clay.config.Rectangle;
import de.brickforceaurora.launcher.ui.clay.config.TextColor;
import de.brickforceaurora.launcher.ui.helper.Button;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.IElementConfig.Text;
import me.lauriichan.clay4j.IElementConfig.Text.WrapMode;
import me.lauriichan.clay4j.ISizing;
import me.lauriichan.clay4j.Layout.LayoutDirection;
import me.lauriichan.clay4j.Layout.Padding;
import me.lauriichan.clay4j.LayoutContext;

public class TestDialog extends AbstractDialog<Void> {

    public static final Padding NO_PADDING = new Padding(0);
    public static final BackgroundRectangle WINDOW_BG = Rectangle.bg(Constant.WINDOW_BACKGROUND_COLOR);

    private final Button startButton = Button.builder().padding(Padding.builder().top(4).right(4).left(28).bottom(8).build())
        .action(this::close).build();

    private final RenderContext renderContext = new RenderContext();

    public TestDialog() {
        startButton.setup(renderContext);
    }

    @Override
    protected void updateState(LayoutContext layout, float deltaTime) {
        renderContext.tickAnimations();
        renderContext.update(layout, deltaTime);
    }

    @Override
    protected void createLayout(LayoutContext layout, float deltaTime) {
        Element.Builder builder = layout.newRoot();
        builder.layout().childGap(4).layoutDirection(LayoutDirection.TOP_TO_BOTTOM).padding(NO_PADDING).width(ISizing.fixed(layout.width()))
            .height(ISizing.fixed(layout.height())).childGap(0);
        try (Element root = builder.elementId("root").build()) {
            try (Element updateBtn = startButton.build(renderContext, root)) {
                builder = updateBtn.newElement();
                builder
                    .layout().layoutDirection(LayoutDirection.TOP_TO_BOTTOM).addConfigs(Text.builder().text("UPDATE")
                        .font(FontWrapper.of(FontAtlas.NOTO_SANS_EXTRA_BOLD)).wrapMode(WrapMode.WRAP_NONE).fontSize(24).build())
                    .addConfigs(new TextColor(Constant.BUTTON_TEXT_COLOR));
                builder.build().close();
            }
        }
    }

}
