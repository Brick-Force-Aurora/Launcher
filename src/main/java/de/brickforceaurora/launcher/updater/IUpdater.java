package de.brickforceaurora.launcher.updater;

import java.io.IOException;

import it.unimi.dsi.fastutil.objects.ObjectList;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.snowframe.util.Version;

public interface IUpdater {

    void checkForUpdate(UpdaterConfig config, ISimpleLogger logger, Version current, ObjectList<IUpdate> updates) throws IOException;

}
