package de.brickforceaurora.launcher.data;

import java.util.Objects;

import de.brickforceaurora.launcher.LauncherApp;
import me.lauriichan.snowframe.util.nbt.CompoundTag;

public final class StoredData<T> {

    private final String key;
    private final StorageHandler<T> handler;

    private final T defaultValue;
    private volatile T value;

    StoredData(final String key, final StorageHandler<T> handler, final T defaultValue) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Invalid key");
        }
        this.key = key;
        this.handler = Objects.requireNonNull(handler);
        this.value = defaultValue;
        this.defaultValue = defaultValue;
    }

    public String key() {
        return key;
    }

    public StorageHandler<T> handler() {
        return handler;
    }

    public T defaultValue() {
        return defaultValue;
    }

    public T rawValue() {
        return value;
    }

    public T value() {
        if (value == null) {
            return defaultValue;
        }
        return value;
    }

    public void value(final T value) {
        this.value = value;
    }

    void read(final CompoundTag root) {
        if (!root.has(key, handler.expectedType)) {
            value = null;
            return;
        }
        try {
            value = handler.read(root, key);
        } catch (final Exception exp) {
            value = null;
            LauncherApp.logger().warning("Failed to read value '{0}'", exp, key);
        }
    }

    void write(final CompoundTag root) {
        if (value == null) {
            root.remove(key);
            return;
        }
        try {
            handler.write(root, key, value);
        } catch (final Exception exp) {
            LauncherApp.logger().warning("Failed to write value '{0}'", exp, key);
        }
    }

}
