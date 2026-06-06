package de.brickforceaurora.launcher.updater.shiningstar;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.brickforceaurora.launcher.config.UpdaterConfig;
import de.brickforceaurora.launcher.updater.IUpdate;
import de.brickforceaurora.launcher.updater.UpdateDownloadListener;
import de.brickforceaurora.launcher.updater.shiningstar.command.api.IncompleteInstructionException;
import de.brickforceaurora.launcher.updater.shiningstar.command.api.UpdateActor;
import de.brickforceaurora.launcher.util.SubWorker;
import de.brickforceaurora.launcher.util.TaskTracker.Task;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.lauriichan.laylib.command.CommandProcess;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.laylib.logger.util.StringUtil;
import me.lauriichan.snowframe.resource.source.PathDataSource;
import me.lauriichan.snowframe.util.Version;
import me.lauriichan.snowframe.util.http.HttpCode;
import me.lauriichan.snowframe.util.http.HttpRequest;
import me.lauriichan.snowframe.util.http.HttpResponse;
import me.lauriichan.snowframe.util.http.data.MultiFormData;
import me.lauriichan.snowframe.util.http.type.HttpContentType;

public final class ShiningStarUpdate implements IUpdate {

    private final ShiningStarUpdater updater;

    private final UpdaterConfig config;
    private final String productId;
    private final Version current, target;

    public ShiningStarUpdate(ShiningStarUpdater updater, UpdaterConfig config, String productId, Version current, Version target) {
        this.updater = updater;
        this.config = config;
        this.productId = productId;
        this.current = current;
        this.target = target;
    }

    @Override
    public void applyUpdate(ISimpleLogger logger, Task task, Path updateTargetDir, Path tempDirectory) throws IOException {
        task.task("Downloading update");
        HttpResponse<MultiFormData> response = new HttpRequest().url(config.shiningStarUrl("update/bundle")).param("product_id", productId)
            .param("current", current).param("target", target).downloadListener(new UpdateDownloadListener(task, 50))
            .call(HttpContentType.multiFormData(HttpContentType.TEXT.restrict("bfaurora/update-instructions"),
                HttpContentType.BINARY.restrict("application/zip")));
        if (response.code() != HttpCode.OK) {
            ShiningStarUpdater.logFormResponse(logger, response);
            return;
        }
        MultiFormData data = response.data().value();
        String[] instructions = data.get("instructions", String.class).split("\n");
        Files.createDirectories(tempDirectory);
        FastByteArrayInputStream byteInput = new FastByteArrayInputStream(data.get("zip", byte[].class));
        SubWorker worker = new SubWorker(task, 20, byteInput.length);
        int lastPos = 0, newPos = 0;
        task.task("Unpacking update files");
        try (ZipInputStream zip = new ZipInputStream(byteInput)) {
            ZipEntry entry;
            Path path, parent;
            while ((entry = zip.getNextEntry()) != null) {
                newPos = (int) byteInput.position();
                worker.work(lastPos - newPos);
                lastPos = newPos;
                if (entry.isDirectory()) {
                    path = tempDirectory.resolve(entry.getName());
                    if (!Files.exists(path)) {
                        Files.createDirectories(path);
                    }
                    continue;
                }
                path = tempDirectory.resolve(entry.getName());
                if (!Files.exists(parent = path.getParent())) {
                    Files.createDirectories(parent);
                }
                Files.copy(zip, path, StandardCopyOption.REPLACE_EXISTING);
            }
            worker.finish();
        }

        task.task("Parsing update instructions");
        worker = new SubWorker(task, 20, instructions.length);
        ObjectArrayList<CommandProcess> parsedInstructions = new ObjectArrayList<>();
        StringBuilder errors = new StringBuilder();
        UpdateActor actor = new UpdateActor(new PathDataSource(tempDirectory), new PathDataSource(updateTargetDir));
        for (String instruction : instructions) {
            try {
                parsedInstructions.add(updater.parseInstruction(logger, actor, instruction));
            } catch (IncompleteInstructionException incomplete) {
                if (!errors.isEmpty()) {
                    errors.append('\n');
                }
                errors.append(StringUtil.stackTraceToBuilder(incomplete));
            }
        }
        if (!errors.isEmpty()) {
            throw new IOException("Failed to parse all instructions (%s / %s successful):\n%s".formatted(parsedInstructions.size(),
                instructions.length, errors));
        }

        task.task("Executing update instructions");
        worker = new SubWorker(task, 20, parsedInstructions.size());
        int failed = 0;
        for (CommandProcess instruction : parsedInstructions) {
            try {
                updater.executeInstruction(actor, instruction);
            } catch (Throwable e) {
                failed++;
                if (!errors.isEmpty()) {
                    errors.append('\n');
                }
                errors.append(StringUtil.stackTraceToBuilder(e));
            }
            worker.work(1);
        }
        if (!errors.isEmpty()) {
            logger.error("Failed to execute all update instructions (%s / %s successful):\n%s".formatted(instructions.length - failed,
                instructions.length, errors));
        }
    }

    @Override
    public Version getVersion() {
        return target;
    }

}
