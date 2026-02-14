package de.brickforceaurora.launcher.updater;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Optional;

import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.config.UpdaterConfig;
import de.brickforceaurora.launcher.util.TaskTracker;
import de.brickforceaurora.launcher.util.TaskTracker.Task;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.snowframe.util.Version;

public final class UpdateManager {

    private final ISimpleLogger logger;

    private final IUpdater updater;
    private final ObjectArrayList<IUpdate> updates = new ObjectArrayList<>();

    public UpdateManager(final ISimpleLogger logger, final IUpdater updater) {
        this.logger = logger;
        this.updater = updater;
    }

    public boolean hasUpdates() {
        return !updates.isEmpty();
    }

    public int updateCount() {
        return updates.size();
    }

    public Optional<Version> latestUpdate() {
        if (updates.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(updates.get(updates.size() - 1).getVersion());
    }

    public boolean checkForUpdates(final UpdaterConfig config, final Version version) {
        try {
            updates.clear();
            updater.checkForUpdate(config, logger, version, updates);
            updates.sort(Comparator.comparing(IUpdate::getVersion));
            return true;
        } catch (final IOException exp) {
            logger.error("Failed to check for updates", exp);
            return false;
        }
    }

    public Version applyUpdates(final TaskTracker tracker, final Path updateTargetDir) {
        if (updates.isEmpty()) {
            return null;
        }
        final LauncherApp app = LauncherApp.get();
        final Path temporaryDirectory = app.tempDirectory();
        Version lastUpdate = null;
        final int updateCount = updates.size();
        int updateNum = 0;
        tracker.budget(updateCount * 100);
        for (final IUpdate update : updates) {
            final Task task = tracker.allocate("Applying update '%s' (%s / %s)".formatted(update.getVersion(), ++updateNum, updateCount),
                100);
            try {
                update.applyUpdate(logger, task, updateTargetDir, temporaryDirectory);
                lastUpdate = update.getVersion();
            } catch (final Throwable exp) {
                logger.error("Failed to apply update '{0}'", exp, update.getVersion());
                return lastUpdate;
            }
            task.done();
        }
        return lastUpdate;
    }

}
