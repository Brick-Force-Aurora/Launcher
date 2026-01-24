package de.brickforceaurora.launcher.ui;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import de.brickforceaurora.launcher.animation.Animation;
import de.brickforceaurora.launcher.animation.AnimationTickTimer;
import de.brickforceaurora.launcher.ui.clay.AbstractUserInterface;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.LayoutContext;
import me.lauriichan.snowframe.util.tick.TimeSync;

public final class RenderContext {

    public static final AnimationTickTimer ANIMATION_TIMER = new AnimationTickTimer();
    public static final long ANIMATION_TIMER_LENGTH = 16_666_667;
    public static final float ANIMATION_TIMER_RATIO = ANIMATION_TIMER_LENGTH / AbstractUserInterface.SECOND_IN_NANOS;
    
    static {
        // 16.666667 ms
        TimeSync sync = ANIMATION_TIMER.sync();
        sync.length(ANIMATION_TIMER_LENGTH, TimeUnit.NANOSECONDS);
        sync.pauseLength(50, TimeUnit.MILLISECONDS);
        ANIMATION_TIMER.start();
    }

    @FunctionalInterface
    public static interface IAction {

        void run(LayoutContext context, Element element, float deltaTime);

    }

    public static class Actions {

        private final Element element;
        private final ObjectArrayList<IAction> list;

        public Actions(RenderContext ctx, Element element) {
            this.element = element;
            ObjectArrayList<IAction> list = ctx.actions.get(element);
            if (list == null) {
                list = new ObjectArrayList<>(4);
                ctx.actions.put(element, list);
            }
            this.list = list;
        }

        public Actions action(IAction action) {
            list.add(Objects.requireNonNull(action));
            return this;
        }

        public Actions click(Runnable runnable) {
            list.add((layout, element, _) -> {
                if (element.isHovered() && layout.pointerState().hasJustReleased()) {
                    runnable.run();
                }
            });
            return this;
        }

        public Actions hovered(BooleanConsumer consumer) {
            list.add((_, elem, _) -> consumer.accept(elem.isHovered()));
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

    public void update(LayoutContext layout, float deltaTime) {
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

    public Animation add(Animation animation) {
        if (!animations.contains(animation)) {
            animations.add(animation);
            ANIMATION_TIMER.add(animation);
        }
        return animation;
    }
    
    public void remove(Animation animation) {
        if (animations.remove(animation)) {
            ANIMATION_TIMER.remove(animation);
        }
    }

    public Actions actions(Element element) {
        return new Actions(this, element);
    }

    public Actions actions(Element.Builder builder) {
        return new Actions(this, builder.build());
    }

}
