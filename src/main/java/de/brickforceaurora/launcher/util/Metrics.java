package de.brickforceaurora.launcher.util;

import java.util.concurrent.TimeUnit;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;

public class Metrics {

    private static record Entry(long timestamp, int value) {}

    private final ObjectList<Entry> list = ObjectLists.synchronize(new ObjectArrayList<>());
    private final long maxLength;

    private volatile long oldestTimestamp = -1;

    public Metrics() {
        this(1, TimeUnit.MINUTES);
    }

    public Metrics(final long maxLength, final TimeUnit unit) {
        this.maxLength = unit.toNanos(maxLength);
    }

    public void push(final int value) {
        final long timestamp = System.nanoTime();
        list.add(new Entry(timestamp, value));
        if (oldestTimestamp == -1) {
            oldestTimestamp = timestamp;
            return;
        }
        if (timestamp - oldestTimestamp <= maxLength) {
            return;
        }
        Entry entry;
        for (int i = 0; i < list.size(); i++) {
            entry = list.get(i);
            if (timestamp - entry.timestamp() > maxLength) {
                list.remove(i--);
                continue;
            }
            oldestTimestamp = entry.timestamp();
            break;
        }
    }

    public float averageFor(long length, final TimeUnit unit) {
        length = unit.toNanos(length);
        final long timestamp = System.nanoTime();
        long value = 0;
        int count = 0;
        for (final Entry entry : list) {
            if (timestamp - entry.timestamp() <= length) {
                value += entry.value();
                count++;
                continue;
            }
            break;
        }
        return value / (float) count;
    }

}
