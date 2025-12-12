package de.brickforceaurora.launcher;

import de.brickforceaurora.launcher.data.StorageHandler;
import me.lauriichan.applicationbase.app.data.nbt.CompoundTag;
import me.lauriichan.applicationbase.app.data.nbt.TagType;
import me.lauriichan.applicationbase.app.util.Version;

public final class DataHandler {
    
    private DataHandler() {
        throw new UnsupportedOperationException();
    }

    public static final StorageHandler<Version> VERSION = new StorageHandler<>(TagType.INT_ARRAY) {
        @Override
        public Version read(CompoundTag root, String key) {
            int[] components = root.getIntArray(key);
            return new Version(components[0], components[1], components[2], components[3]);
        }

        @Override
        public void write(CompoundTag root, String key, Version version) {
            int[] components = new int[4];
            components[0] = version.major;
            components[1] = version.minor;
            components[2] = version.patch;
            components[3] = version.revision;
            root.put(key, components);
        }
    };

}
