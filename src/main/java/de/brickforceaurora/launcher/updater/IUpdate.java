package de.brickforceaurora.launcher.updater;

import java.io.IOException;
import java.nio.file.Path;

import de.brickforceaurora.launcher.util.TaskTracker.Task;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.snowframe.util.Version;

public interface IUpdate {

    void applyUpdate(ISimpleLogger logger, Task task, Path updateTargetDir, Path tempDirectory) throws IOException;

    Version getVersion();

}
