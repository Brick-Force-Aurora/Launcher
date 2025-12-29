package de.brickforceaurora.launcher.ui;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.TextureAtlas;
import de.brickforceaurora.launcher.ui.clay.ClayDockExtension;
import de.brickforceaurora.launcher.ui.clay.config.Panorama;
import de.brickforceaurora.launcher.ui.clay.config.Rectangle;
import me.lauriichan.applicationbase.app.extension.Extension;
import me.lauriichan.applicationbase.app.ui.animation.Animation;
import me.lauriichan.applicationbase.app.ui.animation.animator.IAnimationAnimator;
import me.lauriichan.applicationbase.app.ui.animation.function.IAnimationFunction;
import me.lauriichan.applicationbase.app.ui.animation.interpolator.IAnimationInterpolator;
import me.lauriichan.applicationbase.app.ui.animation.trigger.DelegateTrigger;
import me.lauriichan.applicationbase.app.ui.component.property.PropBool;
import me.lauriichan.applicationbase.app.ui.component.property.PropFloat;
import me.lauriichan.applicationbase.app.util.color.SimpleColor;
import me.lauriichan.applicationbase.app.util.instance.Shared;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.ISizing;
import me.lauriichan.clay4j.Layout.LayoutDirection;
import me.lauriichan.clay4j.Layout.Padding;
import me.lauriichan.clay4j.LayoutContext;

@Shared
@Extension
public class ClayUI extends ClayDockExtension {

    private static final long SEED = 325239523;
    private static final Random RANDOM = new Random();

    private static SimpleColor nextColor() {
        return nextColor(1d);
    }

    private static SimpleColor nextColor(double alpha) {
        return SimpleColor.sRGB(RANDOM.nextDouble(), RANDOM.nextDouble(), RANDOM.nextDouble(), alpha);
    }

    private static final Padding NO_PADDING = new Padding(0);
    
    public final PropBool switchPanorama = new PropBool(true);
    
    private final Animation panoramaAnimation;
    private final Animation transitionAnimation;

    private final PropBool transitionActive = new PropBool(false);
    private final PropFloat transition = new PropFloat(0f);
    private volatile int currentPanoramaTexture, previousPanoramaTexture;

    public ClayUI(LauncherApp app) {
        super("Clay", "panorama", app);
        LauncherApp.GENERIC_ANIMATION_TIMER.add(panoramaAnimation = Animation.builder().trigger(new DelegateTrigger(switchPanorama))
            .repeating(true).function(IAnimationFunction.fade().fadeIn(8, TimeUnit.SECONDS).fadeOut(125, TimeUnit.MILLISECONDS))
            .onRestart((anim, regressing) -> {
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
        LauncherApp.GENERIC_ANIMATION_TIMER.add(transitionAnimation = Animation.builder().trigger(new DelegateTrigger(transitionActive))
            .regressionEnabled(false).function(IAnimationFunction.fade().fadeIn(125, TimeUnit.MILLISECONDS))
            .animator(
                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(transition)).start(0f).end(1f).build())
            .onDone((anim, regressing) -> {
                transitionActive.set(false);
                previousPanoramaTexture = currentPanoramaTexture;
                transition.set(0f);
            }).build());
    }

    @Override
    protected void renderContent(LayoutContext layout, long windowHandle) {
        RANDOM.setSeed(SEED);
        Element.Builder builder = layout.newRoot();
        builder.layout().childGap(4).layoutDirection(LayoutDirection.TOP_TO_BOTTOM).padding(NO_PADDING).width(ISizing.fixed(layout.width()))
            .height(ISizing.fixed(layout.height())).childGap(0);
        try (Element root = builder.elementId("root").build()) {
            builder = root.newElement();
            builder.layout().width(ISizing.percentage(1f)).height(ISizing.fit(32f, 48f)).padding(NO_PADDING).renderBackground(false)
                .addConfigs(new Rectangle(0, nextColor()));
            try (Element titleBar = builder.elementId("titleBar").build()) {

            }
            builder = root.newElement();
            builder.layout().width(ISizing.percentage(1f)).height(ISizing.grow()).padding(NO_PADDING).renderBackground(false)
                .addConfigs(new Rectangle(0, nextColor()))
                .addConfigs(new Panorama(TextureAtlas.PANORAMA, previousPanoramaTexture, currentPanoramaTexture, transition.get()))
                .layoutDirection(LayoutDirection.TOP_TO_BOTTOM).childGap(0);
            try (Element panorama = builder.elementId("panorama").build()) {

                builder = panorama.newElement();
                builder.layout().height(ISizing.fit(16f)).renderBackground(false).addConfigs(new Rectangle(0, nextColor()));
                try (Element panoramaProgress = builder.elementId("nextImage").build()) {
                }

                builder = panorama.newElement();
                builder.layout().width(ISizing.percentage(1f)).height(ISizing.grow()).renderBackground(false)
                    .addConfigs(new Rectangle(0, nextColor()));
                try (Element spacer = builder.elementId("spacer0").build()) {
                }

                builder = panorama.newElement();
                builder.layout().width(ISizing.percentage(1f)).height(ISizing.fit(80f)).renderBackground(true)
                    .addConfigs(new Rectangle(0, nextColor(0.4)));
                try (Element controlBar = builder.elementId("controlBar").build()) {
                    builder = controlBar.newElement();
                    builder.layout().width(ISizing.percentage(0.6f)).height(ISizing.percentage(1f)).renderBackground(false)
                        .addConfigs(new Rectangle(0, nextColor()));
                    try (Element progress = builder.elementId("progressBar").build()) {

                    }

                    builder = controlBar.newElement();
                    builder.layout().width(ISizing.grow()).height(ISizing.percentage(1f)).renderBackground(false)
                        .addConfigs(new Rectangle(0, nextColor()));
                    try (Element start = builder.elementId("start").build()) {

                    }

                    builder = controlBar.newElement();
                    builder.layout().width(ISizing.grow()).height(ISizing.percentage(1f)).layoutDirection(LayoutDirection.TOP_TO_BOTTOM)
                        .renderBackground(false).addConfigs(new Rectangle(0, nextColor()));
                    try (Element settingsQuit = builder.elementId("settingsQuit").build()) {

                    }
                }

                builder = panorama.newElement();
                builder.layout().width(ISizing.percentage(1f)).height(ISizing.grow(0f, 32f)).renderBackground(false)
                    .addConfigs(new Rectangle(0, nextColor()));
                try (Element spacer = builder.elementId("spacer1").build()) {

                }
            }
        }
    }

    @Override
    protected void updateContent(float deltaTime) {
        transitionAnimation.trigger();
        panoramaAnimation.trigger();
    }

}
