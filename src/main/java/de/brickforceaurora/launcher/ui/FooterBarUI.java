package de.brickforceaurora.launcher.ui;

import de.brickforceaurora.launcher.Constant;
import imgui.ImGui;
import imgui.flag.ImGuiCol;
import me.lauriichan.applicationbase.app.extension.Extension;
import me.lauriichan.applicationbase.app.ui.dock.DockUIExtension;
import me.lauriichan.applicationbase.app.util.instance.Shared;

@Shared
@Extension
public class FooterBarUI extends DockUIExtension {

    public FooterBarUI() {
        super("FooterBar", "footer");
    }

    @Override
    protected void pushWindowStyle() {
        ImGui.pushStyleColor(ImGuiCol.WindowBg, Constant.WINDOW_BACKGROUND_COLOR.asABGR());
    }
    
    @Override
    protected void renderContent(long windowHandle) {
        
    }
    
    @Override
    protected void popWindowStyle() {
        ImGui.popStyleColor(1);
    }
    
}
