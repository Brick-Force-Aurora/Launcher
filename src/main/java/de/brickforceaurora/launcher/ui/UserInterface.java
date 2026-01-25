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
import de.brickforceaurora.launcher.animation.trigger.DelegateTrigger;
import de.brickforceaurora.launcher.ui.clay.AbstractUserInterface;
import de.brickforceaurora.launcher.ui.clay.FontWrapper;
import de.brickforceaurora.launcher.ui.clay.config.Image;
import de.brickforceaurora.launcher.ui.clay.config.Panorama;
import de.brickforceaurora.launcher.ui.clay.config.Rectangle;
import de.brickforceaurora.launcher.ui.clay.config.Symbol;
import de.brickforceaurora.launcher.ui.clay.config.Symbol.SymbolType;
import de.brickforceaurora.launcher.ui.clay.config.TextColor;
import imgui.ImGui;
import imgui.flag.ImGuiKey;
import de.brickforceaurora.launcher.TextureAtlas;
import me.lauriichan.clay4j.LayoutContext;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.laylib.logger.util.StringUtil;
import me.lauriichan.snowframe.ImGUIModule;
import me.lauriichan.snowframe.util.color.SimpleColor;
import me.lauriichan.clay4j.Element;
import me.lauriichan.clay4j.IElementConfig;
import me.lauriichan.clay4j.IElementConfig.AspectRatio;
import me.lauriichan.clay4j.IElementConfig.Text;
import me.lauriichan.clay4j.IElementConfig.Text.WrapMode;
import me.lauriichan.clay4j.ISizing;
import me.lauriichan.clay4j.Layout.LayoutDirection;
import me.lauriichan.clay4j.Layout.Padding;

public class UserInterface extends AbstractUserInterface {

    private static final Padding NO_PADDING = new Padding(0);
    private static final Rectangle WINDOW_BG = new Rectangle(0, Constant.WINDOW_BACKGROUND_COLOR);
    private static final TextColor TEXT_WHITE = new TextColor(Constant.WHITE);

    private static final AspectRatio ONE_TO_ONE = new AspectRatio(1f);

    public final PropBool switchPanorama = new PropBool(true);

    private final ISimpleLogger logger;
    private final ImGUIModule guiModule;
    private final RenderContext renderContext = new RenderContext();

    private final SimpleColor exitColor = Constant.WHITE.duplicate();
    private final PropBool exitHovered = new PropBool(false);

    private final PropBool showFps = new PropBool(false);

    private final PropBool transitionActive = new PropBool(false);
    private final PropFloat transition = new PropFloat(0f);
    private volatile int currentPanoramaTexture, previousPanoramaTexture;

    private volatile float dragX, dragY;
    private volatile boolean dragging = false;

    public UserInterface(LauncherApp app) {
        super(app);
        this.logger = app.snowFrame().logger();
        this.guiModule = app.snowFrame().module(ImGUIModule.class);
        renderContext.add(Animation.builder().trigger(new DelegateTrigger(switchPanorama)).repeating(true)
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
    }

    @Override
    protected void updateState(LayoutContext layout, float deltaTime) {
        if (layout.rootAmount() == 0) {
            renderContext.update(layout, deltaTime);
            return;
        }

        if (ImGui.isKeyPressed(ImGuiKey.Enter, false)) {
            showFps.set(!showFps.get());
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
            builder.layout().width(ISizing.percentage(1f)).height(ISizing.fixed(32f)).padding(NO_PADDING).addConfigs(WINDOW_BG);
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
                builder.layout().width(ISizing.grow()).height(ISizing.percentage(1f));
                try (Element leftBar = builder.elementId("titleBar_left").build()) {
                    builder = leftBar.newElement();
                    builder.layout().height(ISizing.percentage(1f)).addConfigs(ONE_TO_ONE).addConfigs(new Image(TextureAtlas.LOGO)).build();
                    builder.build().close();

                    builder = leftBar.newElement();
                    builder.layout().height(ISizing.percentage(1f)).addConfigs(Text.builder().text("BrickForce").fontSize(28)
                        .wrapMode(WrapMode.WRAP_NONE).font(FontWrapper.of(FontAtlas.NOTO_SANS_SEMI_BOLD)).build());
                    builder.build().close();

                }
                builder = titleBar.newElement();
                builder.layout().height(ISizing.percentage(1f));
                try (Element rightBar = builder.elementId("titleBar_right").build()) {
                    builder = rightBar.newElement();
                    builder.layout().width(ISizing.fixed(32)).height(ISizing.percentage(1f)).addConfigs(ONE_TO_ONE)
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

        if (showFps.get()) {
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
            guiModule.renderTicker().sync().tps(),
            guiModule.renderTicker().sync().tpm()
        });
    }

}
