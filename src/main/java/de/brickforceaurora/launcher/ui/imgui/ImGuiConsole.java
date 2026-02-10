package de.brickforceaurora.launcher.ui.imgui;

import java.awt.Color;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import imgui.ImColor;
import imgui.ImDrawList;
import imgui.ImFont;
import imgui.ImGuiInputTextCallbackData;
import imgui.ImGuiStyle;
import imgui.ImVec2;
import imgui.callback.ImGuiInputTextCallback;
import imgui.flag.ImGuiCol;
import imgui.flag.ImGuiInputTextFlags;
import imgui.flag.ImGuiStyleVar;
import imgui.internal.ImGui;
import imgui.type.ImString;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import me.lauriichan.snowframe.util.color.ColorParser;

public final class ImGuiConsole {

    public static class LogType {

        public static final LogType COMMAND;
        public static final LogType INFO;
        public static final LogType WARNING;
        public static final LogType ERROR;
        public static final LogType DEBUG;
        public static final LogType TRACE;

        private static final ObjectList<LogType> INT_VALUES;
        public static final ObjectList<LogType> VALUES;

        static {
            INT_VALUES = ObjectLists.synchronize(new ObjectArrayList<>());
            VALUES = ObjectLists.unmodifiable(INT_VALUES);
            COMMAND = new LogType("#9FFF80", "COMMAND");
            INFO = new LogType("#EFEFEF", "INFO");
            WARNING = new LogType("#F7FC7E", "WARNING", "WARN");
            ERROR = new LogType("#FC4864", "ERROR", "SEVERE");
            DEBUG = new LogType("#A0EBFF", "DEBUG");
            TRACE = new LogType("#EF7C02", "TRACE");
        }

        public static LogType[] values() {
            return VALUES.toArray(LogType[]::new);
        }

        public static LogType find(String name) {
            if (name == null) {
                return LogType.COMMAND;
            }
            for (LogType type : VALUES) {
                for (String tName : type.identifiers) {
                    if (tName.equalsIgnoreCase(name)) {
                        return type;
                    }
                }
            }
            return LogType.INFO;
        }

        private final String name;
        private final ObjectList<String> identifiers;
        private volatile int color;

        public LogType(final String hex, String rawName, final String... additionalIdentifiers) {
            if (rawName == null || rawName.isBlank()) {
                throw new IllegalArgumentException("Name can't be null or empty");
            }
            String name = rawName.trim().toUpperCase();
            if (INT_VALUES.stream().anyMatch(type -> type.name.equals(name))) {
                throw new IllegalArgumentException("There is already a type with name '%s'!".formatted(name));
            }
            this.name = name;
            ObjectArrayList<String> list = new ObjectArrayList<>(additionalIdentifiers);
            list.add(0, name);
            this.identifiers = ObjectLists.unmodifiable(list);
            this.color = ImColor.rgb(ColorParser.parse(hex, Color.WHITE));
            INT_VALUES.add(this);
        }

        public String name() {
            return name;
        }

        public int color() {
            return color;
        }

        public void color(int color) {
            this.color = color;
        }

        public void color(String hex) {
            this.color = ImColor.rgb(ColorParser.parse(hex, Color.WHITE));
        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }

        @Override
        public String toString() {
            return name;
        }

    }

    public static final class LogHistory {

        private final ObjectList<LogEntry> entries = ObjectLists.synchronize(new ObjectArrayList<>());

        private volatile int maxEntries = 500;

        public int maxEntries() {
            return maxEntries;
        }

        public LogHistory maxEntries(int maxEntries) {
            this.maxEntries = Math.max(maxEntries, 25);
            while (entries.size() > this.maxEntries) {
                entries.remove(0);
            }
            return this;
        }

        public void addEntry(String content) {
            addEntryImpl(new LogEntry(content));
        }

        public void addEntry(LogType type, String content) {
            addEntryImpl(new LogEntry(type, content));
        }

        private void addEntryImpl(LogEntry entry) {
            final int maxEntries = this.maxEntries;
            while (entries.size() >= maxEntries) {
                entries.remove(0);
            }
            entries.add(entry);
        }

        public void transferEntriesTo(ObjectArrayList<LogEntry> entries) {
            entries.clear();
            entries.addAll(this.entries);
        }

        public void applyEntries(ObjectArrayList<LogEntry> entries) {
            this.entries.clear();
            entries.forEach(this::addEntryImpl);
        }

        public void clearEntries() {
            entries.clear();
        }
    }

    public static final class LogEntry {

