package de.brickforceaurora.launcher.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import de.brickforceaurora.launcher.GameData;
import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.config.UpdaterConfig;
import de.brickforceaurora.launcher.ui.UserInterface;
import de.brickforceaurora.launcher.updater.UpdateManager;
import de.brickforceaurora.launcher.util.TaskTracker;
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
        final LauncherApp app = LauncherApp.get();
        final UserInterface userInterface = app.userInterface();
        if (userInterface.mainProgress.get() != 1f) {
            return;
        }
        app.platform().startGame();
    }

    public static void runUpdate(final boolean startup, final boolean confirmed) {
        final LauncherApp app = LauncherApp.get();

        final UpdaterConfig config = app.snowFrame().module(ConfigModule.class).manager().config(UpdaterConfig.class);
        final UserInterface userInterface = app.userInterface();

        userInterface.newVersionAvailable.set(false);
        userInterface.newVersionText.set("");

        final Path gameDirectory = app.gameDirectory();
        if (!Files.exists(gameDirectory) || !Files.isDirectory(gameDirectory)) {
            GameData.GAME_VERSION.value(null);
        }
        Version currentVersion = GameData.GAME_VERSION.rawValue();
        final boolean forceInstall = currentVersion == null;
        if (!forceInstall && startup && !config.checkForUpdates()) {
            userInterface.mainText.set("Ready to play (%s)".formatted(currentVersion));
            userInterface.mainProgress.set(1);
            return;
        }
        
        if (currentVersion == null) {
            app.platform().doPlatformSetup(userInterface.mainText, userInterface.mainProgress);
        }

        userInterface.mainText.set("Checking for game updates...");
        userInterface.mainProgress.set(0.05f);

        final UpdateManager updateManager = app.updateManager();
        if (!updateManager.checkForUpdates(config, GameData.GAME_VERSION.value())) {
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

        final Version newVersion = updateManager.applyUpdates(new TaskTracker(userInterface.mainText, userInterface.mainProgress, 0.9f),
            app.gameDirectory());
        if (newVersion == null && currentVersion == null) {
            userInterface.mainText.set("Couldn't apply any game update");
            userInterface.mainProgress.set(0);
            return;
        }
        if (newVersion != null) {
            GameData.GAME_VERSION.value(newVersion);
            try {
                GameData.STORE.save();
            } catch (final IOException e) {
                // Ignore if save fails
            }
            currentVersion = newVersion;
        }
        userInterface.mainText.set("Ready to play (%s)".formatted(currentVersion));
        userInterface.mainProgress.set(1);
    }

}
