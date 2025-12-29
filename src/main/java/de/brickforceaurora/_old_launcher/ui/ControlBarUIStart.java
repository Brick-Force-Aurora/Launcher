//package de.brickforceaurora.old_launcher.ui;
//
//import java.util.concurrent.TimeUnit;
//
//import de.brickforceaurora.launcher.helper.UIActionHelper;
//import de.brickforceaurora.old_launcher.Constant;
//import de.brickforceaurora.old_launcher.FontAtlas;
//import de.brickforceaurora.old_launcher.LauncherApp;
//import de.brickforceaurora.old_launcher.ui.renderer.ArrowRenderer;
//import imgui.ImGui;
//import imgui.flag.ImGuiCol;
//import me.lauriichan.applicationbase.app.extension.Extension;
//import me.lauriichan.applicationbase.app.ui.animation.Animation;
//import me.lauriichan.applicationbase.app.ui.animation.animator.IAnimationAnimator;
//import me.lauriichan.applicationbase.app.ui.animation.function.IAnimationFunction;
//import me.lauriichan.applicationbase.app.ui.animation.interpolator.IAnimationInterpolator;
//import me.lauriichan.applicationbase.app.ui.animation.trigger.IAnimationTrigger;
//import me.lauriichan.applicationbase.app.ui.component.CustomButton;
//import me.lauriichan.applicationbase.app.ui.component.DrawContext;
//import me.lauriichan.applicationbase.app.ui.component.renderer.BoxRenderer;
//import me.lauriichan.applicationbase.app.ui.component.renderer.CompositeRenderer;
//import me.lauriichan.applicationbase.app.ui.component.renderer.TextRenderer;
//import me.lauriichan.applicationbase.app.ui.dock.DockUIExtension;
//import me.lauriichan.applicationbase.app.util.color.SimpleColor;
//import me.lauriichan.applicationbase.app.util.instance.Shared;
//
//@Shared
//@Extension
//public class ControlBarUIStart extends DockUIExtension {
//
//    private final CustomButton button = new CustomButton().setup(btn -> {
//        btn.padding.top(3f);
//        btn.padding.left(3f);
//        btn.padding.right(2f);
//        btn.padding.bottom(3f);
//        BoxRenderer bg = new BoxRenderer();
//        bg.color.set(Constant.BUTTON_COLOR);
//        bg.rounding.set(15f);
//        bg.padding.set(4f);
//        BoxRenderer bgShadow = new BoxRenderer();
//        bgShadow.color.set(Constant.BUTTON_SHADOW_COLOR);
//        bgShadow.rounding.set(15f);
//        bgShadow.padding.left(6f);
//        bgShadow.padding.top(6f);
//        bgShadow.padding.bottom(1f);
//        bgShadow.padding.right(1f);
//        btn.background.set(CompositeRenderer.builder().add(bgShadow).add(bg).build());
//        TextRenderer text = new TextRenderer();
//        text.text.set("START");
//        text.alignment.flag(TextRenderer.ALIGN_HORIZONTAL_CENTER, false);
//        text.font.set(FontAtlas.NOTO_SANS_EXTRA_BOLD);
//        text.padding.left(28f);
//        text.fontSize.set(36f);
//        ArrowRenderer arrow = new ArrowRenderer();
//        arrow.padding.top(21f);
//        arrow.padding.bottom(19f);
//        arrow.padding.left(117f);
//        arrow.padding.right(26f);
//        btn.foreground.set(CompositeRenderer.of(text, arrow));
//        btn.addAnimation(Animation.builder().trigger(IAnimationTrigger.mouseDownLeft())
//            .function(IAnimationFunction.ease().easeIn(50, TimeUnit.MILLISECONDS).easeOut(150, TimeUnit.MILLISECONDS))
//            .animators(new IAnimationAnimator[] {
//                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(bgShadow.padding.left)).start(6f).end(9f)
//                    .build(),
//                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(bgShadow.padding.top)).start(6f).end(9f)
//                    .build(),
//                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(bg.padding.left)).start(4f).end(7f)
//                    .build(),
//                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(bg.padding.top)).start(4f).end(7f).build(),
//                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(bg.padding.right)).start(4f).end(1f)
//                    .build(),
//                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(bg.padding.bottom)).start(4f).end(1f)
//                    .build(),
//                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(arrow.padding.left)).start(117f).end(120f)
//                    .build(),
//                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(arrow.padding.top)).start(21f).end(24f)
//                    .build(),
//                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(arrow.padding.right)).start(26f).end(23f)
//                    .build(),
//                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(arrow.padding.bottom)).start(19f).end(16f)
//                    .build(),
//                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(text.padding.top)).start(0f).end(6f)
//                    .build(),
//                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(text.padding.left)).start(28f).end(31.5f)
//                    .build()
//        }).build());
//        btn.addAnimation(Animation.builder().trigger(IAnimationTrigger.mouseDownLeft())
//            .function(IAnimationFunction.ease().easeIn(75, TimeUnit.MILLISECONDS).easeOut(75, TimeUnit.MILLISECONDS))
//            .animators(new IAnimationAnimator[] {
//                IAnimationAnimator.<SimpleColor>interpolation().interpolator(IAnimationInterpolator.of(bg.color))
//                    .start(Constant.BUTTON_COLOR).end(Constant.BUTTON_HIGHLIGHT_COLOR).build()
//        }).build());
//        btn.action(() -> {
//            Thread thread = new Thread(UIActionHelper::startGame);
//            thread.setDaemon(true);
//            thread.setName("GameStartThread");
//            thread.start();
//        });
//        LauncherApp.COMPONENT_TIMER.add(btn);
//    });
//
//    private final DrawContext context = DrawContext.horizontal();
//
//    public ControlBarUIStart() {
//        super("BarStart", "start");
//    }
//
//    @Override
//    protected void pushWindowStyle() {
//        ImGui.pushStyleColor(ImGuiCol.WindowBg, Constant.BUTTON_PANEL_BACKGROUND_COLOR.asABGR());
//    }
//
//    @Override
//    protected void renderContent(long windowHandle) {
//        context.addToContext(button);
//        context.render(ImGui.getContentRegionMaxX(), ImGui.getContentRegionMaxY());
//    }
//
//    @Override
//    protected void popWindowStyle() {
//        ImGui.popStyleColor(1);
//    }
//
//}
package de.brickforceaurora._old_launcher.ui;


