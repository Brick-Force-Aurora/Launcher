package de.brickforceaurora.launcher.ui.imgui;

import imgui.ImGui;
import imgui.ImGuiStyle;
import imgui.flag.ImGuiCol;

public final class ImGuiStyler {

    private ImGuiStyler() {
        throw new UnsupportedOperationException();
    }

    public static void apply() {
        final ImGuiStyle style = ImGui.getStyle();
        applyColors(style);
        applySizes(style);
    }

    private static void applyColors(final ImGuiStyle style) {
        style.setColor(ImGuiCol.Text, 1.00f, 1.00f, 1.00f, 1.00f);
        style.setColor(ImGuiCol.TextDisabled, 0.50f, 0.50f, 0.50f, 1.00f);
        style.setColor(ImGuiCol.WindowBg, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.ChildBg, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.PopupBg, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.Border, 0.43f, 0.43f, 0.50f, 0.50f);
        style.setColor(ImGuiCol.BorderShadow, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.FrameBg, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.FrameBgHovered, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.FrameBgActive, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.TitleBg, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.TitleBgActive, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.TitleBgCollapsed, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.MenuBarBg, 0.14f, 0.14f, 0.14f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarBg, 0.02f, 0.02f, 0.02f, 0.53f);
        style.setColor(ImGuiCol.ScrollbarGrab, 0.31f, 0.31f, 0.31f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarGrabHovered, 0.41f, 0.41f, 0.41f, 1.00f);
        style.setColor(ImGuiCol.ScrollbarGrabActive, 0.51f, 0.51f, 0.51f, 1.00f);
        style.setColor(ImGuiCol.CheckMark, 0.98f, 0.00f, 0.32f, 1.00f);
        style.setColor(ImGuiCol.SliderGrab, 0.88f, 0.24f, 0.47f, 1.00f);
        style.setColor(ImGuiCol.SliderGrabActive, 0.98f, 0.00f, 0.32f, 0.73f);
        style.setColor(ImGuiCol.Button, 0.79f, 0.02f, 0.42f, 0.64f);
        style.setColor(ImGuiCol.ButtonHovered, 1.00f, 0.45f, 0.73f, 0.54f);
        style.setColor(ImGuiCol.ButtonActive, 0.98f, 0.00f, 0.32f, 0.67f);
        style.setColor(ImGuiCol.Header, 0.79f, 0.02f, 0.42f, 0.64f);
        style.setColor(ImGuiCol.HeaderHovered, 1.00f, 0.45f, 0.73f, 0.54f);
        style.setColor(ImGuiCol.HeaderActive, 0.98f, 0.00f, 0.32f, 0.67f);
        style.setColor(ImGuiCol.Separator, 0.79f, 0.02f, 0.42f, 0.28f);
        style.setColor(ImGuiCol.SeparatorHovered, 1.00f, 0.45f, 0.73f, 0.67f);
        style.setColor(ImGuiCol.SeparatorActive, 0.62f, 0.01f, 0.33f, 1.00f);
        style.setColor(ImGuiCol.ResizeGrip, 0.79f, 0.02f, 0.42f, 0.27f);
        style.setColor(ImGuiCol.ResizeGripHovered, 1.00f, 0.45f, 0.73f, 0.67f);
        style.setColor(ImGuiCol.ResizeGripActive, 0.62f, 0.01f, 0.33f, 1.00f);
        style.setColor(ImGuiCol.Tab, 0.79f, 0.02f, 0.42f, 0.64f);
        style.setColor(ImGuiCol.TabHovered, 1.00f, 0.45f, 0.73f, 0.54f);
        style.setColor(ImGuiCol.TabActive, 0.98f, 0.00f, 0.32f, 0.67f);
        style.setColor(ImGuiCol.TabUnfocused, 0.58f, 0.00f, 0.30f, 0.64f);
        style.setColor(ImGuiCol.TabUnfocusedActive, 0.71f, 0.00f, 0.23f, 0.67f);
        style.setColor(ImGuiCol.DockingPreview, 0.79f, 0.02f, 0.42f, 0.70f);
        style.setColor(ImGuiCol.DockingEmptyBg, 0.20f, 0.20f, 0.20f, 1.00f);
        style.setColor(ImGuiCol.PlotLines, 0.61f, 0.61f, 0.61f, 1.00f);
        style.setColor(ImGuiCol.PlotLinesHovered, 1.00f, 0.43f, 0.35f, 1.00f);
        style.setColor(ImGuiCol.PlotHistogram, 0.93f, 0.4f, 0.78f, 1.00f);
        style.setColor(ImGuiCol.PlotHistogramHovered, 0.86f, 0.0f, 1.00f, 1.00f);
        style.setColor(ImGuiCol.TableHeaderBg, 0.19f, 0.19f, 0.20f, 1.00f);
        style.setColor(ImGuiCol.TableBorderStrong, 0.31f, 0.31f, 0.35f, 1.00f);
        style.setColor(ImGuiCol.TableBorderLight, 0.23f, 0.23f, 0.25f, 1.00f);
        style.setColor(ImGuiCol.TableRowBg, 0.00f, 0.00f, 0.00f, 0.00f);
        style.setColor(ImGuiCol.TableRowBgAlt, 1.00f, 1.00f, 1.00f, 0.06f);
        style.setColor(ImGuiCol.TextSelectedBg, 1.00f, 0.45f, 0.73f, 0.25f);
        style.setColor(ImGuiCol.DragDropTarget, 1.00f, 1.00f, 0.00f, 0.90f);
        style.setColor(ImGuiCol.NavHighlight, 1.00f, 0.00f, 0.30f, 0.72f);
        style.setColor(ImGuiCol.NavWindowingHighlight, 1.00f, 1.00f, 1.00f, 0.70f);
        style.setColor(ImGuiCol.NavWindowingDimBg, 0.80f, 0.80f, 0.80f, 0.20f);
        style.setColor(ImGuiCol.ModalWindowDimBg, 0.80f, 0.80f, 0.80f, 0.35f);
    }

    private static void applySizes(final ImGuiStyle style) {
        style.setFrameRounding(0);
        style.setFramePadding(0, 0);
        style.setWindowPadding(0, 0);
        style.setDockingSeparatorSize(0);
        style.setChildBorderSize(0);
        style.setFrameBorderSize(0);
        style.setWindowBorderSize(0);
        style.setItemSpacing(0, 0);
        style.setCellPadding(0, 0);
        style.setTabRounding(0);
        style.setDisplayWindowPadding(0, 0);
        style.setDisplaySafeAreaPadding(0, 0);
    }

}
