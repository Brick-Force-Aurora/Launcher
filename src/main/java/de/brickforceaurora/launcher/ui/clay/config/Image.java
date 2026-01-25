package de.brickforceaurora.launcher.ui.clay.config;

import de.brickforceaurora.launcher.Constant;
import de.brickforceaurora.launcher.TextureAtlas.ImTexture;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.ElementContext;
import me.lauriichan.clay4j.IElementConfig;
import me.lauriichan.clay4j.RenderCommand;
import me.lauriichan.snowframe.util.color.SimpleColor;

public record Image(ImTexture texture, SimpleColor color) implements IElementConfig {
    
    public Image(ImTexture texture) {
        this(texture, Constant.WHITE);
    }
    
    @Override
    public void buildOpenCommands(ElementContext context, Element element, IElementConfig elementConfig) {
        context.push(new RenderCommand("image", element, context.boundingBox(), this));
    }

}
