package de.brickforceaurora.launcher.ui;

import java.util.concurrent.TimeUnit;

import org.lwjgl.glfw.GLFW;

import de.brickforceaurora.launcher.Constant;
import de.brickforceaurora.launcher.FontAtlas;
import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.Main;
import de.brickforceaurora.launcher.animation.Animation;
import de.brickforceaurora.launcher.animation.animator.IAnimationAnimator;
import de.brickforceaurora.launcher.animation.function.IAnimationFunction;
import de.brickforceaurora.launcher.animation.interpolator.IAnimationInterpolator;
import de.brickforceaurora.launcher.animation.property.PropBool;
import de.brickforceaurora.launcher.animation.property.PropFloat;
import de.brickforceaurora.launcher.animation.property.PropString;
import de.brickforceaurora.launcher.animation.trigger.DelegateTrigger;
import de.brickforceaurora.launcher.helper.UIActionHelper;
import de.brickforceaurora.launcher.ui.clay.AbstractUserInterface;
import de.brickforceaurora.launcher.ui.clay.FontWrapper;
import de.brickforceaurora.launcher.ui.clay.config.BackgroundRectangle;
import de.brickforceaurora.launcher.ui.clay.config.Image;
import de.brickforceaurora.launcher.ui.clay.config.Panorama;
import de.brickforceaurora.launcher.ui.clay.config.ProgressClip;
import de.brickforceaurora.launcher.ui.clay.config.Rectangle;
import de.brickforceaurora.launcher.ui.clay.config.Symbol;
import de.brickforceaurora.launcher.ui.clay.config.Symbol.SymbolType;
import de.brickforceaurora.launcher.ui.clay.config.TextColor;
import de.brickforceaurora.launcher.ui.helper.Button;
import de.brickforceaurora.launcher.ui.settings.SettingsInterface;
import de.brickforceaurora.launcher.TextureAtlas;
import me.lauriichan.clay4j.LayoutContext;
import me.lauriichan.snowframe.ImGUIModule;
import me.lauriichan.snowframe.util.color.SimpleColor;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.IElementConfig.AspectRatio;
import me.lauriichan.clay4j.IElementConfig.Text;
import me.lauriichan.clay4j.IElementConfig.Text.WrapMode;
import me.lauriichan.clay4j.ISizing;
import me.lauriichan.clay4j.Layout.HAlignment;
import me.lauriichan.clay4j.Layout.LayoutDirection;
import me.lauriichan.clay4j.Layout.Padding;
import me.lauriichan.clay4j.Layout.VAlignment;

public class UserInterface extends AbstractUserInterface {

    public static final Padding NO_PADDING = new Padding(0);
    public static final BackgroundRectangle WINDOW_BG = Rectangle.bg(Constant.WINDOW_BACKGROUND_COLOR);

    public static final AspectRatio ONE_TO_ONE = new AspectRatio(1f);

    public static final float CTRL_PANEL_OPACITY = 0.85f;
    public static final BackgroundRectangle CTRL_PANEL_LEFT = Rectangle
        .bg(Constant.PROGRESS_BACKGROUND_COLOR.duplicate().alpha(CTRL_PANEL_OPACITY));
    public static final BackgroundRectangle CTRL_PANEL_RIGHT = Rectangle
        .bg(Constant.BUTTON_PANEL_BACKGROUND_COLOR.duplicate().alpha(CTRL_PANEL_OPACITY));

    public final PropBool switchPanorama = new PropBool(true);

    private final ImGUIModule guiModule;
    private final RenderContext renderContext = new RenderContext();

    private final SimpleColor exitColor = Constant.WHITE.duplicate();
    private final PropBool exitHovered = new PropBool(false);

    private final PropBool showSettings = new PropBool(false);

    private final PropBool transitionActive = new PropBool(false);
    private final PropFloat panoramaProgress = new PropFloat(0f);
    private final PropFloat transition = new PropFloat(0f);
    private volatile int currentPanoramaTexture, previousPanoramaTexture;

    public final PropFloat mainProgress = new PropFloat(0f);
    public final PropString mainText = new PropString("Checking for updates...");

