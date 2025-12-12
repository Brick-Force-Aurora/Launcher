package de.brickforceaurora.launcher.updater;

import java.io.IOException;

import it.unimi.dsi.fastutil.objects.ObjectList;
import me.lauriichan.applicationbase.app.util.Version;
import me.lauriichan.laylib.logger.ISimpleLogger;

public interface IUpdater {

    void checkForUpdate(UpdaterConfig config, ISimpleLogger logger, Version current, ObjectList<IUpdate> updates) throws IOException;

}
