package de.brickforceaurora.launcher.ui;

import java.util.concurrent.TimeUnit;

import de.brickforceaurora.launcher.Constant;
import de.brickforceaurora.launcher.FontAtlas;
import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.animation.Animation;
import de.brickforceaurora.launcher.animation.AnimationTickTimer;
import de.brickforceaurora.launcher.animation.animator.IAnimationAnimator;
import de.brickforceaurora.launcher.animation.function.IAnimationFunction;
import de.brickforceaurora.launcher.animation.interpolator.IAnimationInterpolator;
import de.brickforceaurora.launcher.animation.property.PropBool;
import de.brickforceaurora.launcher.animation.property.PropFloat;
import de.brickforceaurora.launcher.animation.trigger.DelegateTrigger;
import de.brickforceaurora.launcher.ui.clay.AbstractUserInterface;
import de.brickforceaurora.launcher.ui.clay.FontWrapper;
import de.brickforceaurora.launcher.ui.clay.config.Panorama;
import de.brickforceaurora.launcher.ui.clay.config.Rectangle;
import de.brickforceaurora.launcher.ui.clay.config.TextColor;
import imgui.ImGui;
import de.brickforceaurora.launcher.TextureAtlas;
import me.lauriichan.clay4j.LayoutContext;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.laylib.logger.util.StringUtil;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.IElementConfig;
import me.lauriichan.clay4j.ISizing;
import me.lauriichan.clay4j.Layout.LayoutDirection;
import me.lauriichan.clay4j.Layout.Padding;

public class UserInterface extends AbstractUserInterface {

    public static final AnimationTickTimer ANIMATION_TIMER = new AnimationTickTimer();
    public static final long ANIMATION_TIMER_LENGTH = 16_666_667;
    public static final float ANIMATION_TIMER_RATIO = ANIMATION_TIMER_LENGTH / SECOND_IN_NANOS;

    static {
        // 16.666667 ms
        ANIMATION_TIMER.setLength(ANIMATION_TIMER_LENGTH, TimeUnit.NANOSECONDS);
        ANIMATION_TIMER.setPauseLength(50, TimeUnit.MILLISECONDS);
        ANIMATION_TIMER.start();
    }

    private static final Padding NO_PADDING = new Padding(0);
    private static final Rectangle WINDOW_BG = new Rectangle(0, Constant.WINDOW_BACKGROUND_COLOR);
    private static final TextColor TEXT_WHITE = new TextColor(Constant.WHITE);

    public final PropBool switchPanorama = new PropBool(true);

    private final ISimpleLogger logger;

    private final Animation panoramaAnimation;
    private final Animation transitionAnimation;

    private final PropBool transitionActive = new PropBool(false);
    private final PropFloat transition = new PropFloat(0f);
    private volatile int currentPanoramaTexture, previousPanoramaTexture;

