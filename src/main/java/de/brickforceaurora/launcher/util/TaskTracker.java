package de.brickforceaurora.launcher.util;

import java.util.Objects;

import de.brickforceaurora.launcher.animation.property.PropFloat;
import de.brickforceaurora.launcher.animation.property.PropString;

public class TaskTracker {

    public static class Task {

        private final TaskTracker tracker;

        private final String title;
        private final int allocatedWork;

        private volatile String task;
        private volatile int currentWork;

        private Task(final TaskTracker tracker, final String title, final int allocatedWork) {
            this.tracker = tracker;
            this.title = title;
            this.allocatedWork = allocatedWork;
        }

        public String title() {
            return title;
        }

        public void task(final String task) {
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

        public void work(final int work) {
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

    public TaskTracker(final PropString text, final PropFloat progress, final float allocated) {
        this(text, progress, allocated, 0);
    }

    public TaskTracker(final PropString text, final PropFloat progress, final float allocated, final int workBudget) {
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

    public void budget(final int budget) {
        if (workBudget != 0) {
            return;
        }
        workBudget = budget;
    }

    public Task allocate(final String title, final int work) {
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

    private void updateWork(final int work) {
        final float previousProgress = currentWork / (float) workBudget * allocated;
        currentWork += work;
        final float newProgress = currentWork / (float) workBudget * allocated;
        progress.set(progress.get() + (newProgress - previousProgress));
    }

}
