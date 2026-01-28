package de.brickforceaurora.launcher.updater.launcher;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

import de.brickforceaurora.launcher.updater.IUpdate;
import de.brickforceaurora.launcher.updater.UpdateDownloadListener;
import de.brickforceaurora.launcher.util.TaskTracker.Task;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.snowframe.util.Version;
import me.lauriichan.snowframe.util.http.HttpCode;
import me.lauriichan.snowframe.util.http.HttpRequest;
import me.lauriichan.snowframe.util.http.HttpResponse;
import me.lauriichan.snowframe.util.http.type.HttpContentType;

public record LauncherUpdate(Version version, String url) implements IUpdate {

    @Override
    public void applyUpdate(final ISimpleLogger logger, final Task task, final Path updateTargetDir, final Path tempDirectory)
        throws IOException {
        task.task("Downloading launcher update");
        final String[] parts = url.split("/");
        final String fileName = parts[parts.length - 1];
        final HttpResponse<byte[]> request = new HttpRequest().url(url).downloadListener(new UpdateDownloadListener(task, 50))
            .readTimeout(10000).call(HttpContentType.BINARY);
        if (request.code() != HttpCode.OK || request.data().isError()) {
            throw new IllegalStateException();
        }
        task.task("Writing installer");
        final Path path = tempDirectory.resolve(fileName);
        Files.createDirectories(tempDirectory);
        Files.deleteIfExists(path);
        Files.write(path, request.data().value(), StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        task.work(50);
    }

    @Override
    public Version getVersion() {
        return version;
    }

}
