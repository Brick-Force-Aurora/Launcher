package de.brickforceaurora.launcher.platform.linux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.util.ProcessUtil;
import de.brickforceaurora.launcher.util.ProcessUtil.ProgramResult;

public final class ProtonInstance {

    private final String protonPath, compatPath, steamPath, wineTricksPath;

    public ProtonInstance(Path protonPath, Path compatPath, Path steamPath) {
        this.protonPath = protonPath.resolve("proton").toAbsolutePath().toString();
        this.compatPath = compatPath.toAbsolutePath().toString();
        this.steamPath = steamPath.toAbsolutePath().toString();
        Path wineTricksPath = protonPath.resolve("protonfixes/winetricks");
        this.wineTricksPath = Files.exists(wineTricksPath) ? wineTricksPath.toAbsolutePath().toString() : null;
    }

    public String wineTricksPath() {
        return wineTricksPath;
    }

    public ProgramResult executeProtonAwait(String command, String... arguments) throws IOException, InterruptedException {
        return executeProtonAwait(null, command, arguments);
    }

    public ProgramResult executeProtonAwait(Path directory, String command, String... arguments) throws IOException, InterruptedException {
        return ProcessUtil.await(createProcess(directory, command, arguments));
    }

    public Process executeProton(String command, String... arguments) throws IOException {
        return executeProton(null, command, arguments);
    }

    public Process executeProton(Path directory, String command, String... arguments) throws IOException {
        return createProcess(directory, command, arguments).start();
    }

    public ProgramResult executeAwait(String... arguments) throws IOException, InterruptedException {
        return executeAwait(null, arguments);
    }

    public ProgramResult executeAwait(Path directory, String... arguments) throws IOException, InterruptedException {
        return ProcessUtil.await(createProcessForWindowsCommands(directory, arguments));
    }

    public Process execute(String... arguments) throws IOException {
        return execute(null, arguments);
    }

    public Process execute(Path directory, String... arguments) throws IOException {
        return createProcessForWindowsCommands(directory, arguments).start();
    }

    private ProcessBuilder createProcessForWindowsCommands(Path directory, String[] arguments) {
        return createProcess(directory, "run", arguments);
    }
    
    private ProcessBuilder createProcess(Path directory, String command, String[] arguments) {
        String[] processArgs = new String[arguments.length + 2];
        processArgs[0] = protonPath;
        processArgs[1] = command;
        System.arraycopy(arguments, 0, processArgs, 2, arguments.length);
        ProcessBuilder processBuilder = new ProcessBuilder(processArgs);
        Map<String, String> environment = processBuilder.environment();
        environment.put("SteamGameId", "0");
        environment.put("STEAM_COMPAT_DATA_PATH", compatPath);
        environment.put("STEAM_COMPAT_CLIENT_INSTALL_PATH", steamPath);
        if (directory != null) {
            processBuilder.directory(directory.toAbsolutePath().toFile());
        }
        return processBuilder;
    }

    public void setRegistry(String path, String key, String type, String value) {
        try {
            executeAwait("reg", "add", path, "/v", key, "/t", type, "/d", value, "/f");
        } catch (InterruptedException | IOException e) {
            if (LauncherApp.LOGGER.isDebug()) {
                LauncherApp.LOGGER.debug("Failed to set registry entry '{0}' -> '{1}'", e, path, key);
            }
        }
    }
    
    public String queryRegistry(String path, String key) {
        try {
            return executeAwait("reg", "query", path, "/v", key).result();
        } catch (InterruptedException | IOException e) {
            if (LauncherApp.LOGGER.isDebug()) {
                LauncherApp.LOGGER.debug("Failed to query registry entry '{0}' -> '{1}'", e, path, key);
            }
            return null;
        }
    }

}
