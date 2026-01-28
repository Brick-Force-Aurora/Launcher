package de.brickforceaurora.launcher.updater;

import java.text.DecimalFormat;
import java.util.concurrent.TimeUnit;

import de.brickforceaurora.launcher.util.Metrics;
import de.brickforceaurora.launcher.util.TaskTracker.Task;
import me.lauriichan.snowframe.util.http.IHttpProgressListener;

public class UpdateDownloadListener implements IHttpProgressListener {

    private static final DecimalFormat FORMAT = new DecimalFormat("0.00");

    private final Metrics metrics = new Metrics(2, TimeUnit.SECONDS);

    private final Task task;
    private final String taskName;
    private final int totalWork;
    private volatile int currentWork, allocatedWork;
    private volatile int lastCurrent = 0;

    public UpdateDownloadListener(final Task task, final int totalWork) {
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
        currentWork = (int) (current / (float) total * totalWork);
        if (currentWork != allocatedWork) {
            task.work(currentWork - allocatedWork);
            task.task(taskName + " - " + formatMetrics());
            allocatedWork = currentWork;
        }
    }

    private String formatMetrics() {
        final int bytes = Math.round(metrics.averageFor(1, TimeUnit.SECONDS));
        final float kBytes = bytes / 1024f;
        final float mBytes = kBytes / 1024f;
        if (mBytes > 1) {
            return FORMAT.format(mBytes) + " MB/s";
        }
        return FORMAT.format(kBytes) + " KB/s";
    }

}
