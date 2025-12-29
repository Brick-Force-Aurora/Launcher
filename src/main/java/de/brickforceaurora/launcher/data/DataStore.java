package de.brickforceaurora.launcher.data;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMaps;
import me.lauriichan.snowframe.resource.source.IDataSource;
import me.lauriichan.snowframe.util.Tuple;
import me.lauriichan.snowframe.util.nbt.CompoundTag;

public final class DataStore {

    private final IDataSource source;
    private final Object2ObjectMap<String, StoredData<?>> stored = Object2ObjectMaps.synchronize(new Object2ObjectArrayMap<>());

    private volatile CompoundTag root;

    public DataStore(IDataSource source) {
        this.source = source;
    }

    public <T> StoredData<T> register(String key, StorageHandler<T> handler) {
        return register(key, handler, null);
    }

    public <T> StoredData<T> register(String key, StorageHandler<T> handler, T defaultValue) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("Invalid key '%s'".formatted(key));
        }
        if (stored.containsKey(key)) {
            throw new IllegalStateException("Already registered key '%s'".formatted(key));
        }
        StoredData<T> data = new StoredData<>(key, handler, defaultValue);
        stored.put(key, data);
        if (root != null) {
            data.read(root);
        }
        return data;
    }

    public void load() throws IOException {
        try {
            if (!source.exists()) {
                clearInternal();
                return;
            }
            try (DataInputStream data = new DataInputStream(source.openReadableStream())) {
                Tuple<String, CompoundTag> tuple = CompoundTag.readNamed(data);
                if (tuple == null) {
                    clearInternal();
                    return;
                }
                root = tuple.second();
            }
        } catch (IOException e) {
            clearInternal();
            throw e;
        } finally {
            for (StoredData<?> data : stored.values()) {
                data.read(root);
            }
        }
    }

    public void save() throws IOException {
        if (root == null) {
            root = new CompoundTag();
        }
        for (StoredData<?> data : stored.values()) {
            data.write(root);
        }
        try (DataOutputStream data = new DataOutputStream(source.openWritableStream())) {
            root.writeNamed(data, "root");
        }
    }

    public void clear() {
        clearInternal();
        for (StoredData<?> data : stored.values()) {
            data.read(root);
        }
    }

    private void clearInternal() {
        if (root == null) {
            root = new CompoundTag();
            return;
        }
        root.clear();
    }

}