        private static final Pattern LOG_ENTRY_PATTERN = Pattern.compile("(?:\\[[^\\]]+\\]\\s*\\[[^\\]]+\\/)([a-zA-Z]{1,7})(?:\\]\\:.+)");

        private static String extractLogTypeName(String content) {
            Matcher matcher = LOG_ENTRY_PATTERN.matcher(content);
            if (!matcher.matches()) {
                return null;
            }
            return matcher.group(1);
        }

        private final LogType type;
        private final String content;

        public LogEntry(String content) {
            this(LogType.find(extractLogTypeName(content)), content);
        }

        public LogEntry(LogType type, String content) {
            this.type = type;
            this.content = content;
        }

    }

    private static final int TEXT_SIZE = 1024 * 64;

    private static final int COMMAND_INPUT_FLAGS = ImGuiInputTextFlags.EnterReturnsTrue | ImGuiInputTextFlags.AlwaysOverwrite
        | ImGuiInputTextFlags.CallbackEdit | ImGuiInputTextFlags.CallbackHistory;
    private static final int COMMAND_INPUT_FLAGS_RO = ImGuiInputTextFlags.ReadOnly;

    private volatile int bgColor = ImColor.rgb(ColorParser.parse("#020202"));

    private volatile int scrollColor = ImColor.rgb(ColorParser.parse("#540034"));
    private volatile int scrollActiveColor = ImColor.rgb(ColorParser.parse("#740054"));
    private volatile int scrollHoverColor = ImColor.rgb(ColorParser.parse("#640044"));

    private volatile int bgEntryColorFirst = ImColor.rgba("#0F0F0F99");
    private volatile int bgEntryColorSecond = ImColor.rgba("#00000000");

    private volatile boolean allowBlank = false;
    private volatile boolean editable = true;

    private volatile LogHistory logHistory = new LogHistory();
    private volatile float prevMax = 0f;

    private volatile BiConsumer<ImGuiConsole, String> commandHandler;

    private volatile ImFont font;

    /* ImGui Text buffer */
    private ImString text;
    /* ImGui Text callback */
    private ImGuiInputTextCallback textCallback;
    /* Internal focus manager */
    private volatile boolean focusTextNext = false;

    public LogHistory logHistory() {
        return logHistory;
    }

    public void logHistory(LogHistory logHistory) {
        this.logHistory = Objects.requireNonNull(logHistory);
    }

    public BiConsumer<ImGuiConsole, String> commandHandler() {
        return commandHandler;
    }

    public void commandHandler(BiConsumer<ImGuiConsole, String> commandHandler) {
        this.commandHandler = commandHandler;
    }

    public ImFont font() {
        return font;
    }

    public void font(ImFont font) {
        this.font = font;
    }

    public boolean editable() {
        return editable;
    }

    public void editable(boolean editable) {
        this.editable = editable;
    }

    public boolean allowBlank() {
        return allowBlank;
    }

    public void allowBlank(boolean allowBlank) {
        this.allowBlank = allowBlank;
    }

    public int bgColor() {
        return bgColor;
    }

    public void bgColor(int bgColor) {
        this.bgColor = bgColor;
    }

    public int scrollColor() {
        return scrollColor;
    }

    public void scrollColor(int scrollColor) {
        this.scrollColor = scrollColor;
    }

    public int scrollActiveColor() {
        return scrollActiveColor;
    }

    public void scrollActiveColor(int scrollActiveColor) {
        this.scrollActiveColor = scrollActiveColor;
    }

    public int scrollHoverColor() {
        return scrollHoverColor;
    }

    public void scrollHoverColor(int scrollHoverColor) {
        this.scrollHoverColor = scrollHoverColor;
    }

    public int bgEntryColorFirst() {
        return bgEntryColorFirst;
    }

    public void bgEntryColorFirst(int bgEntryColorFirst) {
        this.bgEntryColorFirst = bgEntryColorFirst;
    }

    public int bgEntryColorSecond() {
        return bgEntryColorSecond;
    }

    public void bgEntryColorSecond(int bgEntryColorSecond) {
        this.bgEntryColorSecond = bgEntryColorSecond;
    }

