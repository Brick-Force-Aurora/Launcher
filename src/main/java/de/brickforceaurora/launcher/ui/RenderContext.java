package de.brickforceaurora.launcher.ui;

import java.util.Objects;

import de.brickforceaurora.launcher.animation.Animation;
import de.brickforceaurora.launcher.ui.clay.AbstractUserInterface;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.LayoutContext;
import me.lauriichan.snowframe.ImGUIModule;

public final class RenderContext {

    @FunctionalInterface
    public interface IAction {

        void run(LayoutContext context, Element element, float deltaTime);

    }

    public static class Actions {

        private final Element element;
        private final ObjectArrayList<IAction> list;

        public Actions(final RenderContext ctx, final Element element) {
            this.element = element;
            ObjectArrayList<IAction> list = ctx.actions.get(element);
            if (list == null) {
                list = new ObjectArrayList<>(4);
                ctx.actions.put(element, list);
            }
            this.list = list;
        }

        public Actions action(final IAction action) {
            list.add(Objects.requireNonNull(action));
            return this;
        }

        public Actions click(final Runnable runnable) {
            if (runnable == null) {
                return this;
            }
            list.add((layout, element, _) -> {
                if (element.isHovered() && layout.pointerState().hasJustReleased()) {
                    runnable.run();
                }
            });
            return this;
        }

        public Actions hovered(final BooleanConsumer consumer) {
            if (consumer == null) {
                return this;
            }
            list.add((_, elem, _) -> consumer.accept(elem.isHovered()));
            return this;
        }

        public Actions hoveredDown(final BooleanConsumer consumer) {
            if (consumer == null) {
                return this;
            }
            list.add((layout, elem, _) -> consumer.accept(elem.isHovered() && layout.pointerState().hasPressed()));
            return this;
        }

        public final Element done() {
            return element;
        }

        public final void close() {
            element.close();
        }

    }

    private final Object2ObjectArrayMap<Element, ObjectArrayList<IAction>> actions = new Object2ObjectArrayMap<>();
    private final ObjectList<Animation> animations = ObjectLists.synchronize(new ObjectArrayList<>());

    public void tickAnimations() {
        final double deltaSecond = ImGUIModule.DELTA_TIME.get() / AbstractUserInterface.SECOND_RATIO;
        for (final Animation animation : animations) {
            animation.update(deltaSecond);
        }
    }

    public void update(final LayoutContext layout, final float deltaTime) {
        if (layout.rootAmount() == 0) {
            actions.clear();
            return;
        }

        // Run all actions
        actions.forEach((element, actions) -> actions.forEach(action -> action.run(layout, element, deltaTime)));
        // Actions should be cleared after each update
        actions.clear();

        // Update animations
        animations.forEach(Animation::trigger);
    }

    public Animation add(final Animation animation) {
        if (!animations.contains(animation)) {
            animations.add(animation);
        }
        return animation;
    }

    public void remove(final Animation animation) {
        animations.remove(animation);
    }

    public Actions actions(final Element element) {
        return new Actions(this, element);
    }

    public Actions actions(final Element.Builder builder) {
        return new Actions(this, builder.build());
    }

}
