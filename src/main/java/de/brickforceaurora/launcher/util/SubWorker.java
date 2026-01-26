package de.brickforceaurora.launcher.util;

import de.brickforceaurora.launcher.util.TaskTracker.Task;

public class SubWorker {

    private final Task task;
    private final int totalWork;
    private volatile int currentWork, allocatedWork;

    private final int totalSubWork;
    private volatile int subWork;

    public SubWorker(Task task, int totalWork, int totalSubWork) {
        this.task = task;
        this.totalWork = totalWork;
        this.totalSubWork = totalSubWork;
    }

    public void work(int work) {
        if (subWork + work > totalSubWork) {
            work = totalSubWork - subWork;
            if (work == 0) {
                return;
            }
        }
        subWork += work;
        currentWork = (int) ((subWork / (float) totalSubWork) * totalWork);
        if (currentWork != allocatedWork) {
            task.work(currentWork - allocatedWork);
            allocatedWork = currentWork;
        }
    }

}
