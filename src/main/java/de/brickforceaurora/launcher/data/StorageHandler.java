package de.brickforceaurora.launcher.data;

import me.lauriichan.snowframe.util.nbt.CompoundTag;
import me.lauriichan.snowframe.util.nbt.TagType;

public abstract class StorageHandler<T> {

    public final TagType<?> expectedType;

    public StorageHandler(TagType<?> expectedType) {
        this.expectedType = expectedType;
    }

    public abstract T read(CompoundTag root, String key);

    public abstract void write(CompoundTag root, String key, T value);

}
