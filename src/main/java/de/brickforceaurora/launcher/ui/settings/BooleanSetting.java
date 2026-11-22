package de.brickforceaurora.launcher.ui.settings;

import static de.brickforceaurora.launcher.ui.UIConstant.*;

import java.util.function.BooleanSupplier;
import java.util.function.Function;

import de.brickforceaurora.launcher.Constant;
import de.brickforceaurora.launcher.ui.RenderContext;
import de.brickforceaurora.launcher.ui.clay.config.Rectangle;
import de.brickforceaurora.launcher.ui.clay.config.Symbol;
import de.brickforceaurora.launcher.ui.clay.config.Symbol.SymbolType;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.ISizing;

public class BooleanSetting extends Setting {

    private final BooleanSupplier getter;
    private final Function<Boolean, Boolean> setter;

    private boolean value = false;

    public BooleanSetting(final BooleanSupplier getter, final BooleanConsumer setter) {
        this(getter, (value) -> {
            setter.accept(value.booleanValue());
            return false;
        });
    }

    public BooleanSetting(final BooleanSupplier getter, final Function<Boolean, Boolean> setter) {
        super(3f);
        this.getter = getter;
        this.setter = setter;
        update();
    }

    @Override
    public void update() {
        value = getter.getAsBoolean();
    }

    @Override
    public boolean apply() {
        return setter.apply(value);
    }

    @Override
    protected void create(final RenderContext context, final Element parent) {
        Element.Builder builder = parent.newElement();
        builder.layout().width(ISizing.percentage(1f)).height(ISizing.percentage(1f)).addConfigs(ONE_TO_ONE)
            .addConfigs(Rectangle.hollow(Constant.TEXT_COLOR, 5f));
        try (Element element = builder.build()) {
            context.actions(element).click(() -> this.value = !this.value);
            if (value) {
                builder = element.newElement();
                builder.layout().width(ISizing.percentage(1f)).height(ISizing.percentage(1f)).addConfigs(ONE_TO_ONE)
                    .addConfigs(new Symbol(SymbolType.CROSS, Constant.TEXT_COLOR, 1f));
                builder.build().close();
            }
        }
    }

}