    public void render() {
        LogHistory currentHistory = logHistory;

        if (text == null) {
            text = new ImString(TEXT_SIZE);
            text.set("> ");
            textCallback = new ImGuiInputTextCallback() {
                @Override
                public void accept(ImGuiInputTextCallbackData data) {
                    String buf = data.getBuf();
                    if (buf.startsWith("> ")) {
                        return;
                    } else if (buf.startsWith(">")) {
                        data.insertChars(1, " ");
                    } else if (buf.startsWith(" ")) {
                        data.insertChars(0, ">");
                    } else {
                        data.insertChars(0, "> ");
                    }
                }
            };
        }

        ImGui.pushStyleColor(ImGuiCol.ScrollbarBg, bgColor);
        ImGui.pushStyleColor(ImGuiCol.ScrollbarGrab, scrollColor);
        ImGui.pushStyleColor(ImGuiCol.ScrollbarGrabActive, scrollActiveColor);
        ImGui.pushStyleColor(ImGuiCol.ScrollbarGrabHovered, scrollHoverColor);
        ImGui.pushStyleVar(ImGuiStyleVar.ScrollbarRounding, 0);
        ImGui.pushStyleVar(ImGuiStyleVar.ScrollbarSize, 12);

        ImVec2 regMin = ImGui.getWindowContentRegionMin();
        ImVec2 regMax = ImGui.getWindowContentRegionMax();
        ImFont font = this.font;
        if (font != null) {
            ImGui.pushFont(font);
        } else {
            font = ImGui.getFont();
        }
        float commandHeight = font.getFontSize() + 8f;
        float width = regMax.x - regMin.x, height = regMax.y - regMin.y - commandHeight;

        ImGuiStyle style = ImGui.getStyle();
        float contentWidth = width;
        if (prevMax != 0) {
            contentWidth -= style.getScrollbarSize();
        }
        float padding = style.getItemSpacingY() / 2f;

        ImGui.beginChild("ConsoleLog", width, height);
        ImDrawList draw = ImGui.getWindowDrawList();
        regMin = draw.getClipRectMin();
        regMax = draw.getClipRectMax();
        draw.addRectFilled(regMin.x, regMin.y, regMax.x, regMax.y, bgColor);
        draw.channelsSplit(2);
        int i = 0;
        float startX = regMin.x, endX = regMin.x + contentWidth, lastY = regMin.y - padding;
        ImGui.pushTextWrapPos(contentWidth);
        for (LogEntry entry : currentHistory.entries) {
            draw.channelsSetCurrent(1);
            ImGui.pushStyleColor(ImGuiCol.Text, entry.type.color);
            ImGui.textUnformatted(entry.content);
            ImGui.popStyleColor();
            draw.channelsSetCurrent(0);
            draw.addRectFilled(startX, ImGui.getItemRectMinY() - padding, endX, lastY = ImGui.getItemRectMaxY() + padding,
                i++ % 2 == 0 ? bgEntryColorFirst : bgEntryColorSecond);
        }
        ImGui.popTextWrapPos();
        draw.channelsMerge();
        float max = ImGui.getScrollMaxY();
        float current = ImGui.getScrollY();
        if (prevMax != max && current == prevMax) {
            ImGui.setScrollY(max);
        }
        if (max == 0) {
            float entryHeight = font.getFontSize() + padding * 2;
            float offset = lastY; // We add one padding
            int amount = (int) Math.ceil(height / entryHeight);
            while (i < amount) {
                draw.addRectFilled(startX, offset, endX, offset + entryHeight, i++ % 2 == 0 ? bgEntryColorFirst : bgEntryColorSecond);
                offset += entryHeight;
            }
        }
        prevMax = max;
        ImGui.endChild();
        ImGui.separator();
        ImGui.beginChild("ConsoleCommand", width, font.getFontSize() + 8f);
        ImGui.pushItemWidth(-1f);
        ImGui.pushStyleVar(ImGuiStyleVar.FrameRounding, 0);
        ImGui.pushStyleColor(ImGuiCol.FrameBg, bgColor);
        if (focusTextNext) {
            focusTextNext = false;
            ImGui.setKeyboardFocusHere();
        }
        if (ImGui.inputText("ConsoleText", text, editable ? COMMAND_INPUT_FLAGS : COMMAND_INPUT_FLAGS_RO, textCallback)) {
            focusTextNext = true;
            String command = text.get().substring(2);
            if (allowBlank || !command.isBlank()) {
                currentHistory.addEntry(text.get());
                text.set("> ");
                if (commandHandler != null) {
                    commandHandler.accept(this, command);
                }
            }
        }
        ImGui.setItemDefaultFocus();
        ImGui.popStyleColor();
        ImGui.popStyleVar();
        ImGui.popItemWidth();
        ImGui.endChild();
        ImGui.popStyleVar(2);
        ImGui.popStyleColor(4);
        if (font != null) {
            ImGui.popFont();
        }
    }

}
