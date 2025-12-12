package de.brickforceaurora.launcher.ui.renderer;

import me.lauriichan.applicationbase.app.ui.DefaultConstants;
import me.lauriichan.applicationbase.app.ui.component.IRenderer;
import me.lauriichan.applicationbase.app.ui.component.property.PropPadding;
import imgui.ImDrawList;
import imgui.ImVec2;
import me.lauriichan.applicationbase.app.util.color.SimpleColor;

public class ArrowRenderer implements IRenderer {

    private static final ImVec2[] QUAD_1 = new ImVec2[] {
        new ImVec2(0f, 0f),
        new ImVec2(0.99f, 0.51f),
        new ImVec2(0.50f, 0.51f),
        new ImVec2(0f, 0.25f),
    };

    private static final ImVec2[] QUAD_2 = new ImVec2[] {
        new ImVec2(0f, 1f),
        new ImVec2(0f, 0.75f),
        new ImVec2(0.50f, 0.49f),
        new ImVec2(0.99f, 0.49f),
    };

    public final SimpleColor color = DefaultConstants.TEXT_COLOR.duplicate();

    public final PropPadding padding = new PropPadding();

    @Override
    public void render(ImDrawList drawList, float x, float y, float width, float height, int layerOffset) {
        float pointWidth = width - padding.right() - padding.left();
        float pointHeight = height - padding.top() - padding.bottom();
        drawList.addQuadFilled(QUAD_1[0].x * pointWidth + x + padding.left(), QUAD_1[0].y * pointHeight + y + padding.top(),
            QUAD_1[1].x * pointWidth + x + padding.left(), QUAD_1[1].y * pointHeight + y + padding.top(),
            QUAD_1[2].x * pointWidth + x + padding.left(), QUAD_1[2].y * pointHeight + y + padding.top(),
            QUAD_1[3].x * pointWidth + x + padding.left(), QUAD_1[3].y * pointHeight + y + padding.top(), color.asABGR());
        drawList.addQuadFilled(QUAD_2[0].x * pointWidth + x + padding.left(), QUAD_2[0].y * pointHeight + y + padding.top(),
            QUAD_2[1].x * pointWidth + x + padding.left(), QUAD_2[1].y * pointHeight + y + padding.top(),
            QUAD_2[2].x * pointWidth + x + padding.left(), QUAD_2[2].y * pointHeight + y + padding.top(),
            QUAD_2[3].x * pointWidth + x + padding.left(), QUAD_2[3].y * pointHeight + y + padding.top(), color.asABGR());
    }

}
