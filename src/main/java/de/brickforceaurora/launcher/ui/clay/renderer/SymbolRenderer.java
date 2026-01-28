package de.brickforceaurora.launcher.ui.clay.renderer;

import de.brickforceaurora.launcher.ui.clay.ElementRenderer;
import de.brickforceaurora.launcher.ui.clay.config.Symbol;
import imgui.ImDrawList;
import imgui.ImVec2;
import me.lauriichan.clay4j.BoundingBox;
import me.lauriichan.clay4j.Element;
import me.lauriichan.snowframe.extension.Extension;

@Extension
public class SymbolRenderer extends ElementRenderer<Symbol> {

    private static final ImVec2[] QUAD_1 = {
        new ImVec2(0f, 0f),
        new ImVec2(0.99f, 0.51f),
        new ImVec2(0.50f, 0.51f),
        new ImVec2(0f, 0.25f),
    };

    private static final ImVec2[] QUAD_2 = {
        new ImVec2(0f, 1f),
        new ImVec2(0f, 0.75f),
        new ImVec2(0.50f, 0.49f),
        new ImVec2(0.99f, 0.49f),
    };

    public SymbolRenderer() {
        super("symbol", Symbol.class);
    }

    @Override
    public void render(final ImDrawList drawList, final ImVec2 offset, final Element element, final BoundingBox bb, final Symbol data) {
        final float x = offset.x + bb.x(), y = offset.y + bb.y();
        final int color = data.color().asABGR();
        switch (data.type()) {
        case ARROW -> {
            drawList.addQuadFilled(QUAD_1[0].x * bb.width() + x, QUAD_1[0].y * bb.height() + y, QUAD_1[1].x * bb.width() + x,
                QUAD_1[1].y * bb.height() + y, QUAD_1[2].x * bb.width() + x, QUAD_1[2].y * bb.height() + y, QUAD_1[3].x * bb.width() + x,
                QUAD_1[3].y * bb.height() + y, color);
            drawList.addQuadFilled(QUAD_2[0].x * bb.width() + x, QUAD_2[0].y * bb.height() + y, QUAD_2[1].x * bb.width() + x,
                QUAD_2[1].y * bb.height() + y, QUAD_2[2].x * bb.width() + x, QUAD_2[2].y * bb.height() + y, QUAD_2[3].x * bb.width() + x,
                QUAD_2[3].y * bb.height() + y, color);
        }
        case CROSS -> {
            drawList.addLine(x, y, x + bb.width(), y + bb.height(), color, data.thickness());
            drawList.addLine(x, y + bb.height(), x + bb.width(), y, color, data.thickness());
        }
        default -> {
        }
        }
    }

}
