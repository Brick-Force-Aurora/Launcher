package de.brickforceaurora.launcher.ui.settings;

import static de.brickforceaurora.launcher.ui.UIConstant.*;
import static de.brickforceaurora.launcher.Constant.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Supplier;

import de.brickforceaurora.launcher.Constant;
import de.brickforceaurora.launcher.FontAtlas;
import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.ui.RenderContext;
import de.brickforceaurora.launcher.ui.clay.FontWrapper;
import de.brickforceaurora.launcher.ui.clay.config.Rectangle;
import de.brickforceaurora.launcher.ui.clay.config.TextSettings;
import de.brickforceaurora.launcher.ui.clay.config.TextSettings.Scroll;
import de.brickforceaurora.launcher.ui.helper.Button;
import de.brickforceaurora.launcher.util.NFDUtil;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.ISizing;
import me.lauriichan.clay4j.IElementConfig.Text;
import me.lauriichan.clay4j.IElementConfig.Text.WrapMode;
import me.lauriichan.clay4j.Layout.HAlignment;
import me.lauriichan.clay4j.Layout.LayoutDirection;
import me.lauriichan.clay4j.Layout.Padding;
import me.lauriichan.clay4j.Layout.VAlignment;

public class PathSetting extends Setting {

    private final TextSettings pathSettings = new TextSettings(TEXT_COLOR, Scroll.newDefault());

    private final Path appDir = LauncherApp.get().appDirectory();

    private final boolean directory;

    private final Supplier<Path> getter;
    private final Consumer<Path> setter;

    private final Button button;

    private String pathValue = "";

    public PathSetting(RenderContext renderContext, boolean directory, Supplier<Path> getter, Consumer<Path> setter) {
        super(4f);
        this.directory = directory;
        this.getter = getter;
        this.setter = setter;
        update();
        this.button = Button.builder().action(this::selectPath).width(ISizing.grow()).padding(new Padding(4, 4, 2, 4)).build()
            .setup(renderContext);
    }

    @Override
    public void update() {
        Path current = getter.get();
        try {
            current = appDir.relativize(current);
        } catch (RuntimeException _) {
            current = current.toAbsolutePath();
        }
        pathValue = current.toString();
    }

    @Override
    public void apply() {
        Path path = Paths.get(pathValue);
        if (!path.isAbsolute()) {
            path = appDir.resolve(pathValue).toAbsolutePath();
        }
        setter.accept(path);
    }

    private void selectPath() {
        String path = directory ? NFDUtil.pickDirectory(pathValue) : NFDUtil.pickFile(pathValue);
        if (path == null || path.isBlank()) {
            return;
        }
        pathValue = path;
    }

    @Override
    protected boolean belowLabel() {
        return true;
    }

    @Override
    protected void create(RenderContext context, Element parent) {
        Element.Builder builder = parent.newElement();
        builder.layout().width(ISizing.percentage(1)).height(ISizing.percentage(1)).layoutDirection(LayoutDirection.LEFT_TO_RIGHT)
            .padding(NO_PADDING).childHorizontalAlignment(HAlignment.LEFT).childGap(8);
        try (Element settingContainer = builder.build()) {
            builder = settingContainer.newElement();
            builder.layout().width(ISizing.percentage(0.65f)).height(ISizing.percentage(1))
                .addConfigs(Rectangle.bg(Constant.BUTTON_SHADOW_COLOR, 2.5f)).childVerticalAlignment(VAlignment.CENTER);
            try (Element container1 = builder.build()) {
                builder = container1.newElement();
                builder.layout().width(ISizing.percentage(1f)).height(ISizing.fixed(20f))
                    .addConfigs(Text.builder().text(pathValue).alignment(HAlignment.LEFT).wrapMode(WrapMode.WRAP_NONE)
                        .font(FontWrapper.of(FontAtlas.NOTO_SANS_MEDIUM)).fontSize(16).build())
                    .addConfigs(pathSettings).layoutDirection(LayoutDirection.TOP_TO_BOTTOM);
                builder.build().close();
            }

            builder = settingContainer.newElement();
            builder.layout().width(ISizing.grow()).height(ISizing.percentage(1)).padding(NO_PADDING)
                .childHorizontalAlignment(HAlignment.LEFT);
            try (Element container2 = builder.build()) {
                try (Element btn = button.build(context, container2)) {
                    builder = btn.newElement();
                    builder
                        .layout().layoutDirection(LayoutDirection.TOP_TO_BOTTOM).addConfigs(Text.builder().text("BROWSE")
                            .font(FontWrapper.of(FontAtlas.NOTO_SANS_EXTRA_BOLD)).wrapMode(WrapMode.WRAP_NONE).fontSize(18).build())
                        .addConfigs(BUTTON_TXT_COLOR);
                    builder.build().close();
                }
            }
        }

    }

}
