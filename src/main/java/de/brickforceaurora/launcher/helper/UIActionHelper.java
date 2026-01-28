package de.brickforceaurora.launcher.helper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import de.brickforceaurora.launcher.GameData;
import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.Main;
import de.brickforceaurora.launcher.helper.WindowsHelper.ProgramResult;
import de.brickforceaurora.launcher.ui.UserInterface;
import de.brickforceaurora.launcher.updater.UpdateManager;
import de.brickforceaurora.launcher.updater.UpdaterConfig;
import de.brickforceaurora.launcher.updater.launcher.LauncherUpdater;
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
        final LauncherApp app = LauncherApp.get();
        final UserInterface userInterface = app.userInterface();
        if (userInterface.mainProgress.get() != 1f) {
            return;
        }
        final String gamePath = app.gameDirectory().resolve("BrickForce.exe").toString();
        final ISimpleLogger logger = app.snowFrame().logger();
        logger.info("Launching game '{0}'", gamePath);
        ProgramResult result;
        if (!WindowsHelper.isAuthorized("BrickForce", gamePath)) {
            result = WindowsHelper.authorizeProgram("BrickForce", gamePath);
            if (!result.success() || result.success() && logger.isDebug()) {
                logger.debug("Firewall Result: \n{0}\nFirewall Error: \n{1}", result.result(), result.error());
            }
        }
        result = WindowsHelper.applyRegistryLanguageFix();
        if (!result.success() || result.success() && logger.isDebug()) {
            logger.debug("Registry Result: \n{0}\nRegistry Error: \n{1}", result.result(), result.error());
        }

        try {
            new ProcessBuilder("cmd.exe", "/c", "start", "BrickForce.exe").directory(app.gameDirectory().toFile()).start();
        } catch (final IOException e) {
            logger.error("Failed to launch game", e);
        }
    }

    public static boolean updateLauncher() {
        final LauncherApp app = LauncherApp.get();

        final UpdaterConfig config = app.snowFrame().module(ConfigModule.class).manager().config(UpdaterConfig.class);
        final UserInterface userInterface = app.userInterface();

        userInterface.newVersionAvailable.set(false);
        userInterface.newVersionText.set("");

        userInterface.mainText.set("Checking for launcher updates...");
        userInterface.mainProgress.set(0.00f);

        final UpdateManager updateManager = new UpdateManager(app.snowFrame().logger(), new LauncherUpdater());
        try {
            Files.deleteIfExists(app.tempDirectory().resolve("BrickForceAurora-Update-%s.exe".formatted(Main.version().toString())));
        } catch (final IOException _) {
            // We can ignore if this fails
        }
        if (!updateManager.checkForUpdates(config, Main.version()) || !updateManager.hasUpdates()) {
            return false;
        }
        Version newVersion;
        if ((newVersion = updateManager.applyUpdates(new TaskTracker(userInterface.mainText, userInterface.mainProgress, 1f, 100),
            app.appDirectory())) != null) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    new ProcessBuilder("cmd.exe", "/c", "start", "BrickForceAurora-Update-%s.exe".formatted(newVersion.toString()),
                        "/verysilent").directory(app.tempDirectory().toFile()).start();
                } catch (final IOException _) {
                }
            }));
            Main.shutdown();
            return true;
        }
        return false;
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
