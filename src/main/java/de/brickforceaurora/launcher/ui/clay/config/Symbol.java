package de.brickforceaurora.launcher.ui.clay.config;

import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.ElementContext;
import me.lauriichan.clay4j.IElementConfig;
import me.lauriichan.clay4j.RenderCommand;
import me.lauriichan.snowframe.util.color.SimpleColor;

public record Symbol(SymbolType type, SimpleColor color, float thickness) implements IElementConfig {
    
    public Symbol(SymbolType type, SimpleColor color) {
        this(type, color, 1f);
    }

    public static enum SymbolType {
        
        ARROW,
        CROSS;
        
    }
    
    @Override
    public void buildCommands(ElementContext context, Element element, IElementConfig elementConfig) {
        context.push(new RenderCommand("symbol", element, context.boundingBox(), this));
    }
    
}
