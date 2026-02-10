package de.brickforceaurora.launcher.util;

import de.brickforceaurora.launcher.ui.imgui.ImGuiConsole.LogHistory;
import de.brickforceaurora.launcher.ui.imgui.ImGuiConsole.LogType;
import me.lauriichan.snowframe.util.logger.IDelegateLogger;

public final class ConsoleDelegateLogger implements IDelegateLogger {

    private final LogHistory history;

    public ConsoleDelegateLogger(LogHistory history) {
        this.history = history;
    }

    @Override
    public void info(String message) {
        history.addEntry(LogType.INFO, message);
    }

    @Override
    public void warning(String message) {
        history.addEntry(LogType.WARNING, message);
    }

    @Override
    public void error(String message) {
        history.addEntry(LogType.ERROR, message);
    }

    @Override
    public void track(String message) {
        history.addEntry(LogType.TRACE, message);
    }

    @Override
    public void debug(String message) {
        history.addEntry(LogType.DEBUG, message);
    }

}
