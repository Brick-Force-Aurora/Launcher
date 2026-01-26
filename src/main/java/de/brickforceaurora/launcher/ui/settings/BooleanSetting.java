package de.brickforceaurora.launcher.ui.settings;

import java.util.function.BooleanSupplier;

import de.brickforceaurora.launcher.Constant;
import de.brickforceaurora.launcher.ui.RenderContext;
import de.brickforceaurora.launcher.ui.UserInterface;
import de.brickforceaurora.launcher.ui.clay.config.Rectangle;
import de.brickforceaurora.launcher.ui.clay.config.Symbol;
import de.brickforceaurora.launcher.ui.clay.config.Symbol.SymbolType;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.ISizing;

public class BooleanSetting extends Setting {

    private final BooleanSupplier getter;
    private final BooleanConsumer setter;

    private boolean value = false;

    public BooleanSetting(BooleanSupplier getter, BooleanConsumer setter) {
        super(1);
        this.getter = getter;
        this.setter = setter;
        update();
    }

    @Override
    public void update() {
        value = getter.getAsBoolean();
    }

    @Override
    public void apply() {
        setter.accept(value);
    }

    @Override
    protected void create(RenderContext context, Element parent) {
        Element.Builder builder = parent.newElement();
        builder.layout().width(ISizing.percentage(1f)).height(ISizing.percentage(1f))
            .addConfigs(UserInterface.ONE_TO_ONE)
            .addConfigs(Rectangle.hollow(Constant.TEXT_COLOR, 5f));
        try (Element element = builder.build()) {
            context.actions(element).click(() -> this.value = !this.value);
            if (value) {
                builder = element.newElement();
                builder.layout().width(ISizing.percentage(1f)).height(ISizing.percentage(1f))
                    .addConfigs(UserInterface.ONE_TO_ONE)
                    .addConfigs(new Symbol(SymbolType.CROSS, Constant.TEXT_COLOR, 1f));
                builder.build().close();
            }
        }
    }

}
