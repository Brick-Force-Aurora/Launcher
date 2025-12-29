package de.brickforceaurora.launcher.updater;

import java.io.IOException;
import java.nio.file.Path;

import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.snowframe.util.Version;

public interface IUpdate {

    void applyUpdate(ISimpleLogger logger, Path gameDirectory, Path tempDirectory) throws IOException;

    Version getVersion();

}
