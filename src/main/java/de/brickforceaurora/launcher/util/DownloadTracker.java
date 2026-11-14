package de.brickforceaurora.launcher.util;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import de.brickforceaurora.launcher.util.TaskTracker.Task;
import me.lauriichan.snowframe.util.http.IHttpProgressListener;

public class DownloadTracker implements IHttpProgressListener {

    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");

    private final Metrics metrics = new Metrics(2, TimeUnit.SECONDS);

    private final Task task;
    private final String taskName;
    private final int totalWork;
    private volatile int currentWork, allocatedWork;
    private volatile int lastCurrent = 0;

    public DownloadTracker(final Task task, final int totalWork) {
        this.task = task;
        this.taskName = task.task();
        this.totalWork = totalWork;
    }

    public Metrics metrics() {
        return metrics;
    }

    @Override
    public void progress(final int current, final int total) {
        metrics.push(current - lastCurrent);
        lastCurrent = current;
        updateTask();
        if (total <= 0) {
            return;
        } else if (current == total) {
            currentWork = totalWork;
            task.work(currentWork - allocatedWork);
            return;
        }
        currentWork = (int) (current / (float) total * totalWork);
        if (currentWork != allocatedWork) {
            task.work(currentWork - allocatedWork);
            allocatedWork = currentWork;
        }
    }
    
    private void updateTask() {
        task.task("%s (%s) - %s".formatted(taskName, formatDownloaded(), formatMetrics()));
    }

    private String formatMetrics() {
        final int bytes = Math.round(metrics.accumulatedValueFor(1, TimeUnit.SECONDS));
        final float kBytes = bytes / 1024f;
        final float mBytes = kBytes / 1024f;
        if (mBytes > 1) {
            return FORMAT.format(mBytes) + " MB/s";
        }
        return FORMAT.format(kBytes) + " KB/s";
    }

    private String formatDownloaded() {
        final int bytes = lastCurrent;
        final float kBytes = bytes / 1024f;
        final float mBytes = kBytes / 1024f;
        if (mBytes > 1) {
            return FORMAT.format(mBytes) + " MB";
        }
        return FORMAT.format(kBytes) + " KB";
    }

}