    public UserInterface(LauncherApp app) {
        super(app);
        this.logger = app.snowFrame().logger();
        ANIMATION_TIMER.add(panoramaAnimation = Animation.builder().trigger(new DelegateTrigger(switchPanorama)).repeating(true)
            .function(IAnimationFunction.fade().fadeIn(8, TimeUnit.SECONDS).fadeOut(125, TimeUnit.MILLISECONDS))
            .onRestart((_, regressing) -> {
                if (regressing) {
                    return;
                }
                int current = currentPanoramaTexture;
                if (current + 1 == TextureAtlas.PANORAMA.textures.size()) {
                    currentPanoramaTexture = 0;
                } else {
                    currentPanoramaTexture = current + 1;
                }
                transitionActive.set(true);
            }).build());
        ANIMATION_TIMER.add(transitionAnimation = Animation.builder().trigger(new DelegateTrigger(transitionActive))
            .regressionEnabled(false).function(IAnimationFunction.fade().fadeIn(125, TimeUnit.MILLISECONDS))
            .animator(
                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(transition)).start(0f).end(1f).build())
            .onDone((_, _) -> {
                transitionActive.set(false);
                previousPanoramaTexture = currentPanoramaTexture;
                transition.set(0f);
            }).build());
    }

    @Override
    protected void updateState(LayoutContext layout, float deltaTime) {
        if (layout.rootAmount() == 0) {
            return;
        }
        transitionAnimation.trigger();
        panoramaAnimation.trigger();
    }

    @Override
    protected void createLayout(LayoutContext layout, float deltaTime) {
        Element.Builder builder = layout.newRoot();
        builder.layout().childGap(4).layoutDirection(LayoutDirection.TOP_TO_BOTTOM).padding(NO_PADDING).width(ISizing.fixed(layout.width()))
            .height(ISizing.fixed(layout.height())).childGap(0);
        try (Element root = builder.elementId("root").build()) {
            builder = root.newElement();
            builder.layout().width(ISizing.percentage(1f)).height(ISizing.fit(32f, 48f)).padding(NO_PADDING).addConfigs(WINDOW_BG);
            try (Element titleBar = builder.elementId("titleBar").build()) {

            }
            builder = root.newElement();
            builder.layout().width(ISizing.percentage(1f)).height(ISizing.grow()).padding(NO_PADDING).addConfigs(WINDOW_BG)
                .addConfigs(new Panorama(TextureAtlas.PANORAMA, previousPanoramaTexture, currentPanoramaTexture, transition.get()))
                .layoutDirection(LayoutDirection.TOP_TO_BOTTOM).childGap(0);
            try (Element panorama = builder.elementId("panorama").build()) {

                builder = panorama.newElement();
                builder.layout().height(ISizing.fit(16f)).renderBackground(false);
                try (Element panoramaProgress = builder.elementId("nextImage").build()) {
                }

                builder = panorama.newElement();
                builder.layout().width(ISizing.percentage(1f)).height(ISizing.grow());
                try (Element spacer = builder.elementId("spacer0").build()) {
                }

                builder = panorama.newElement();
                builder.layout().width(ISizing.percentage(1f)).height(ISizing.fit(80f));
                try (Element controlBar = builder.elementId("controlBar").build()) {
                    builder = controlBar.newElement();
                    builder.layout().width(ISizing.percentage(0.6f)).height(ISizing.percentage(1f));
                    try (Element progress = builder.elementId("progressBar").build()) {

                    }

                    builder = controlBar.newElement();
                    builder.layout().width(ISizing.grow()).height(ISizing.percentage(1f));
                    try (Element start = builder.elementId("start").build()) {

                    }

                    builder = controlBar.newElement();
                    builder.layout().width(ISizing.grow()).height(ISizing.percentage(1f)).layoutDirection(LayoutDirection.TOP_TO_BOTTOM);
                    try (Element settingsQuit = builder.elementId("settingsQuit").build()) {

                    }
                }

                builder = panorama.newElement();
                builder.layout().width(ISizing.percentage(1f)).height(ISizing.grow(0f, 32f));
                try (Element spacer = builder.elementId("spacer1").build()) {

                }
            }
        }

        if (logger.isDebug()) {
            builder = layout.newRoot();
            builder.layout().childGap(4).layoutDirection(LayoutDirection.TOP_TO_BOTTOM).padding(NO_PADDING)
                .width(ISizing.fixed(layout.width())).height(ISizing.fixed(layout.height())).childGap(0);
            try (Element root = builder.elementId("debug_root").build()) {
                builder = root.newElement();
                builder.layout().width(ISizing.percentage(1f)).height(ISizing.fit(32f, 48f)).padding(NO_PADDING);
                try (Element _ = builder.elementId("debug_titleBar_spacer").build()) {
                }
                builder = root.newElement();
                builder.layout().width(ISizing.percentage(1f)).height(ISizing.grow()).layoutDirection(LayoutDirection.TOP_TO_BOTTOM);
                try (Element debug = builder.elementId("debug_overlay").build()) {
                    builder = debug.newElement();
                    builder.layout().width(ISizing.percentage(1f)).addConfigs(IElementConfig.Text.builder().text(debugText())
                        .font(FontWrapper.of(FontAtlas.NOTO_SANS_MEDIUM)).fontSize(16).build()).addConfigs(TEXT_WHITE);
                    try (Element _ = builder.build()) {
                    }
                }
            }
        }
    }
    
    private String debugText() {
        return StringUtil.format("FPS: {0}, FPM: {1}", new Object[] {
            guiModule.renderTicker().getTicksPerSecond(),
            guiModule.renderTicker().getTicksPerMinute()
        });
    }

}
