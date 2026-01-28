package de.brickforceaurora.launcher.updater.launcher;

import java.io.IOException;

import de.brickforceaurora.launcher.updater.IUpdate;
import de.brickforceaurora.launcher.updater.IUpdater;
import de.brickforceaurora.launcher.updater.UpdaterConfig;
import it.unimi.dsi.fastutil.objects.ObjectList;
import me.lauriichan.laylib.json.IJson;
import me.lauriichan.laylib.json.JsonObject;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.snowframe.util.Version;
import me.lauriichan.snowframe.util.http.HttpCode;
import me.lauriichan.snowframe.util.http.HttpRequest;
import me.lauriichan.snowframe.util.http.HttpResponse;
import me.lauriichan.snowframe.util.http.type.HttpContentType;

public class LauncherUpdater implements IUpdater {

    private static final String LAUNCHER_RELEASE = "https://brickforce-aurora.de/download/latest_launcher.json";

    @Override
    public void checkForUpdate(final UpdaterConfig config, final ISimpleLogger logger, final Version current,
        final ObjectList<IUpdate> updates) throws IOException {
        final HttpResponse<IJson<?>> response = new HttpRequest().url(LAUNCHER_RELEASE).readTimeout(5000).call(HttpContentType.JSON);
        if (response.code() != HttpCode.OK || response.data().isError()) {
            return;
        }
        final JsonObject object = response.data().value().asJsonObject();
        final Version version = Version.parse(object.getAsString("version"));
        final String url = object.getAsString("url");
        if (current.compareTo(version) >= 0) {
            return;
        }
        updates.add(new LauncherUpdate(version, url));
    }

}