    private final Button startButton = Button.builder().padding(Padding.builder().top(4).right(4).left(28).bottom(8).build())
        .action(UIActionHelper::startGame).build();
    private final Button settingsButton = Button.builder().height(ISizing.grow()).padding(NO_PADDING).action(showSettings::toggle).build();
    private final Button quitButton = Button.builder().height(ISizing.grow()).padding(NO_PADDING).action(Main::shutdown).build();

    private final SettingsInterface settingsInterface = new SettingsInterface(renderContext);

    private volatile float dragX, dragY;
    private volatile boolean dragging = false;

    public UserInterface(LauncherApp app) {
        super(app);
        this.guiModule = app.snowFrame().module(ImGUIModule.class);
        renderContext.add(Animation.builder().trigger(new DelegateTrigger(switchPanorama)).repeating(true)
            .function(IAnimationFunction.fade().fadeIn(8, TimeUnit.SECONDS).fadeOut(125, TimeUnit.MILLISECONDS)).animator(IAnimationAnimator
                .<Float>interpolation().interpolator(IAnimationInterpolator.of(panoramaProgress)).start(0f).end(1f).build())
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
        renderContext.add(Animation.builder().trigger(new DelegateTrigger(transitionActive)).regressionEnabled(false)
            .function(IAnimationFunction.fade().fadeIn(125, TimeUnit.MILLISECONDS))
            .animator(
                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(transition)).start(0f).end(1f).build())
            .onDone((_, _) -> {
                transitionActive.set(false);
                previousPanoramaTexture = currentPanoramaTexture;
                transition.set(0f);
            }).build());

        renderContext.add(Animation.builder().trigger(new DelegateTrigger(exitHovered))
            .function(IAnimationFunction.fade().fadeIn(100, TimeUnit.MILLISECONDS).fadeOut(150, TimeUnit.MILLISECONDS))
            .animator(IAnimationAnimator.<SimpleColor>interpolation().interpolator(IAnimationInterpolator.of(exitColor))
                .start(Constant.WHITE).end(Constant.RED).build())
            .build());

        startButton.setup(renderContext);
        settingsButton.setup(renderContext);
        quitButton.setup(renderContext);
    }

    @Override
    protected void updateState(LayoutContext layout, float deltaTime) {
        renderContext.tickAnimations();
        if (showSettings.get()) {
            settingsInterface.updateState(layout, deltaTime);
        }
        renderContext.update(layout, deltaTime);
    }

