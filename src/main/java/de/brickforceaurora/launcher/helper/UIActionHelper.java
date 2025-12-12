package de.brickforceaurora.launcher.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import de.brickforceaurora.launcher.GameData;
import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.helper.WindowsHelper.ProgramResult;
import de.brickforceaurora.launcher.ui.ControlBarUIProgress;
import de.brickforceaurora.launcher.updater.UpdateManager;
import de.brickforceaurora.launcher.updater.UpdaterConfig;
import de.brickforceaurora.launcher.util.TaskTracker;
import me.lauriichan.applicationbase.app.util.Version;
import me.lauriichan.laylib.logger.ISimpleLogger;

public final class UIActionHelper {

    private UIActionHelper() {
        throw new UnsupportedOperationException();
    }

    public static void startGame() {
        if (GameData.GAME_VERSION.rawValue() == null) {
            return;
        }
        ControlBarUIProgress progressUi = LauncherApp.app().sharedExtensions().getCached(ControlBarUIProgress.class);
        if (progressUi.progress.get() != 1f) {
            return;
        }
        String gamePath = LauncherApp.gameDirectory().resolve("BrickForce.exe").toAbsolutePath().toString();
        ISimpleLogger logger = LauncherApp.app().logger();
        logger.info("Launching game '{0}'", gamePath);
        ProgramResult result;
        if (!WindowsHelper.isAuthorized("BrickForce", gamePath)) {
            result = WindowsHelper.authorizeProgram("BrickForce", gamePath);
            if (result.success() && logger.isDebug()) {
                logger.debug("Firewall Result: \n{0}\nFirewall Error: \n{1}", result.result(), result.error());
            }
        }
        result = WindowsHelper.applyRegistryLanguageFix();
        if (result.success() && logger.isDebug()) {
            logger.debug("Registry Result: \n{0}\nRegistry Error: \n{1}", result.result(), result.error());
        }

        try {
            new ProcessBuilder(new String[] {
                "cmd.exe",
                "/c",
                "start",
                gamePath
            }).directory(LauncherApp.gameDirectory().toFile()).start();
        } catch (IOException e) {
            LauncherApp.app().logger().error("Failed to launch game", e);
        }
    }

    public static void runUpdate(boolean startup) {
        LauncherApp app = LauncherApp.app();

        UpdaterConfig config = app.configManager().config(UpdaterConfig.class);

        ControlBarUIProgress progressUi = app.sharedExtensions().getCached(ControlBarUIProgress.class);

        Path gameDirectory = LauncherApp.gameDirectory();
        if (!Files.exists(gameDirectory) || !Files.isDirectory(gameDirectory)) {
            GameData.GAME_VERSION.value(null);
        }
        Version currentVersion = GameData.GAME_VERSION.rawValue();
        boolean forceInstall = currentVersion == null;
        if (!forceInstall && startup && !config.checkForUpdates()) {
            progressUi.progressText.set("Ready to play (%s)".formatted(currentVersion));
            progressUi.progress.set(1);
            return;
        }

        progressUi.progressText.set("Checking for game updates...");
        progressUi.progress.set(0.05f);

        UpdateManager updateManager = LauncherApp.updateManager();
        if (!updateManager.checkForUpdates(config)) {
            if (currentVersion == null) {
                progressUi.progressText.set("Couldn't check for game updates");
                progressUi.progress.set(0);
                return;
            }
            progressUi.progressText.set("Ready to play (%s)".formatted(currentVersion));
            progressUi.progress.set(1);
            return;
        }

        if (!updateManager.hasUpdates()) {
            if (currentVersion == null) {
                progressUi.progressText.set("Couldn't find any game update");
                progressUi.progress.set(0);
                return;
            }
            progressUi.progressText.set("Ready to play (%s)".formatted(currentVersion));
            progressUi.progress.set(1);
            return;
        }

        progressUi.progress.set(0.1f);
        if (!forceInstall && !config.automaticUpdate()) {
            progressUi.progressText.set("Waiting for user response...");
            // TODO: Add dialog
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
            }
        }

        Version newVersion = updateManager.applyUpdates(new TaskTracker(progressUi.progressText, progressUi.progress, 0.9f));
        if (newVersion == null && currentVersion == null) {
            progressUi.progressText.set("Couldn't apply any game update");
            progressUi.progress.set(0);
            return;
        }
        if (newVersion != null) {
            GameData.GAME_VERSION.value(newVersion);
            try {
                GameData.STORE.save();
            } catch (IOException e) {
                // Ignore if save fails
            }
            currentVersion = newVersion;
        }
        progressUi.progressText.set("Ready to play (%s)".formatted(currentVersion));
        progressUi.progress.set(1);
    }

}
