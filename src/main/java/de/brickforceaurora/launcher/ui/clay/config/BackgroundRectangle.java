package de.brickforceaurora.launcher.ui.clay.config;

import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.ElementContext;
import me.lauriichan.clay4j.IElementConfig;
import me.lauriichan.snowframe.util.color.SimpleColor;

public record BackgroundRectangle(SimpleColor color, float cornerRadius) implements IElementConfig {

    @Override
    public int priority() {
        return -2;
    }

    @Override
    public void buildOpenCommands(ElementContext context, Element element, IElementConfig elementConfig) {
        context.emitRectangle(true);
    }

}
