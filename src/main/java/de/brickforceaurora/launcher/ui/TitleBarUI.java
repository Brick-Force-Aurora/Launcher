package de.brickforceaurora.launcher.ui;

import java.util.concurrent.TimeUnit;

import org.lwjgl.glfw.GLFW;

import de.brickforceaurora.launcher.Constant;
import de.brickforceaurora.launcher.FontAtlas;
import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.TextureAtlas;
import imgui.ImGui;
import imgui.ImVec2;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiMouseButton;
import imgui.flag.ImGuiStyleVar;
import me.lauriichan.applicationbase.app.extension.Extension;
import me.lauriichan.applicationbase.app.ui.animation.Animation;
import me.lauriichan.applicationbase.app.ui.animation.animator.IAnimationAnimator;
import me.lauriichan.applicationbase.app.ui.animation.function.IAnimationFunction;
import me.lauriichan.applicationbase.app.ui.animation.interpolator.IAnimationInterpolator;
import me.lauriichan.applicationbase.app.ui.animation.trigger.IAnimationTrigger;
import me.lauriichan.applicationbase.app.ui.component.Component;
import me.lauriichan.applicationbase.app.ui.component.CustomButton;
import me.lauriichan.applicationbase.app.ui.component.DrawContext;
import me.lauriichan.applicationbase.app.ui.component.Image;
import me.lauriichan.applicationbase.app.ui.component.Text;
import me.lauriichan.applicationbase.app.ui.component.renderer.CrossRenderer;
import me.lauriichan.applicationbase.app.ui.component.renderer.TextRenderer;
import me.lauriichan.applicationbase.app.ui.dock.DockUIExtension;
import me.lauriichan.applicationbase.app.util.color.SimpleColor;
import me.lauriichan.applicationbase.app.util.instance.Shared;

@Shared
@Extension
public class TitleBarUI extends DockUIExtension {

    private final int[] x = new int[1], y = new int[1];
    private volatile boolean isDragging = false, isDraggingTitleBar = false;

    private final CustomButton button = new CustomButton().setup(btn -> {
        CrossRenderer renderer = new CrossRenderer();
        renderer.padding.set(4f);
        renderer.thickness.set(1.25f);
        renderer.color.set(Constant.WHITE);
        btn.foreground.set(renderer);
        btn.flags.flag(Component.ALIGN_RIGHT, true);
        btn.flags.flag(Component.MIRROR_HEIGHT, true);
        btn.addAnimation(Animation.builder().trigger(IAnimationTrigger.hover())
            .function(IAnimationFunction.fade().fadeIn(100, TimeUnit.MILLISECONDS).fadeOut(150, TimeUnit.MILLISECONDS))
            .animator(IAnimationAnimator.<SimpleColor>interpolation().interpolator(IAnimationInterpolator.of(renderer.color))
                .start(Constant.WHITE).end(Constant.RED).build())
            .build());
        btn.action(() -> LauncherApp.app().shutdownUI());
        LauncherApp.COMPONENT_TIMER.add(btn);
    });
    
    private final Image logo = new Image().setup(image -> {
        image.image.padding.set(4f);
        image.image.texture.set(TextureAtlas.LOGO.id);
        image.flags.flag(Component.MIRROR_HEIGHT, true);
    });
    
    private final Text title = new Text().setup(text -> {
        text.text.text.set("Brick-Force");
        text.text.fontSize.set(24f);
        text.text.font.set(FontAtlas.NOTO_SANS_MEDIUM);
        text.text.color.set(Constant.WHITE);
        text.text.padding.set(4f);
        text.text.padding.left(-2f);
        text.text.padding.bottom(6f);
        text.text.alignment.flag(TextRenderer.ALIGN_HORIZONTAL_CENTER, false);
        text.flags.flag(Component.GRAB_WIDTH, false);
        text.width.set(116f);
    });

    private final DrawContext context = DrawContext.horizontal();

    public TitleBarUI() {
        super("TitleBar", "titlebar");
    }

    @Override
    protected void pushWindowStyle() {
        ImGui.pushStyleColor(ImGuiCol.WindowBg, Constant.WINDOW_BACKGROUND_COLOR.asABGR());
        ImGui.pushStyleVar(ImGuiStyleVar.ChildBorderSize, 0);
    }

    @Override
    protected void renderContent(long windowHandle) {
        handleDrag(windowHandle);
        
        float width = ImGui.getContentRegionMaxX(), height = ImGui.getContentRegionMaxY();

        context.addToContext(logo);
        context.addToContext(title);
        context.addToContext(button);
        context.render(width, height);
    }

    private void handleDrag(long windowHandle) {
        if (ImGui.isMouseDragging(ImGuiMouseButton.Left)) {
            if (!isDragging) {
                isDragging = true;
                ImVec2 pos = ImGui.getWindowPos();
                ImVec2 size = ImGui.getWindowSize();
                if (!ImGui.isMouseHoveringRect(pos.x, pos.y, pos.x + size.x, pos.y + size.y)) {
                    return;
                }
                GLFW.glfwGetWindowPos(windowHandle, x, y);
                isDraggingTitleBar = true;
            }
            if (!isDraggingTitleBar) {
                return;
            }
            GLFW.glfwSetWindowPos(windowHandle, x[0] + Math.round(ImGui.getMouseDragDeltaX()),
                y[0] + Math.round(ImGui.getMouseDragDeltaY()));
        } else if (isDragging) {
            isDragging = false;
            isDraggingTitleBar = false;
        }
    }

    @Override
    protected void popWindowStyle() {
        ImGui.popStyleVar(1);
        ImGui.popStyleColor(1);
    }

}
