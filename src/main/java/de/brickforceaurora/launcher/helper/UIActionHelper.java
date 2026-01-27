package de.brickforceaurora.launcher.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import de.brickforceaurora.launcher.GameData;
import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.helper.WindowsHelper.ProgramResult;
import de.brickforceaurora.launcher.ui.UserInterface;
import de.brickforceaurora.launcher.updater.UpdateManager;
import de.brickforceaurora.launcher.updater.UpdaterConfig;
import de.brickforceaurora.launcher.util.TaskTracker;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.snowframe.ConfigModule;
import me.lauriichan.snowframe.util.Version;

public final class UIActionHelper {

    private UIActionHelper() {
        throw new UnsupportedOperationException();
    }

    public static void startGame() {
        if (GameData.GAME_VERSION.rawValue() == null) {
            return;
        }
        LauncherApp app = LauncherApp.get();
        UserInterface userInterface = app.userInterface();
        if (userInterface.mainProgress.get() != 1f) {
            return;
        }
        String gamePath = app.gameDirectory().resolve("BrickForce.exe").toString();
        ISimpleLogger logger = app.snowFrame().logger();
        logger.info("Launching game '{0}'", gamePath);
        ProgramResult result;
        if (!WindowsHelper.isAuthorized("BrickForce", gamePath)) {
            result = WindowsHelper.authorizeProgram("BrickForce", gamePath);
            if (!result.success() || (result.success() && logger.isDebug())) {
                logger.debug("Firewall Result: \n{0}\nFirewall Error: \n{1}", result.result(), result.error());
            }
        }
        result = WindowsHelper.applyRegistryLanguageFix();
        if (!result.success() || (result.success() && logger.isDebug())) {
            logger.debug("Registry Result: \n{0}\nRegistry Error: \n{1}", result.result(), result.error());
        }

        try {
            new ProcessBuilder(new String[] {
                "cmd.exe",
                "/c",
                "start",
                "BrickForce.exe"
            }).directory(app.gameDirectory().toFile()).start();
        } catch (IOException e) {
            logger.error("Failed to launch game", e);
        }
    }

    public static void runUpdate(boolean startup, boolean confirmed) {
        LauncherApp app = LauncherApp.get();

        UpdaterConfig config = app.snowFrame().module(ConfigModule.class).manager().config(UpdaterConfig.class);
        UserInterface userInterface = app.userInterface();

        userInterface.newVersionAvailable.set(false);
        userInterface.newVersionText.set("");

        Path gameDirectory = app.gameDirectory();
        if (!Files.exists(gameDirectory) || !Files.isDirectory(gameDirectory)) {
            GameData.GAME_VERSION.value(null);
        }
        Version currentVersion = GameData.GAME_VERSION.rawValue();
        boolean forceInstall = currentVersion == null;
        if (!forceInstall && startup && !config.checkForUpdates()) {
            userInterface.mainText.set("Ready to play (%s)".formatted(currentVersion));
            userInterface.mainProgress.set(1);
            return;
        }

        userInterface.mainText.set("Checking for game updates...");
        userInterface.mainProgress.set(0.05f);

        UpdateManager updateManager = app.updateManager();
        if (!updateManager.checkForUpdates(config)) {
            if (currentVersion == null) {
                userInterface.mainText.set("Couldn't check for game updates");
                userInterface.mainProgress.set(0);
                return;
            }
            userInterface.mainText.set("Ready to play (%s)".formatted(currentVersion));
            userInterface.mainProgress.set(1);
            return;
        }

        if (!updateManager.hasUpdates()) {
            if (currentVersion == null) {
                userInterface.mainText.set("Couldn't find any game update");
                userInterface.mainProgress.set(0);
                return;
            }
            userInterface.mainText.set("Ready to play (%s)".formatted(currentVersion));
            userInterface.mainProgress.set(1);
            return;
        }

        userInterface.mainProgress.set(0.1f);
        if (!confirmed && !forceInstall && !config.automaticUpdate()) {
            userInterface.newVersionText.set("There is %s new update(s) available, please update to %s."
                .formatted(updateManager.updateCount(), updateManager.latestUpdate().get()));
            userInterface.newVersionAvailable.set(true);
            userInterface.mainText.set("Ready to play (%s)".formatted(currentVersion));
            userInterface.mainProgress.set(1);
            return;
        }

        Version newVersion = updateManager.applyUpdates(new TaskTracker(userInterface.mainText, userInterface.mainProgress, 0.9f));
        if (newVersion == null && currentVersion == null) {
            userInterface.mainText.set("Couldn't apply any game update");
            userInterface.mainProgress.set(0);
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
        userInterface.mainText.set("Ready to play (%s)".formatted(currentVersion));
        userInterface.mainProgress.set(1);
    }

}