    @Override
    protected void createLayout(LayoutContext layout, float deltaTime) {
        Element.Builder builder = layout.newRoot();
        builder.layout().childGap(4).layoutDirection(LayoutDirection.TOP_TO_BOTTOM).padding(NO_PADDING).width(ISizing.fixed(layout.width()))
            .height(ISizing.fixed(layout.height())).childGap(0);

        try (Element root = builder.elementId("root").build()) {
            builder = root.newElement();
            builder.layout().width(ISizing.percentage(1f)).height(ISizing.fixed(32f)).addConfigs(WINDOW_BG);
            try (Element titleBar = builder.elementId("titleBar").build()) {

                renderContext.actions(titleBar).action((ctx, element, _) -> {
                    if (dragging) {
                        if (ctx.pointerState().hasJustReleased()) {
                            dragging = false;
                            return;
                        }
                    } else if (!element.isHovered() || !ctx.pointerState().hasPressed()) {
                        return;
                    }
                    dragging = true;
                    double[] cursorX = new double[1], cursorY = new double[1];
                    GLFW.glfwGetCursorPos(guiModule.windowPointer(), cursorX, cursorY);
                    if (ctx.pointerState().hasJustHappened()) {
                        dragX = (float) cursorX[0];
                        dragY = (float) cursorY[0];
                        return;
                    }
                    int[] x = new int[1], y = new int[1];
                    GLFW.glfwGetWindowPos(guiModule.windowPointer(), x, y);
                    GLFW.glfwSetWindowPos(guiModule.windowPointer(), x[0] - (int) (dragX - cursorX[0]), y[0] - (int) (dragY - cursorY[0]));
                });

                builder = titleBar.newElement();
                builder.layout().width(ISizing.grow()).height(ISizing.percentage(1f)).padding(NO_PADDING);
                try (Element leftBar = builder.elementId("titleBar_left").build()) {
                    builder = leftBar.newElement();
                    builder.layout().height(ISizing.percentage(1f)).addConfigs(ONE_TO_ONE).addConfigs(new Image(TextureAtlas.LOGO)).build();
                    builder.build().close();

                    builder = leftBar.newElement();
                    builder.layout().height(ISizing.percentage(1f)).layoutDirection(LayoutDirection.TOP_TO_BOTTOM)
                        .addConfigs(Text.builder().text("Brick-Force").fontSize(28).wrapMode(WrapMode.WRAP_NONE)
                            .font(FontWrapper.of(FontAtlas.NOTO_SANS_SEMI_BOLD)).build());
                    builder.build().close();

                }
                builder = titleBar.newElement();
                builder.layout().width(ISizing.percentage(0.2f)).height(ISizing.percentage(1f)).padding(NO_PADDING)
                    .childVerticalAlignment(VAlignment.CENTER).childHorizontalAlignment(HAlignment.RIGHT);
                try (Element rightBar = builder.elementId("titleBar_right").build()) {
                    builder = rightBar.newElement();
                    builder.layout().height(ISizing.percentage(0.5f)).addConfigs(ONE_TO_ONE)
                        .addConfigs(new Symbol(SymbolType.CROSS, exitColor, 2f));
                    renderContext.actions(builder).click(Main::shutdown).hovered(exitHovered::set).close();
                }

            }
            builder = root.newElement();
            builder.layout().width(ISizing.percentage(1f)).height(ISizing.grow()).padding(NO_PADDING).addConfigs(WINDOW_BG)
                .addConfigs(new Panorama(TextureAtlas.PANORAMA, previousPanoramaTexture, currentPanoramaTexture, transition.get()))
                .layoutDirection(LayoutDirection.TOP_TO_BOTTOM).childGap(0);
            try (Element panorama = builder.elementId("panorama").build()) {

                builder = panorama.newElement();
                builder.layout().height(ISizing.fixed(24f)).width(ISizing.fit(16f * 5, 16f * 9)).padding(new Padding(8));
                try (Element panoramaNextProgress = builder.elementId("nextImage").build()) {

                    builder = panoramaNextProgress.newElement();
                    builder.layout().height(ISizing.fixed(12f)).width(ISizing.grow())
                        .addConfigs(Rectangle.bg(WINDOW_BG.color().duplicate().alpha(0.2f), 25f))
                        .addConfigs(Rectangle.hollow(SimpleColor.sRGB(1f, 1f, 1f, 0.6f), 25f, 1.5f)).padding(new Padding(5));
                    try (Element progressNextBG = builder.build()) {

                        builder = progressNextBG.newElement();
                        builder.layout().height(ISizing.fit(8)).width(ISizing.grow())
                            .addConfigs(new ProgressClip(panoramaProgress.get(), 1f))
                            .addConfigs(Rectangle.filled(SimpleColor.sRGB(1f, 1f, 1f, 0.6f), 25f));
                        builder.build().close();

                    }

                }

                builder = panorama.newElement();
                builder.layout().width(ISizing.percentage(1f)).height(ISizing.grow());
                builder.build().close();

                builder = panorama.newElement();
                builder.layout().width(ISizing.percentage(1f)).height(ISizing.fit(80f)).padding(NO_PADDING).childGap(0);
                try (Element controlBar = builder.elementId("controlBar").build()) {
                    builder = controlBar.newElement();
                    builder.layout().width(ISizing.percentage(0.6f)).height(ISizing.percentage(1f))
                        .layoutDirection(LayoutDirection.TOP_TO_BOTTOM).childHorizontalAlignment(HAlignment.CENTER)
                        .addConfigs(CTRL_PANEL_LEFT).padding(new Padding(8)).childGap(4);
                    try (Element progress = builder.elementId("progressBar").build()) {

                        builder = progress.newElement();
                        builder.layout().height(ISizing.percentage(0.35f)).layoutDirection(LayoutDirection.TOP_TO_BOTTOM)
                            .addConfigs(Text.builder().text(mainText.get()).font(FontWrapper.of(FontAtlas.NOTO_SANS_NORMAL))
                                .alignment(HAlignment.CENTER).wrapMode(WrapMode.WRAP_NONE).fontSize(18).build());
                        builder.build().close();

                        builder = progress.newElement();
                        builder.layout().width(ISizing.percentage(1f)).height(ISizing.grow())
                            .addConfigs(Rectangle.bg(Constant.PROGRESS_BAR_FILL_EMPTY_COLOR, 5f))
                            .addConfigs(Rectangle.hollow(Constant.PROGRESS_BAR_COLOR, 5f, 2f)).padding(new Padding(2));
                        try (Element mainPorgress = builder.build()) {

                            builder = mainPorgress.newElement();
                            builder.layout().height(ISizing.percentage(1f)).width(ISizing.percentage(1f))
                                .addConfigs(new ProgressClip(mainProgress.get(), 1f))
                                .addConfigs(Rectangle.filled(Constant.PROGRESS_BAR_FILL_COLOR, 5f));
                            builder.build().close();

                        }

                    }

                    builder = controlBar.newElement();
                    builder.layout().width(ISizing.grow()).height(ISizing.percentage(1f)).addConfigs(CTRL_PANEL_RIGHT)
                        .padding(new Padding(8));
                    try (Element start = builder.elementId("start").build()) {
                        try (Element startBtn = startButton.build(renderContext, start, btnBuilder -> {
                            btnBuilder.layout().childGap(6).childVerticalAlignment(VAlignment.CENTER);
                        })) {

                            builder = startBtn.newElement();
                            builder.layout().width(ISizing.percentage(0.6f)).layoutDirection(LayoutDirection.LEFT_TO_RIGHT)
                                .addConfigs(Text.builder().text("START").font(FontWrapper.of(FontAtlas.NOTO_SANS_EXTRA_BOLD))
                                    .alignment(HAlignment.RIGHT).wrapMode(WrapMode.WRAP_NONE).fontSize(36).build())
                                .addConfigs(new TextColor(Constant.BUTTON_TEXT_COLOR));
                            builder.build().close();

                            builder = startBtn.newElement();
                            builder.layout().height(ISizing.percentage(0.6f)).width(ISizing.percentage(0.125f))
                                .layoutDirection(LayoutDirection.TOP_TO_BOTTOM)
                                .padding(Padding.builder().top(2).left(0).right(0).bottom(0).build());
                            try (Element symbolParent = builder.build()) {
                                builder = symbolParent.newElement();
                                builder.layout().height(ISizing.percentage(1f)).width(ISizing.percentage(1f))
                                    .addConfigs(new Symbol(Symbol.SymbolType.ARROW, Constant.BUTTON_TEXT_COLOR));
                                builder.build().close();
                            }

                        }
                    }

                    builder = controlBar.newElement();
                    builder.layout().width(ISizing.grow()).height(ISizing.percentage(1f)).layoutDirection(LayoutDirection.TOP_TO_BOTTOM)
                        .addConfigs(CTRL_PANEL_RIGHT).padding(new Padding(8)).childGap(8);
                    try (Element settingsQuit = builder.elementId("settingsQuit").build()) {
                        try (Element settingsBtn = settingsButton.build(renderContext, settingsQuit)) {
                            builder = settingsBtn.newElement();
                            builder.layout().layoutDirection(LayoutDirection.TOP_TO_BOTTOM)
                                .addConfigs(Text.builder().text("SETTINGS").font(FontWrapper.of(FontAtlas.NOTO_SANS_EXTRA_BOLD))
                                    .wrapMode(WrapMode.WRAP_NONE).fontSize(24).build())
                                .addConfigs(new TextColor(Constant.BUTTON_TEXT_COLOR));
                            builder.build().close();
                        }
                        try (Element quitBtn = quitButton.build(renderContext, settingsQuit)) {
                            builder = quitBtn.newElement();
                            builder.layout().layoutDirection(LayoutDirection.TOP_TO_BOTTOM)
                                .addConfigs(Text.builder().text("QUIT").font(FontWrapper.of(FontAtlas.NOTO_SANS_EXTRA_BOLD))
                                    .wrapMode(WrapMode.WRAP_NONE).fontSize(24).build())
                                .addConfigs(new TextColor(Constant.BUTTON_TEXT_COLOR));
                            builder.build().close();
                        }
                    }
                }

                builder = panorama.newElement();
                builder.layout().width(ISizing.percentage(1f)).height(ISizing.grow(0f, 32f));
                builder.build().close();
            }
        }

        if (showSettings.get()) {
            settingsInterface.createLayout(layout, deltaTime);
        }
    }

    public SettingsInterface settingsInterface() {
        return settingsInterface;
    }

}
