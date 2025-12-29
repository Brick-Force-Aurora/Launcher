package de.brickforceaurora.launcher.updater;

import java.io.IOException;
import java.nio.file.Path;

import de.brickforceaurora.launcher.GameData;
import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.util.TaskTracker;
import de.brickforceaurora.launcher.util.TaskTracker.Task;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.snowframe.util.Version;

public final class UpdateManager {

    private final ISimpleLogger logger;

    private final IUpdater updater;
    private final ObjectArrayList<IUpdate> updates = new ObjectArrayList<>();

    public UpdateManager(ISimpleLogger logger, IUpdater updater) {
        this.logger = logger;
        this.updater = updater;
    }

    public boolean hasUpdates() {
        return !updates.isEmpty();
    }

    public boolean checkForUpdates(UpdaterConfig config) {
        try {
            updates.clear();
            updater.checkForUpdate(config, logger, GameData.GAME_VERSION.value(), updates);
            updates.sort((u1, u2) -> u1.getVersion().compareTo(u2.getVersion()));
            return true;
        } catch (IOException exp) {
            logger.error("Failed to check for updates", exp);
            return false;
        }
    }

    public Version applyUpdates(TaskTracker tracker) {
        if (updates.isEmpty()) {
            return null;
        }
        Task task = tracker.allocate("Applying updates...", updates.size());
        LauncherApp app = LauncherApp.get();
        Path gameDirectory = app.gameDirectory();
        Path temporaryDirectory = app.tempDirectory();
        Version lastUpdate = null;
        task.task(null);
        for (IUpdate update : updates) {
            try {
                update.applyUpdate(logger, gameDirectory, temporaryDirectory);
                lastUpdate = update.getVersion();
            } catch (Throwable exp) {
                logger.error("Failed to apply update '{0}'", exp, update.getVersion());
                return lastUpdate;
            }
            task.work(1);
            task.task(null);
        }
        return lastUpdate;
    }

}
