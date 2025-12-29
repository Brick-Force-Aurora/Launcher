//package de.brickforceaurora.old_launcher.ui;
//
//import java.util.concurrent.TimeUnit;
//
//import de.brickforceaurora.old_launcher.Constant;
//import de.brickforceaurora.old_launcher.LauncherApp;
//import de.brickforceaurora.old_launcher.TextureAtlas;
//import de.brickforceaurora.old_launcher.TextureAtlas.ImTexture;
//import de.brickforceaurora.old_launcher.ui.renderer.PercentageClippedRenderer;
//import imgui.ImDrawList;
//import imgui.ImGui;
//import imgui.flag.ImGuiCol;
//import imgui.flag.ImGuiStyleVar;
//import me.lauriichan.applicationbase.app.extension.Extension;
//import me.lauriichan.applicationbase.app.ui.animation.Animation;
//import me.lauriichan.applicationbase.app.ui.animation.animator.IAnimationAnimator;
//import me.lauriichan.applicationbase.app.ui.animation.function.IAnimationFunction;
//import me.lauriichan.applicationbase.app.ui.animation.interpolator.IAnimationInterpolator;
//import me.lauriichan.applicationbase.app.ui.animation.trigger.DelegateTrigger;
//import me.lauriichan.applicationbase.app.ui.component.DrawContext;
//import me.lauriichan.applicationbase.app.ui.component.ToggleButtonComponent;
//import me.lauriichan.applicationbase.app.ui.component.property.PropBool;
//import me.lauriichan.applicationbase.app.ui.component.property.PropFloat;
//import me.lauriichan.applicationbase.app.ui.component.renderer.BoxOutlineRenderer;
//import me.lauriichan.applicationbase.app.ui.component.renderer.BoxRenderer;
//import me.lauriichan.applicationbase.app.ui.component.renderer.CompositeRenderer;
//import me.lauriichan.applicationbase.app.ui.component.renderer.CrossRenderer;
//import me.lauriichan.applicationbase.app.ui.component.renderer.DelegateRenderer;
//import me.lauriichan.applicationbase.app.ui.dock.DockUIExtension;
//import me.lauriichan.applicationbase.app.util.instance.Shared;
//
//@Shared
////@Extension
//public class PanoramaUI extends DockUIExtension {
//
//    public final PropBool showSettings = new PropBool(false);
//    public final PropBool switchPanorama = new PropBool(true);
//    public final PropFloat settingsVisibility = new PropFloat(0f);
//
//    private final DrawContext settingsContext = DrawContext.vertical();
//    private final DelegateRenderer settingsBackground = new DelegateRenderer();
//
//    private final ToggleButtonComponent checkUpdates = new ToggleButtonComponent().setup((btn) -> {
//        btn.padding.left.set(4f);
//        btn.padding.top.set(4f);
//        btn.height.set(24f);
//        btn.width.set(24f);
//        btn.flags.set(0);
//
//        BoxOutlineRenderer outline = new BoxOutlineRenderer();
//        outline.color.set(Constant.WHITE);
//        outline.rounding.set(5f);
//        outline.padding.set(0f);
//        btn.disabledRenderer.set(outline);
//
//        CrossRenderer cross = new CrossRenderer();
//        cross.padding.set(4f);
//        cross.color.set(Constant.WHITE);
//        btn.enabledRenderer.set(CompositeRenderer.of(outline, cross));
//    });
//
//    private final Animation panoramaAnimation;
//    private final Animation transitionAnimation;
//
//    private final DelegateRenderer panoramaProgress = new DelegateRenderer();
//
//    private final PropBool transitionActive = new PropBool(false);
//    private final PropFloat transition = new PropFloat(0f);
//    private volatile int currentPanoramaTexture, previousPanoramaTexture;
//
//    public PanoramaUI() {
//        super("Panorama", "panorama");
//
//        BoxRenderer settingsBox = new BoxRenderer();
//        settingsBox.padding.set(4f);
//        settingsBox.padding.right.set(8f);
//        settingsBox.color.alpha(1f);
//        settingsBackground.set(settingsBox);
//
//        BoxOutlineRenderer barBG = new BoxOutlineRenderer();
//        barBG.padding.set(0f);
//        barBG.thickness.set(1f);
//        barBG.color.set(Constant.WHITE).alpha(0.25);
//        BoxRenderer barFillBG = new BoxRenderer();
//        barFillBG.padding.set(1f);
//        barFillBG.color.set(Constant.WHITE).alpha(0.1);
//        BoxRenderer barFill = new BoxRenderer();
//        barFill.padding.set(3f);
//        barFill.color.set(Constant.WHITE).alpha(0.6);
//        PercentageClippedRenderer barFillClipped = new PercentageClippedRenderer();
//        barFillClipped.set(barFill);
//        panoramaProgress.set(CompositeRenderer.of(barBG, barFillBG, barFillClipped));
//
//        LauncherApp.GENERIC_ANIMATION_TIMER.add(panoramaAnimation = Animation.builder().trigger(new DelegateTrigger(switchPanorama))
//            .repeating(true).function(IAnimationFunction.fade().fadeIn(8, TimeUnit.SECONDS).fadeOut(125, TimeUnit.MILLISECONDS))
//            .onRestart((anim, regressing) -> {
//                if (regressing) {
//                    return;
//                }
//                int current = currentPanoramaTexture;
//                if (current + 1 == TextureAtlas.PANORAMA.textures.size()) {
//                    currentPanoramaTexture = 0;
//                } else {
//                    currentPanoramaTexture = current + 1;
//                }
//                transitionActive.set(true);
//            }).animator(IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(barFillClipped.widthPercentage))
//                .start(0f).end(1f).build())
//            .build());
//        LauncherApp.GENERIC_ANIMATION_TIMER.add(transitionAnimation = Animation.builder().trigger(new DelegateTrigger(transitionActive))
//            .regressionEnabled(false).function(IAnimationFunction.fade().fadeIn(125, TimeUnit.MILLISECONDS))
//            .animator(
//                IAnimationAnimator.<Float>interpolation().interpolator(IAnimationInterpolator.of(transition)).start(0f).end(1f).build())
//            .onDone((anim, regressing) -> {
//                transitionActive.set(false);
//                previousPanoramaTexture = currentPanoramaTexture;
//                transition.set(0f);
//            }).build());
//    }
//
//    @Override
//    protected void pushWindowStyle() {
//        ImGui.pushStyleColor(ImGuiCol.WindowBg, Constant.WINDOW_BACKGROUND_COLOR.asABGR());
//    }
//
//    @Override
//    protected void renderContent(long windowHandle) {
//        int prev = previousPanoramaTexture, cur = currentPanoramaTexture;
//        transitionAnimation.trigger();
//        panoramaAnimation.trigger();
//        float width = ImGui.getContentRegionMaxX(), height = ImGui.getContentRegionMaxY();
//
//        ImTexture previousPanorama = TextureAtlas.PANORAMA.textures.get(prev);
//        ImTexture currentPanorama = TextureAtlas.PANORAMA.textures.get(cur);
//
//        showPanorama(previousPanorama, width, height, 1f - transition.get());
//        if (previousPanorama != currentPanorama) {
//            showPanorama(currentPanorama, width, height, transition.get());
//        }
//
//        ImDrawList drawList = ImGui.getForegroundDrawList();
//        drawList.channelsSplit(panoramaProgress.layerAmount());
//        panoramaProgress.render(drawList, ImGui.getWindowPosX() + 12, ImGui.getWindowPosY() + 8, 60, 12, 0);
//        drawList.channelsMerge();
//
//        // TODO: Add Aurora Logo
//        //        float imageWidth = (TextureAtlas.BANNER_WHITEBG.width / (float) TextureAtlas.BANNER_WHITEBG.height) * height * 0.225f;
//        //        ImGui.setCursorPos((width - imageWidth) / 2, height * 0.01f);
//        //        ImGui.pushStyleVar(ImGuiStyleVar.Alpha, 0.8f);
//        //        ImGui.image(TextureAtlas.BANNER_WHITEBG.id, imageWidth, height * 0.225f);
//        //        ImGui.popStyleVar();
//
////        if (settingsVisibility.get() > 0f) {
////            float ratio = 2.5f;
////            float contentWidth = width / ratio;
////            float xOffset = width - contentWidth;
////            
////            drawList.channelsSplit(settingsBackground.layerAmount());
////            settingsBackground.render(drawList, ImGui.getWindowPosX() + xOffset, ImGui.getWindowPosY(), width, height, 0);
////            drawList.channelsMerge();
////
////            ImGui.setCursorPos(xOffset, 0f);
////            ImGui.setNextItemWidth(width);
////            ImGui.beginChild("SettingsPanel");
////            settingsContext.addToContext(checkUpdates);
////            settingsContext.render(xOffset + 4f, 4f, width - 8f, height - 8f);
////            ImGui.endChild();
////        }
//    }
//
//    private void showPanorama(ImTexture texture, float width, float height, float alpha) {
//        if (alpha == 0f) {
//            return;
//        }
//        float imageWidth = (texture.width / (float) texture.height) * height;
//        ImGui.setCursorPos((width - imageWidth) / 2, 0);
//        ImGui.pushStyleVar(ImGuiStyleVar.Alpha, alpha);
//        ImGui.image(texture.id, imageWidth, height);
//        ImGui.popStyleVar();
//    }
//
//    @Override
//    protected void popWindowStyle() {
//        ImGui.popStyleColor(1);
//    }
//
//}
package de.brickforceaurora._old_launcher.ui;


