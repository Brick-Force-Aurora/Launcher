package de.brickforceaurora.launcher.ui.clay.config;

import de.brickforceaurora.launcher.TextureAtlas.ImTextureBundle;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.ElementContext;
import me.lauriichan.clay4j.IElementConfig;
import me.lauriichan.clay4j.RenderCommand;

public record Panorama(ImTextureBundle bundle, int previous, int current, float progress) implements IElementConfig {

    @Override
    public void buildOpenCommands(final ElementContext context, final Element element, final IElementConfig elementConfig) {
        context.push(new RenderCommand("panorama", element, context.boundingBox(), this));
    }

}
