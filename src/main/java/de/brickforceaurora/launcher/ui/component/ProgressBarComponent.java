package de.brickforceaurora.launcher.ui.component;

import de.brickforceaurora.launcher.ui.renderer.PercentageClippedRenderer;
import imgui.ImDrawList;
import me.lauriichan.applicationbase.app.ui.component.Component;
import me.lauriichan.applicationbase.app.ui.component.property.PropFloat;
import me.lauriichan.applicationbase.app.ui.component.renderer.BoxRenderer;

public class ProgressBarComponent extends Component<ProgressBarComponent> {

    public final PercentageClippedRenderer foreground = new PercentageClippedRenderer();
    public final PropFloat progress = foreground.widthPercentage;

    public ProgressBarComponent() {
        foreground.heightPercentage.set(1f);
        BoxRenderer bar = new BoxRenderer();
        bar.padding.set(2f);
        background.set(bar);
        bar = new BoxRenderer();
        bar.padding.set(4f);
        foreground.set(bar);
    }

    @Override
    protected void renderForeground(ImDrawList drawList, float x, float y, float gx, float gy, float width, float height, int layerOffset) {
        foreground.render(drawList, gx, gy, width, height, layerOffset);
    }

    @Override
    protected int foregroundLayerAmount() {
        return foreground.layerAmount();
    }

}
