package de.brickforceaurora.launcher.ui.clay.config;

import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.ElementContext;
import me.lauriichan.clay4j.IElementConfig;
import me.lauriichan.clay4j.RenderCommand;
import me.lauriichan.snowframe.util.color.SimpleColor;

public record Symbol(SymbolType type, SimpleColor color, float thickness) implements IElementConfig {

    public Symbol(final SymbolType type, final SimpleColor color) {
        this(type, color, 1f);
    }

    public static enum SymbolType {

        ARROW,
        CROSS;

    }

    @Override
    public void buildOpenCommands(final ElementContext context, final Element element, final IElementConfig elementConfig) {
        context.push(new RenderCommand("symbol", element, context.boundingBox(), this));
    }

}
