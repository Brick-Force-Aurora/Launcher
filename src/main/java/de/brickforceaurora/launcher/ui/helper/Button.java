package de.brickforceaurora.launcher.ui.helper;

import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import de.brickforceaurora.launcher.Constant;
import de.brickforceaurora.launcher.animation.Animation;
import de.brickforceaurora.launcher.animation.animator.IAnimationAnimator;
import de.brickforceaurora.launcher.animation.function.IAnimationFunction;
import de.brickforceaurora.launcher.animation.interpolator.IAnimationInterpolator;
import de.brickforceaurora.launcher.animation.property.PropBool;
import de.brickforceaurora.launcher.animation.property.PropFloat;
import de.brickforceaurora.launcher.animation.trigger.DelegateTrigger;
import de.brickforceaurora.launcher.ui.RenderContext;
import de.brickforceaurora.launcher.ui.clay.config.BFButton;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.ISizing;
import me.lauriichan.clay4j.Layout.HAlignment;
import me.lauriichan.clay4j.Layout.LayoutDirection;
import me.lauriichan.clay4j.Layout.Padding;
import me.lauriichan.clay4j.Layout.VAlignment;
import me.lauriichan.snowframe.util.color.SimpleColor;

public class Button {

    public static Builder builder() {
        return new Builder();
    }

    private final PropBool hovered = new PropBool();
    private final PropFloat paddingTopLeft = new PropFloat(0);

    private final String elementId;

    private final ISizing width, height;
    private final float rounding, shadowSize;
    private final Padding padding;

    private final SimpleColor normal, shadow, highlight;
    private final SimpleColor buttonColor;

    private final Runnable action;

    private Button(String elementId, Runnable action, ISizing width, ISizing height, float rounding, float shadowSize, Padding padding,
        SimpleColor normal, SimpleColor shadow, SimpleColor highlight) {
        this.elementId = elementId;
        this.action = action;
        this.width = width;
        this.height = height;
        this.rounding = rounding;
        this.shadowSize = shadowSize;
        this.padding = padding;
        this.normal = normal;
        this.buttonColor = normal.duplicate();
        this.shadow = shadow;
        this.highlight = highlight;
    }

    public Button setup(RenderContext context) {
        context.add(Animation.builder().trigger(new DelegateTrigger(hovered))
            .function(IAnimationFunction.ease().easeIn(50, TimeUnit.MILLISECONDS).easeOut(150, TimeUnit.MILLISECONDS))
            .animators(new IAnimationAnimator[] {
                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(paddingTopLeft)).start(0f).end(shadowSize)
                    .build()
            }).build());
        context.add(Animation.builder().trigger(new DelegateTrigger(hovered))
            .function(IAnimationFunction.ease().easeIn(75, TimeUnit.MILLISECONDS).easeOut(75, TimeUnit.MILLISECONDS))
            .animators(new IAnimationAnimator[] {
                IAnimationAnimator.<SimpleColor>interpolation().interpolator(IAnimationInterpolator.of(buttonColor)).start(normal)
                    .end(highlight).build()
            }).build());
        return this;
    }

    public Element build(RenderContext context, Element parent) {
        return build(context, parent, null);
    }

    public Element build(RenderContext context, Element parent, Consumer<Element.Builder> styler) {
        Element.Builder builder = parent.newElement();
        if (elementId != null) {
            builder.elementId(elementId);
        }
        builder.layout().width(width).height(height)
            .padding(Padding.builder().left(padding.left() + (int) paddingTopLeft.get()).top(padding.top() + (int) paddingTopLeft.get())
                .right(padding.right() - (int) paddingTopLeft.get()).bottom(padding.bottom() - (int) paddingTopLeft.get()).build())
            .layoutDirection(LayoutDirection.LEFT_TO_RIGHT)
            .childVerticalAlignment(VAlignment.TOP)
            .childHorizontalAlignment(HAlignment.CENTER)
            .addConfigs(new BFButton(paddingTopLeft.get(), shadowSize, rounding, buttonColor, shadow));
        if (styler != null) {
            styler.accept(builder);
        }
        Element element = builder.build();
        context.actions(element).hoveredDown(hovered::set).click(action);
        return element;
    }

    /*
     * Builder to the bottom
     */

    public static class Builder {

        private String elementId;

        private ISizing width = ISizing.percentage(1f), height = ISizing.percentage(1f);
        private float rounding = 12.5f, shadowSize = 3f;
        private Padding padding = new Padding(4);

        private SimpleColor normal = Constant.BUTTON_COLOR, shadow = Constant.BUTTON_SHADOW_COLOR,
            highlight = Constant.BUTTON_HIGHLIGHT_COLOR;

        private Runnable action;

        private Builder() {}

        public ISizing width() {
            return width;
        }

        public Builder width(ISizing width) {
            this.width = Objects.requireNonNull(width);
            return this;
        }

        public ISizing height() {
            return height;
        }

        public Builder height(ISizing height) {
            this.height = Objects.requireNonNull(height);
            return this;
        }

        public float rounding() {
            return rounding;
        }

        public Builder rounding(float rounding) {
            this.rounding = Math.max(rounding, 0f);
            return this;
        }

        public float shadowSize() {
            return shadowSize;
        }

        public Builder shadowSize(float shadowSize) {
            this.shadowSize = Math.max(shadowSize, 0f);
            return this;
        }

        public Padding padding() {
            return padding;
        }

        public Builder padding(Padding padding) {
            this.padding = Objects.requireNonNull(padding);
            return this;
        }

        public SimpleColor normal() {
            return normal;
        }

        public Builder normal(SimpleColor normal) {
            this.normal = Objects.requireNonNull(normal);
            return this;
        }

        public SimpleColor shadow() {
            return shadow;
        }

        public Builder shadow(SimpleColor shadow) {
            this.shadow = Objects.requireNonNull(shadow);
            return this;
        }

        public SimpleColor highlight() {
            return highlight;
        }

        public Builder highlight(SimpleColor highlight) {
            this.highlight = Objects.requireNonNull(highlight);
            return this;
        }

        public Runnable action() {
            return action;
        }

        public Builder action(Runnable action) {
            this.action = Objects.requireNonNull(action);
            return this;
        }

        public String elementId() {
            return elementId;
        }

        public Builder elementId(String elementId) {
            this.elementId = Objects.requireNonNull(elementId);
            return this;
        }

        public Button build() {
            return new Button(elementId, action, width, height, rounding, shadowSize, padding, normal, shadow, highlight);
        }

    }

}
