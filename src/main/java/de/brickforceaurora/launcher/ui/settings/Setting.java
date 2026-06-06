package de.brickforceaurora.launcher.ui.settings;

import de.brickforceaurora.launcher.ui.RenderContext;
import me.lauriichan.clay4j.Element;

public abstract class Setting {

    private final float size;

    public Setting(final float size) {
        this.size = size;
    }

    public final float size() {
        return size;
    }

    public abstract void update();

    public abstract void apply();
    
    protected boolean belowLabel() {
        return false;
    }

    protected abstract void create(RenderContext context, Element parent);

}
