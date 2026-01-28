package de.brickforceaurora.launcher.ui.clay.config;

import me.lauriichan.clay4j.BoundingBox;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.ElementContext;
import me.lauriichan.clay4j.IElementConfig;
import me.lauriichan.clay4j.RenderCommand;
import me.lauriichan.snowframe.util.color.SimpleColor;

public record BFButton(float paddingTopLeft, float shadowSize, float rounding, SimpleColor color, SimpleColor shadow)
    implements IElementConfig {

    @Override
    public void buildOpenCommands(final ElementContext context, final Element element, final IElementConfig elementConfig) {
        final BoundingBox bb = context.boundingBox();
        context.push(new RenderCommand("rectangle", element, new BoundingBox(bb.x() + 1 + paddingTopLeft, bb.y() + 1 + paddingTopLeft,
            bb.width() - 1 - paddingTopLeft, bb.height() - 1 - paddingTopLeft), Rectangle.filled(shadow, rounding)));
        context.push(new RenderCommand("rectangle", element,
            new BoundingBox(bb.x() + paddingTopLeft, bb.y() + paddingTopLeft, bb.width() - shadowSize, bb.height() - shadowSize),
            Rectangle.filled(color, rounding)));
    }

}
