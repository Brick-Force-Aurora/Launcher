//package de.brickforceaurora.old_launcher.ui;
//
//import de.brickforceaurora.old_launcher.Constant;
//import de.brickforceaurora.old_launcher.FontAtlas;
//import de.brickforceaurora.old_launcher.LauncherApp;
//import de.brickforceaurora.old_launcher.ui.component.ProgressBarComponent;
//import de.brickforceaurora.old_launcher.ui.renderer.PercentageClippedRenderer;
//import imgui.ImGui;
//import imgui.flag.ImGuiCol;
//import me.lauriichan.applicationbase.app.extension.Extension;
//import me.lauriichan.applicationbase.app.ui.animation.Animation;
//import me.lauriichan.applicationbase.app.ui.animation.animator.IAnimationAnimator;
//import me.lauriichan.applicationbase.app.ui.animation.function.IAnimationFunction;
//import me.lauriichan.applicationbase.app.ui.animation.trigger.IAnimationTrigger;
//import me.lauriichan.applicationbase.app.ui.component.property.*;
//import me.lauriichan.applicationbase.app.ui.component.Component;
//import me.lauriichan.applicationbase.app.ui.component.DrawContext;
//import me.lauriichan.applicationbase.app.ui.component.Text;
//import me.lauriichan.applicationbase.app.ui.component.renderer.BoxRenderer;
//import me.lauriichan.applicationbase.app.ui.component.renderer.CompositeRenderer;
//import me.lauriichan.applicationbase.app.ui.component.renderer.TextRenderer;
//import me.lauriichan.applicationbase.app.ui.dock.DockUIExtension;
//import me.lauriichan.applicationbase.app.util.instance.Shared;
//
//@Shared
//@Extension
//public class ControlBarUIProgress extends DockUIExtension {
//
//    private final Text description = new Text().setup(text -> {
//        text.text.text.set("This is still WIP (Subject to change)");
//        text.text.fontSize.set(18f);
//        text.text.font.set(FontAtlas.NOTO_SANS_MEDIUM);
//        text.text.color.set(Constant.WHITE);
//        text.text.padding.set(16f);
//        text.text.padding.top.set(4f);
//        text.text.padding.bottom.set(10f);
//        text.text.alignment.set(TextRenderer.ALIGN_VERTICAL_BOTTOM | TextRenderer.ALIGN_HORIZONTAL_CENTER);
//        BoxRenderer bg = new BoxRenderer();
//        bg.rounding.set(5f);
//        bg.padding.set(12f);
//        bg.padding.top.set(8f);
//        bg.padding.bottom.set(-8f);
//        bg.color.set(Constant.PROGRESS_BAR_COLOR);
//        BoxRenderer bgFill = new BoxRenderer();
//        bgFill.rounding.set(5f);
//        bgFill.padding.set(14f);
//        bgFill.padding.top.set(10f);
//        bgFill.padding.bottom.set(-8f);
//        bgFill.color.set(Constant.PROGRESS_BAR_FILL_EMPTY_COLOR);
//        BoxRenderer bgUnrounded = new BoxRenderer();
//        bgUnrounded.rounding.set(0f);
//        bgUnrounded.padding.set(20f);
//        bgUnrounded.padding.bottom.set(-8f);
//        bgUnrounded.color.set(Constant.PROGRESS_BAR_COLOR);
//        BoxRenderer bgUnroundedFill = new BoxRenderer();
//        bgUnroundedFill.rounding.set(0f);
//        bgUnroundedFill.padding.set(14f);
//        bgUnroundedFill.padding.top.set(16f);
//        bgUnroundedFill.padding.bottom.set(0);
//        bgUnroundedFill.color.set(Constant.PROGRESS_BAR_FILL_EMPTY_COLOR);
//        text.background.set(CompositeRenderer.of(bg, bgFill, bgUnrounded, bgUnroundedFill));
//        text.flags.set(Component.GRAB_WIDTH);
//        text.height.set(40f);
//    });
//
//    private final ProgressBarComponent progressBar = new ProgressBarComponent().setup(bar -> {
//        bar.height.set(48f);
//        bar.flags.set(Component.GRAB_WIDTH | Component.ALIGN_BOTTOM);
//        bar.padding.set(3f);
//        bar.progress.set(0f);
//        BoxRenderer bg = bar.background.get(BoxRenderer.class);
//        bg.rounding.set(5f);
//        bg.color.set(Constant.PROGRESS_BAR_COLOR);
//        BoxRenderer bgFill = new BoxRenderer();
//        bgFill.padding.set(4f);
//        bgFill.rounding.set(5f);
//        bgFill.color.set(Constant.PROGRESS_BAR_FILL_EMPTY_COLOR);
//        bar.background.set(CompositeRenderer.of(bg, bgFill));
//        BoxRenderer fill = bar.foreground.get(BoxRenderer.class);
//        fill.padding.set(4f);
//        fill.rounding.set(5f);
//        fill.color.set(Constant.PROGRESS_BAR_FILL_COLOR);
//        PercentageClippedRenderer highlightClip = new PercentageClippedRenderer();
//        BoxRenderer highlightFill = new BoxRenderer();
//        highlightFill.padding.set(4f);
//        highlightFill.rounding.set(5f);
//        highlightFill.color.set(Constant.PROGRESS_BAR_FILL_HIGHLIGHT_COLOR);
//        highlightClip.set(highlightFill);
//        highlightClip.clipWidth.set(0.05f);
//        highlightClip.widthPercentage.set(0f);
//        bar.foreground.set(CompositeRenderer.of(fill, highlightClip));
//        bar.addAnimation(Animation.builder().trigger(new IAnimationTrigger() {
//            @Override
//            public boolean isTriggered(float gx, float gy, float width, float height) {
//                return bar.progress.get() != 1f;
//            }
//        }).function(IAnimationFunction.fade().fadeIn(1.25d)).animators(new IAnimationAnimator[] {
//            new IAnimationAnimator() {
//
//                private final PropFloat widthPercentage = highlightClip.widthPercentage;
//                private final PropFloat barProgress = bar.progress;
//
//                private volatile float progressTracker = 0f;
//
//                @Override
//                public void animate(boolean regressing, double progress) {
//                    if (progressTracker == 0f && !regressing) {
//                        return;
//                    }
//                    progressTracker += LauncherApp.COMPONENT_TIMER_RATIO;
//                    widthPercentage.set(Math.max((float) progressTracker, 0f));
//                    if (progressTracker >= barProgress.get() + LauncherApp.COMPONENT_TIMER_RATIO) {
//                        progressTracker = LauncherApp.COMPONENT_TIMER_RATIO * -10;
//                        if (!regressing) {
//                            widthPercentage.set(0);
//                        }
//                    }
//                }
//            }
//        }).repeating(true).build());
//        LauncherApp.COMPONENT_TIMER.add(bar);
//    });
//
//    public final PropString progressText = description.text.text;
//    public final PropFloat progress = progressBar.progress;
//
//    private final DrawContext context = DrawContext.vertical();
//
//    public ControlBarUIProgress() {
//        super("BarProgress", "progress");
//    }
//
//    @Override
//    protected void pushWindowStyle() {
//        ImGui.pushStyleColor(ImGuiCol.WindowBg, Constant.PROGRESS_BACKGROUND_COLOR.asABGR());
//    }
//
//    @Override
//    protected void renderContent(long windowHandle) {
//        context.addToContext(description);
//        context.addToContext(progressBar);
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


