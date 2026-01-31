package de.brickforceaurora.launcher.ui.settings;

import static de.brickforceaurora.launcher.ui.UserInterface.NO_PADDING;

import java.awt.Desktop;
import java.io.IOException;
import java.util.function.BooleanSupplier;

import de.brickforceaurora.launcher.Constant;
import de.brickforceaurora.launcher.FontAtlas;
import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.ui.RenderContext;
import de.brickforceaurora.launcher.ui.clay.FontWrapper;
import de.brickforceaurora.launcher.ui.clay.config.Rectangle;
import de.brickforceaurora.launcher.ui.clay.config.TextColor;
import de.brickforceaurora.launcher.ui.helper.Button;
import de.brickforceaurora.launcher.updater.UpdaterConfig;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.IElementConfig.Clip;
import me.lauriichan.clay4j.IElementConfig.Text;
import me.lauriichan.clay4j.IElementConfig.Text.WrapMode;
import me.lauriichan.clay4j.ISizing;
import me.lauriichan.clay4j.Layout.HAlignment;
import me.lauriichan.clay4j.Layout.LayoutDirection;
import me.lauriichan.clay4j.Layout.Padding;
import me.lauriichan.clay4j.Layout.VAlignment;
import me.lauriichan.clay4j.LayoutContext;
import me.lauriichan.snowframe.ConfigModule;
import me.lauriichan.snowframe.config.ConfigManager;

public final class SettingsInterface {

    private record Entry(String name, Setting setting) {}

    private final ObjectArrayList<Entry> entries = new ObjectArrayList<>();

    private final RenderContext renderContext;

    private final ConfigManager configManager;
    private final UpdaterConfig updaterConfig;

    private final Button saveButton, openFolderButton;

    public SettingsInterface(final RenderContext renderContext) {
        this.renderContext = renderContext;
        this.configManager = LauncherApp.get().snowFrame().module(ConfigModule.class).manager();
        this.updaterConfig = configManager.config(UpdaterConfig.class);
        createSettings();
        this.saveButton = Button.builder().action(() -> {
            for (final Entry entry : entries) {
                if (entry.setting() != null) {
                    entry.setting().apply();
                }
            }
            configManager.save();
        }).width(ISizing.grow()).build().setup(renderContext);
        this.openFolderButton = Button.builder().action(() -> {
            try {
                Desktop.getDesktop().open(LauncherApp.get().appDirectory().toFile());
            } catch (IOException _) {}
        }).width(ISizing.grow()).build().setup(renderContext);
    }

    private void createSettings() {
        group("Updater");
        checkbox("Check for updates?", updaterConfig::checkForUpdates, updaterConfig::checkForUpdates);
        checkbox("Get experimental updates", updaterConfig::experimental, updaterConfig::experimental);
        checkbox("Automatically install updates", updaterConfig::automaticUpdate, updaterConfig::automaticUpdate);
    }

    /*
     * Settings creation helper
     */

    private void group(final String name) {
        entries.add(new Entry(name, null));
    }

    private void checkbox(final String name, final BooleanSupplier getter, final BooleanConsumer setter) {
        entries.add(new Entry(name, new BooleanSetting(getter, setter)));
    }

    /*
     * 
     */

    public void updateState(final LayoutContext layout, final float deltaTime) {}

