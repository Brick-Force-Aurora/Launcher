//package de.brickforceaurora.old_launcher.ui.renderer;
//
//import imgui.ImDrawList;
//import imgui.ImVec2;
//import me.lauriichan.applicationbase.app.ui.component.property.PropFloat;
//import me.lauriichan.applicationbase.app.ui.component.renderer.DelegateRenderer;
//
//public class PercentageClippedRenderer extends DelegateRenderer {
//
//    public final PropFloat clipWidth = new PropFloat(0f, 0f, 1f);
//    public final PropFloat clipHeight = new PropFloat(0f, 0f, 1f);
//
//    public final PropFloat widthPercentage = new PropFloat(1f, 0f, 1f);
//    public final PropFloat heightPercentage = new PropFloat(1f, 0f, 1f);
//
//    private final ImVec2 min = new ImVec2(), max = new ImVec2();
//
//    @Override
//    public void render(ImDrawList drawList, float x, float y, float width, float height, int layerOffset) {
//        float clipSX = x, clipW = width * widthPercentage.get();
//        float clipSY = y, clipH = height * heightPercentage.get();
//        float clipEX = x + clipW;
//        float clipEY = y + clipH;
//
//        float tmp = width * clipWidth.get();
//        if (tmp != 0f && clipW > tmp) {
//            clipSX += clipW - tmp;
//        }
//        tmp = width * clipHeight.get();
//        if (tmp != 0f && clipH > tmp) {
//            clipSY += clipH - tmp;
//        }
//
//        drawList.getClipRectMin(min);
//        drawList.getClipRectMax(max);
//        // This implementation is not perfect but good enough for now.
//        if (clipSX < min.x) {
//            clipSX = min.x;
//        }
//        if (clipEX > max.x) {
//            clipEX = max.x;
//        }
//        if (clipSY < min.y) {
//            clipSY = min.y;
//        }
//        if (clipEY > max.y) {
//            clipEY = max.y;
//        }
//        drawList.pushClipRect(clipSX, clipSY, clipEX, clipEY);
//        super.render(drawList, x, y, width, height, layerOffset);
//        drawList.popClipRect();
//    }
//
//}
package de.brickforceaurora._old_launcher.ui.renderer;


