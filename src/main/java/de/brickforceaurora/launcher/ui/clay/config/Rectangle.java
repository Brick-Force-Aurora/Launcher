package de.brickforceaurora.launcher.ui.clay.config;

import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.ElementContext;
import me.lauriichan.clay4j.IElementConfig;
import me.lauriichan.clay4j.RenderCommand;
import me.lauriichan.snowframe.util.color.SimpleColor;

public record Rectangle(SimpleColor color, float cornerRadius, boolean hollow, float brushSize) implements IElementConfig {

    public static BackgroundRectangle bg(SimpleColor color) {
        return new BackgroundRectangle(color, 0f);
    }

    public static BackgroundRectangle bg(SimpleColor color, float cornerRadius) {
        return new BackgroundRectangle(color, cornerRadius);
    }

    public static Rectangle filled(SimpleColor color) {
        return new Rectangle(color, 0f, false, 1f);
    }

    public static Rectangle filled(SimpleColor color, float cornerRadius) {
        return new Rectangle(color, cornerRadius, false, 1f);
    }

    public static Rectangle hollow(SimpleColor color) {
        return new Rectangle(color, 0f, true, 1f);
    }

    public static Rectangle hollow(SimpleColor color, float cornerRadius) {
        return new Rectangle(color, cornerRadius, true, 1f);
    }

    public static Rectangle hollow(SimpleColor color, float cornerRadius, float borderSize) {
        return new Rectangle(color, cornerRadius, true, borderSize);
    }

    @Override
    public int priority() {
        return -1;
    }

    @Override
    public void buildOpenCommands(ElementContext context, Element element, IElementConfig elementConfig) {
        context.push(new RenderCommand("rectangle", element, context.boundingBox(), this));
    }

}
