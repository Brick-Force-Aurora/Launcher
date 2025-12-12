package de.brickforceaurora.launcher.updater;

import java.io.IOException;
import java.nio.file.Path;

import me.lauriichan.applicationbase.app.util.Version;
import me.lauriichan.laylib.logger.ISimpleLogger;

public interface IUpdate {

    void applyUpdate(ISimpleLogger logger, Path gameDirectory, Path tempDirectory) throws IOException;

    Version getVersion();

}
