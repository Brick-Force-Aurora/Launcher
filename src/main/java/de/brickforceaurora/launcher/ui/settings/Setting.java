package de.brickforceaurora.launcher.ui.settings;

import de.brickforceaurora.launcher.ui.RenderContext;
import me.lauriichan.clay4j.Element;

public abstract class Setting {

    private final int size;

    public Setting(final int size) {
        this.size = size;
    }

    public final int size() {
        return size;
    }

    public abstract void update();

    public abstract void apply();

    protected abstract void create(RenderContext context, Element parent);

}
