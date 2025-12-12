package de.brickforceaurora.launcher.ui;

import java.util.concurrent.TimeUnit;

import de.brickforceaurora.launcher.Constant;
import de.brickforceaurora.launcher.FontAtlas;
import de.brickforceaurora.launcher.LauncherApp;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import me.lauriichan.applicationbase.app.extension.Extension;
import me.lauriichan.applicationbase.app.ui.animation.Animation;
import me.lauriichan.applicationbase.app.ui.animation.animator.IAnimationAnimator;
import me.lauriichan.applicationbase.app.ui.animation.function.IAnimationFunction;
import me.lauriichan.applicationbase.app.ui.animation.interpolator.IAnimationInterpolator;
import me.lauriichan.applicationbase.app.ui.animation.trigger.IAnimationTrigger;
import me.lauriichan.applicationbase.app.ui.component.DrawContext;
import me.lauriichan.applicationbase.app.ui.component.LabelButton;
import me.lauriichan.applicationbase.app.ui.component.renderer.BoxRenderer;
import me.lauriichan.applicationbase.app.ui.component.renderer.CompositeRenderer;
import me.lauriichan.applicationbase.app.ui.dock.DockUIExtension;
import me.lauriichan.applicationbase.app.util.color.SimpleColor;
import me.lauriichan.applicationbase.app.util.instance.Shared;

@Shared
@Extension
public class ControlBarUISettings extends DockUIExtension {

    private final LabelButton settingsButton = new LabelButton().setup(btn -> {
        btn.padding.top(3f);
        btn.padding.left(3f);
        btn.padding.right(2f);
        BoxRenderer bg = new BoxRenderer();
        bg.color.set(Constant.BUTTON_COLOR);
        bg.rounding.set(15f);
        bg.padding.set(4f);
        BoxRenderer bgShadow = new BoxRenderer();
        bgShadow.color.set(Constant.BUTTON_SHADOW_COLOR);
        bgShadow.rounding.set(15f);
        bgShadow.padding.left(6f);
        bgShadow.padding.top(6f);
        bgShadow.padding.bottom(1f);
        bgShadow.padding.right(1f);
        btn.background.set(CompositeRenderer.builder().add(bgShadow).add(bg).build());
        btn.label.text.set("SETTINGS");
        btn.label.font.set(FontAtlas.NOTO_SANS_EXTRA_BOLD);
        btn.label.fontSize.set(24f);
        btn.addAnimation(Animation.builder().trigger(IAnimationTrigger.mouseDownLeft())
            .function(IAnimationFunction.ease().easeIn(50, TimeUnit.MILLISECONDS).easeOut(150, TimeUnit.MILLISECONDS))
            .animators(new IAnimationAnimator[] {
                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(bgShadow.padding.left)).start(6f).end(9f)
                    .build(),
                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(bgShadow.padding.top)).start(6f).end(9f)
                    .build(),
                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(bg.padding.left)).start(4f).end(7f)
                    .build(),
                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(bg.padding.top)).start(4f).end(7f).build(),
                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(bg.padding.right)).start(4f).end(1f)
                    .build(),
                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(bg.padding.bottom)).start(4f).end(1f)
                    .build(),
                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(btn.label.padding.left)).start(0f)
                    .end(4.5f).build(),
                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(btn.label.padding.top)).start(0f).end(6f)
                    .build()
        }).build());
        btn.addAnimation(Animation.builder().trigger(IAnimationTrigger.mouseDownLeft())
            .function(IAnimationFunction.ease().easeIn(75, TimeUnit.MILLISECONDS).easeOut(75, TimeUnit.MILLISECONDS))
            .animators(new IAnimationAnimator[] {
                IAnimationAnimator.<SimpleColor>interpolation().interpolator(IAnimationInterpolator.of(bg.color))
                    .start(Constant.BUTTON_COLOR).end(Constant.BUTTON_HIGHLIGHT_COLOR).build()
        }).build());
        btn.action(() -> {
            PanoramaUI panorama = ((PanoramaUI) LauncherApp.app().dockUiPool().extensions().stream()
                .filter(ui -> ui.dockId().equals("panorama")).findAny().get());
            panorama.showSettings.set(!panorama.showSettings.get());
        });
        LauncherApp.COMPONENT_TIMER.add(btn);
    });

    private final LabelButton quitButton = new LabelButton().setup(btn -> {
        btn.padding.top(3f);
        btn.padding.left(3f);
        btn.padding.right(2f);
        BoxRenderer bg = new BoxRenderer();
        bg.color.set(Constant.BUTTON_COLOR);
        bg.rounding.set(15f);
        bg.padding.set(4f);
        BoxRenderer bgShadow = new BoxRenderer();
        bgShadow.color.set(Constant.BUTTON_SHADOW_COLOR);
        bgShadow.rounding.set(15f);
        bgShadow.padding.left(6f);
        bgShadow.padding.top(6f);
        bgShadow.padding.bottom(1f);
        bgShadow.padding.right(1f);
        btn.background.set(CompositeRenderer.builder().add(bgShadow).add(bg).build());
        btn.label.text.set("QUIT");
        btn.label.font.set(FontAtlas.NOTO_SANS_EXTRA_BOLD);
        btn.label.fontSize.set(24f);
        btn.addAnimation(Animation.builder().trigger(IAnimationTrigger.mouseDownLeft())
            .function(IAnimationFunction.ease().easeIn(50, TimeUnit.MILLISECONDS).easeOut(150, TimeUnit.MILLISECONDS))
            .animators(new IAnimationAnimator[] {
                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(bgShadow.padding.left)).start(6f).end(9f)
                    .build(),
                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(bgShadow.padding.top)).start(6f).end(9f)
                    .build(),
                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(bg.padding.left)).start(4f).end(7f)
                    .build(),
                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(bg.padding.top)).start(4f).end(7f).build(),
                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(bg.padding.right)).start(4f).end(1f)
                    .build(),
                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(bg.padding.bottom)).start(4f).end(1f)
                    .build(),
                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(btn.label.padding.left)).start(0f)
                    .end(4.5f).build(),
                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(btn.label.padding.top)).start(0f).end(6f)
                    .build()
        }).build());
        btn.addAnimation(Animation.builder().trigger(IAnimationTrigger.mouseDownLeft())
            .function(IAnimationFunction.ease().easeIn(75, TimeUnit.MILLISECONDS).easeOut(75, TimeUnit.MILLISECONDS))
            .animators(new IAnimationAnimator[] {
                IAnimationAnimator.<SimpleColor>interpolation().interpolator(IAnimationInterpolator.of(bg.color))
                    .start(Constant.BUTTON_COLOR).end(Constant.BUTTON_HIGHLIGHT_COLOR).build()
        }).build());
        btn.action(() -> LauncherApp.app().shutdownUI());
        LauncherApp.COMPONENT_TIMER.add(btn);
    });

    private final DrawContext context = DrawContext.vertical().itemSpacing(0f);

    public ControlBarUISettings() {
        super("BarSettings", "settings");
    }

    @Override
    protected void pushWindowStyle() {
        ImGui.pushStyleColor(ImGuiCol.WindowBg, Constant.BUTTON_PANEL_BACKGROUND_COLOR.asABGR());
    }

    @Override
    protected void renderContent(long windowHandle) {
        context.addToContext(settingsButton);
        context.addToContext(quitButton);
        context.render(ImGui.getContentRegionMaxX(), ImGui.getContentRegionMaxY());
    }

    @Override
    protected void popWindowStyle() {
        ImGui.popStyleColor(1);
    }

}
