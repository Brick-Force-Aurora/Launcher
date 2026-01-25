package de.brickforceaurora.launcher.ui.clay.config;

import me.lauriichan.clay4j.BoundingBox;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.ElementContext;
import me.lauriichan.clay4j.IElementConfig;
import me.lauriichan.clay4j.IElementData;
import me.lauriichan.clay4j.RenderCommand;

public record ProgressClip(float horizontalProgress, float verticalProgress) implements IElementConfig {

    private static class ProgressClipData implements IElementData {

        private BoundingBox clippingBox;

    }

    // Why?
    public static ProgressClip both(float progress) {
        return new ProgressClip(progress, progress);
    }

    public static ProgressClip horizontal(float progress) {
        return new ProgressClip(progress, 1f);
    }

    public static ProgressClip vertical(float progress) {
        return new ProgressClip(1f, progress);
    }

    @Override
    public int priority() {
        return -10;
    }

    @Override
    public IElementData buildData(Element element) {
        return new ProgressClipData();
    }

    @Override
    public void buildOpenCommands(ElementContext context, Element element, IElementConfig elementConfig) {
        ProgressClipData clipData = element.data(ProgressClipData.class).get();
        BoundingBox bb = context.boundingBox();
        clipData.clippingBox = new BoundingBox(0, 0, Math.min(bb.width() * horizontalProgress, bb.width()),
            Math.min(bb.height() * verticalProgress, bb.height()));
        context.push(new RenderCommand(RenderCommand.CLIPPING_START_ID, element, clipData.clippingBox));
    }

    @Override
    public void buildCloseCommands(ElementContext context, Element element, IElementConfig elementConfig) {
        context.push(new RenderCommand(RenderCommand.CLIPPING_END_ID, element, element.data(ProgressClipData.class).get().clippingBox));
    }

}
