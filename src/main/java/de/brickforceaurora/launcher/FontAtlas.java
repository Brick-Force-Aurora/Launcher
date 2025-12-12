package de.brickforceaurora.launcher;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.reflect.Field;

import de.brickforceaurora.launcher.util.IOUtil;
import imgui.ImFont;
import imgui.ImFontAtlas;
import imgui.ImFontConfig;
import imgui.ImGui;
import me.lauriichan.applicationbase.app.resource.source.IDataSource;

public final class FontAtlas {

    @Retention(RetentionPolicy.RUNTIME)
    private static @interface Font {

        String path();

        float defaultSize() default 60f;

    }

    private FontAtlas() {
        throw new UnsupportedOperationException();
    }

    static void load(LauncherApp app) {
        ImFontAtlas atlas = ImGui.getIO().getFonts();
        for (Field field : FontAtlas.class.getDeclaredFields()) {
            Font fontInfo = field.getDeclaredAnnotation(Font.class);
            if (fontInfo == null) {
                continue;
            }
            try {
                IDataSource source = app.externalResource("jar://fonts/%s".formatted(fontInfo.path()),
                    "data://resources/fonts/%s".formatted(fontInfo.path()));
                if (!source.exists()) {
                    app.logger().error("Couldn't find font '{0}'", fontInfo.path());
                    continue;
                }
                ImFontConfig config = new ImFontConfig();
                config.setName(field.getName());
                ImFont font = atlas.addFontFromFileTTF(IOUtil.asPath(source).toString(), fontInfo.defaultSize(), config);
                field.set(null, font);
            } catch (Throwable thr) {
                app.logger().error("Failed to load font '{0}'", thr, fontInfo.path());
            }
        }
    }

    @Font(path = "NotoSans/NotoSans-Thin.ttf")
    public static ImFont NOTO_SANS_THIN;
    @Font(path = "NotoSans/NotoSans-ExtraLight.ttf")
    public static ImFont NOTO_SANS_EXTRA_LIGHT;
    @Font(path = "NotoSans/NotoSans-Light.ttf")
    public static ImFont NOTO_SANS_LIGHT;
    @Font(path = "NotoSans/NotoSans-Regular.ttf")
    public static ImFont NOTO_SANS_NORMAL;
    @Font(path = "NotoSans/NotoSans-Medium.ttf")
    public static ImFont NOTO_SANS_MEDIUM;
    @Font(path = "NotoSans/NotoSans-SemiBold.ttf")
    public static ImFont NOTO_SANS_SEMI_BOLD;
    @Font(path = "NotoSans/NotoSans-Bold.ttf")
    public static ImFont NOTO_SANS_BOLD;
    @Font(path = "NotoSans/NotoSans-ExtraBold.ttf")
    public static ImFont NOTO_SANS_EXTRA_BOLD;

}
