package de.brickforceaurora.launcher.ui;

import de.brickforceaurora.launcher.Constant;
import de.brickforceaurora.launcher.ui.clay.config.BackgroundRectangle;
import de.brickforceaurora.launcher.ui.clay.config.Rectangle;
import de.brickforceaurora.launcher.ui.clay.config.TextSettings;
import me.lauriichan.clay4j.IElementConfig.AspectRatio;
import me.lauriichan.clay4j.Layout.Padding;

public final class UIConstant {

    private UIConstant() {
        throw new UnsupportedOperationException();
    }

    public static final TextSettings BUTTON_TXT_COLOR = new TextSettings(Constant.BUTTON_TEXT_COLOR);
    
    public static final Padding NO_PADDING = new Padding(0);
    public static final BackgroundRectangle WINDOW_BG = Rectangle.bg(Constant.WINDOW_BACKGROUND_COLOR);

    public static final AspectRatio ONE_TO_ONE = new AspectRatio(1f);

    public static final float PANEL_OPACTIY = 0.85f;
    public static final BackgroundRectangle PANEL_LIGHT = Rectangle.bg(Constant.PROGRESS_BACKGROUND_COLOR.duplicate().alpha(PANEL_OPACTIY));
    public static final BackgroundRectangle PANEL_DARK = Rectangle
        .bg(Constant.BUTTON_PANEL_BACKGROUND_COLOR.duplicate().alpha(PANEL_OPACTIY));

}
