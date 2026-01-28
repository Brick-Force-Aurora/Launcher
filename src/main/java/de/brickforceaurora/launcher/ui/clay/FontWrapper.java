package de.brickforceaurora.launcher.ui.clay;

import imgui.ImFont;
import imgui.ImVec2;
import it.unimi.dsi.fastutil.longs.Long2ObjectArrayMap;
import me.lauriichan.clay4j.IFont;

public final class FontWrapper implements IFont {

    private static volatile int ID_COUNTER = 0;
    private static final Long2ObjectArrayMap<FontWrapper> POINTER_TO_WRAPPER = new Long2ObjectArrayMap<>();

    public static FontWrapper of(final ImFont font) {
        FontWrapper wrapper = POINTER_TO_WRAPPER.get(font.ptr);
        if (wrapper == null) {
            wrapper = new FontWrapper(font);
            POINTER_TO_WRAPPER.put(font.ptr, wrapper);
        }
        return wrapper;
    }

    private final int id = ID_COUNTER++;

    private final ImFont font;
    private final ImVec2 size = new ImVec2();

    private FontWrapper(final ImFont font) {
        this.font = font;
    }

    public ImFont font() {
        return font;
    }

    @Override
    public int id() {
        return id;
    }

    @Override
    public void calculateSize(final String text, final int fontSize, final float[] size) {
        font.calcTextSizeA(this.size, fontSize, Float.MAX_VALUE, 0.0f, text);
        size[0] = this.size.x;
        size[1] = this.size.y;
    }

}
