package de.brickforceaurora.launcher.util;

import java.io.BufferedReader;
import java.io.IOException;

import de.brickforceaurora.launcher.LauncherApp;
import me.lauriichan.laylib.logger.ISimpleLogger;

public final class ProcessUtil {

    public static record ProgramResult(Exception exception, int code, String result, String error) {}

    private ProcessUtil() {
        throw new UnsupportedOperationException();
    }

    public static ProgramResult run(String[] command) {
        ISimpleLogger logger = LauncherApp.logger();
        try {
            final Process process = new ProcessBuilder(command).start();
            BufferedReader inputReader = process.inputReader(), errorReader = process.inputReader();
            int cycles = 100;
            StringBuilder resultBuilder = new StringBuilder(), errorBuilder = new StringBuilder();
            while (process.isAlive()) {
                if (inputReader.ready()) {
                    resultBuilder.append(inputReader.readAllAsString());
                }
                if (errorReader.ready()) {
                    errorBuilder.append(errorReader.readAllAsString());
                }
                Thread.sleep(5);
                if (cycles-- == 0) {
                    break;
                }
            }
            if (process.isAlive()) {
                process.destroy();
            }
            if (inputReader.ready()) {
                resultBuilder.append(inputReader.readAllAsString());
            }
            if (errorReader.ready()) {
                errorBuilder.append(errorReader.readAllAsString());
            }
            int code = -1;
            if (!process.isAlive()) {
                code = process.exitValue();
            }
            String result = resultBuilder.toString(), error = errorBuilder.toString();
            if (logger.isDebug()) {
                logger.debug("""
                    Execution of '{3}' success
                    ==========================
                    Code: {0}
                    Error: {2}
                    Result: {1}
                    """, code, result, error, String.join(" ", command));
            }
            return new ProgramResult(null, code, result, error);
        } catch (IOException | InterruptedException e) {
            if (logger.isDebug()) {
                logger.debug("Execution of '{0}' failed", e, String.join(" ", command));
            }
            return new ProgramResult(e, -1, "", "");
        }
    }

}