    public void createLayout(final LayoutContext layout, final float deltaTime) {
        Element.Builder builder = layout.newRoot();
        builder.layout().childGap(4).layoutDirection(LayoutDirection.TOP_TO_BOTTOM).padding(NO_PADDING).width(ISizing.fixed(layout.width()))
            .height(ISizing.fixed(layout.height())).childGap(4)
            .padding(Padding.builder().left((int) (layout.width() * 0.6f)).top(40 + 8).right(8).bottom(80 + 32 + 8).build());
        try (Element window = builder.elementId("settings_root").build()) {
            builder = window.newElement();
            builder.layout().childGap(4).width(ISizing.percentage(1f)).height(ISizing.percentage(1f))
                .layoutDirection(LayoutDirection.TOP_TO_BOTTOM)
                .addConfigs(Rectangle.bg(Constant.PROGRESS_BACKGROUND_COLOR.duplicate().alpha(0.85f), 12.5f))
                .addConfigs(Rectangle.hollow(Constant.BUTTON_PANEL_BACKGROUND_COLOR.duplicate().alpha(0.85f), 12.5f, 2.5f));
            try (Element root = builder.build()) {
                builder = root.newElement();
                builder.layout().width(ISizing.percentage(1f)).height(ISizing.grow()).layoutDirection(LayoutDirection.TOP_TO_BOTTOM)
                    .padding(NO_PADDING).addConfigs(Clip.builder().vertical(true).build());
                try (Element tableParent = builder.build()) {
                    for (final Entry entry : entries) {
                        builder = tableParent.newElement();
                        builder.layout().width(ISizing.percentage(1f))
                            .height(ISizing.fixed(24f * (entry.setting() == null ? 1 : entry.setting().size())))
                            .childVerticalAlignment(VAlignment.CENTER);
                        try (Element entryParent = builder.build()) {
                            if (entry.setting() == null) {
                                builder = entryParent.newElement();
                                builder.layout().width(ISizing.grow()).height(ISizing.percentage(1f))
                                    .layoutDirection(LayoutDirection.TOP_TO_BOTTOM)
                                    .addConfigs(Text.builder().text(entry.name().toUpperCase())
                                        .font(FontWrapper.of(FontAtlas.NOTO_SANS_EXTRA_BOLD)).fontSize(22).build());
                                builder.build().close();
                                continue;
                            }
                            builder = entryParent.newElement();
                            builder.layout().width(ISizing.grow()).height(ISizing.percentage(1f))
                                .layoutDirection(LayoutDirection.TOP_TO_BOTTOM)
                                .addConfigs(
                                    Text.builder().text(entry.name()).font(FontWrapper.of(FontAtlas.NOTO_SANS_MEDIUM)).fontSize(18).build())
                                .childVerticalAlignment(VAlignment.CENTER);
                            builder.build().close();

                            builder = entryParent.newElement();
                            builder.layout().width(ISizing.grow()).height(ISizing.percentage(1f)).padding(NO_PADDING)
                                .childHorizontalAlignment(HAlignment.RIGHT);
                            try (Element element = builder.build()) {
                                entry.setting().create(renderContext, element);
                            }
                        }
                    }
                }
                builder = root.newElement();
                builder.layout().width(ISizing.percentage(1f)).height(ISizing.fixed(32f)).padding(NO_PADDING);
                try (Element buttonRow = builder.build()) {
                    try (Element folderBtn = openFolderButton.build(renderContext, buttonRow)) {
                        builder = folderBtn.newElement();
                        builder
                            .layout().layoutDirection(LayoutDirection.TOP_TO_BOTTOM).addConfigs(Text.builder().text("OPEN FOLDER")
                                .font(FontWrapper.of(FontAtlas.NOTO_SANS_EXTRA_BOLD)).wrapMode(WrapMode.WRAP_NONE).fontSize(20).build())
                            .addConfigs(new TextColor(Constant.BUTTON_TEXT_COLOR));
                        builder.build().close();
                    }
                    try (Element saveBtn = saveButton.build(renderContext, buttonRow)) {
                        builder = saveBtn.newElement();
                        builder
                            .layout().layoutDirection(LayoutDirection.TOP_TO_BOTTOM).addConfigs(Text.builder().text("APPLY & SAVE")
                                .font(FontWrapper.of(FontAtlas.NOTO_SANS_EXTRA_BOLD)).wrapMode(WrapMode.WRAP_NONE).fontSize(20).build())
                            .addConfigs(new TextColor(Constant.BUTTON_TEXT_COLOR));
                        builder.build().close();
                    }
                }
            }
        }
    }

}
