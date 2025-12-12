package de.brickforceaurora.launcher;

import me.lauriichan.applicationbase.app.ui.DefaultConstants;
import me.lauriichan.applicationbase.app.util.color.SimpleColor;

public final class Constant {

    private Constant() {
        throw new UnsupportedOperationException();
    }

    public static final SimpleColor BLACK = SimpleColor.sRGB("#0");
    public static final SimpleColor WHITE = SimpleColor.sRGB("#f");
    public static final SimpleColor RED = SimpleColor.sRGB("#eb4b18");

    public static final SimpleColor WINDOW_BACKGROUND_COLOR = DefaultConstants.WINDOW_BACKGROUND_COLOR.set(SimpleColor.sRGB("#000F1B"));
    public static final SimpleColor PROGRESS_BACKGROUND_COLOR = SimpleColor.sRGB("#0A2133");
    public static final SimpleColor BUTTON_PANEL_BACKGROUND_COLOR = SimpleColor.sRGB("#091E2F");

    public static final SimpleColor TEXT_COLOR = DefaultConstants.TEXT_COLOR.set(SimpleColor.sRGB("#3c3c3c"));
    public static final SimpleColor BUTTON_COLOR = SimpleColor.sRGB("#F49719");
    public static final SimpleColor BUTTON_SHADOW_COLOR = BUTTON_COLOR.duplicate().multiply(0.7).add(BLACK.duplicate().multiply(0.3));
    public static final SimpleColor BUTTON_HIGHLIGHT_COLOR = BUTTON_COLOR.duplicate().multiply(0.85).add(WHITE.duplicate().multiply(0.15));

    public static final SimpleColor PROGRESS_BAR_COLOR = BUTTON_SHADOW_COLOR;
    public static final SimpleColor PROGRESS_BAR_FILL_EMPTY_COLOR = BUTTON_COLOR.duplicate().multiply(0.85).add(BLACK.duplicate().multiply(0.15));
    public static final SimpleColor PROGRESS_BAR_FILL_COLOR = BUTTON_HIGHLIGHT_COLOR;
    public static final SimpleColor PROGRESS_BAR_FILL_HIGHLIGHT_COLOR = BUTTON_HIGHLIGHT_COLOR.duplicate().multiply(0.75).add(WHITE.duplicate().multiply(0.25));

    static void updateVariables() {
        PROGRESS_BAR_FILL_HIGHLIGHT_COLOR.alpha(0.8d);
    }

}
