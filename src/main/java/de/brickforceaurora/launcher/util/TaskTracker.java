package de.brickforceaurora.launcher.util;

import java.util.Objects;

import me.lauriichan.applicationbase.app.ui.component.property.PropFloat;
import me.lauriichan.applicationbase.app.ui.component.property.PropString;

public class TaskTracker {

    public static class Task {

        private final TaskTracker tracker;

        private final String title;
        private final int allocatedWork;

        private volatile String task;
        private volatile int currentWork;

        private Task(TaskTracker tracker, String title, int allocatedWork) {
            this.tracker = tracker;
            this.title = title;
            this.allocatedWork = allocatedWork;
        }

        public String title() {
            return title;
        }

        public void task(String task) {
            if (task == null) {
                this.task = new StringBuilder().append(currentWork).append(" / ").append(allocatedWork).toString();
                tracker.text.set(new StringBuilder(title).append(" (").append(this.task).append(')').toString());
                return;
            }
            tracker.text.set(new StringBuilder(title).append(" (").append(this.task = task).append(')').toString());
        }

        public String task() {
            return task;
        }

        public void work(int work) {
            if (work <= 0) {
                throw new IllegalArgumentException("Work can't be less than or equal to zero");
            }
            if (currentWork + work > allocatedWork) {
                throw new IllegalArgumentException("Too much work!");
            }
            currentWork += work;
            tracker.updateWork(work);
        }

        public void done() {
            if (currentWork == allocatedWork) {
                return;
            }
            work(allocatedWork - currentWork);
        }

    }

    private final PropString text;
    private final PropFloat progress;

    private final float allocated;
    private volatile int workBudget, usedBudget;

    private volatile int currentWork;

    public TaskTracker(PropString text, PropFloat progress, float allocated) {
        this(text, progress, allocated, 0);
    }

    public TaskTracker(PropString text, PropFloat progress, float allocated, int workBudget) {
        if (allocated <= 0) {
            throw new IllegalArgumentException("Progress allocation can't be less than or equal to zero");
        }
        if (workBudget < 0) {
            throw new IllegalArgumentException("Work budget can't be less than zero");
        }
        this.text = Objects.requireNonNull(text);
        this.progress = Objects.requireNonNull(progress);
        this.allocated = allocated;
        this.workBudget = workBudget;
    }

    public Task allocate(String title, int work) {
        if (work <= 0) {
            throw new IllegalArgumentException("Work can't be less than or equal to zero");
        }
        if (workBudget == 0) {
            workBudget = work;
        }
        if (usedBudget + work > workBudget) {
            throw new IllegalArgumentException("Work budget already used");
        }
        usedBudget += work;
        return new Task(this, title, work);
    }

    private void updateWork(int work) {
        float previousProgress = (currentWork / (float) workBudget) * allocated;
        currentWork += work;
        float newProgress = (currentWork / (float) workBudget) * allocated;
        progress.set(progress.get() + (newProgress - previousProgress));
    }

}
