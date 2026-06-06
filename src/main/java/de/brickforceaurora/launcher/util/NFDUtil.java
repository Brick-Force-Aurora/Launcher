package de.brickforceaurora.launcher.util;

import static org.lwjgl.util.nfd.NativeFileDialog.*;

import java.nio.IntBuffer;

import org.lwjgl.PointerBuffer;
import org.lwjgl.system.MemoryStack;
import org.lwjgl.system.MemoryUtil;
import org.lwjgl.util.nfd.NFDFilterItem;
import org.lwjgl.util.nfd.NFDPathSetEnum;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;

public final class NFDUtil {

    public static record Filter(String description, String filters) {

        public Filter(String description, String... filters) {
            this(description, String.join(",", filters));
        }

    }

    private NFDUtil() {
        throw new UnsupportedOperationException();
    }

    public static String pickDirectory(String defaultPath) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer buf = stack.mallocPointer(1);
            if (NFD_PickFolder(buf, defaultPath) == NFD_OKAY) {
                try {
                    return buf.getStringUTF8(0);
                } finally {
                    NFD_FreePath(buf.get(0));
                }
            }
        }
        return null;
    }

    public static ObjectList<String> pickDirectories(String defaultPath) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            PointerBuffer buf = stack.mallocPointer(1);
            if (NFD_PickFolderMultiple(buf, defaultPath) == NFD_OKAY) {
                long pathSetPointer = buf.get(0);
                
                IntBuffer countBuf = stack.mallocInt(1);
                NFD_PathSet_GetCount(pathSetPointer, countBuf);
                ObjectArrayList<String> paths = new ObjectArrayList<>(countBuf.get(0));
                
                NFDPathSetEnum pathSet = NFDPathSetEnum.calloc(stack);
                NFD_PathSet_GetEnum(pathSetPointer, pathSet);

                while (NFD_PathSet_EnumNext(pathSet, buf) == NFD_OKAY && buf.get(0) != MemoryUtil.NULL) {
                    paths.add(buf.getStringUTF8(0));
                    NFD_PathSet_FreePath(buf.get(0));
                }

                NFD_PathSet_FreeEnum(pathSet);
                NFD_PathSet_Free(pathSetPointer);

                if (paths.isEmpty()) {
                    return ObjectLists.emptyList();
                }
                return ObjectLists.unmodifiable(paths);
            }
        }
        return ObjectLists.emptyList();
    }

    public static String pickFile(String defaultPath, Filter... filters) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            NFDFilterItem.Buffer filterBuf = NFDFilterItem.malloc(filters.length, stack);
            for (int i = 0; i < filters.length; i++) {
                filterBuf.get(i).name(stack.UTF8(filters[i].description())).spec(stack.UTF8(filters[i].filters()));
            }
            PointerBuffer buf = stack.mallocPointer(1);
            if (NFD_OpenDialog(buf, filterBuf, stack.UTF8(defaultPath)) == NFD_OKAY) {
                try {
                    return buf.getStringUTF8(0);
                } finally {
                    NFD_FreePath(buf.get(0));
                }
            }
        }
        return null;
    }

    public static ObjectList<String> pickFiles(String defaultPath, Filter... filters) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            NFDFilterItem.Buffer filterBuf = NFDFilterItem.malloc(filters.length, stack);
            for (int i = 0; i < filters.length; i++) {
                filterBuf.get(i).name(stack.UTF8(filters[i].description())).spec(stack.UTF8(filters[i].filters()));
            }
            PointerBuffer buf = stack.mallocPointer(1);
            if (NFD_OpenDialogMultiple(buf, filterBuf, stack.UTF8(defaultPath)) == NFD_OKAY) {
                long pathSetPointer = buf.get(0);
                
                IntBuffer countBuf = stack.mallocInt(1);
                NFD_PathSet_GetCount(pathSetPointer, countBuf);
                ObjectArrayList<String> paths = new ObjectArrayList<>(countBuf.get(0));
                
                NFDPathSetEnum pathSet = NFDPathSetEnum.calloc(stack);
                NFD_PathSet_GetEnum(pathSetPointer, pathSet);

                while (NFD_PathSet_EnumNext(pathSet, buf) == NFD_OKAY && buf.get(0) != MemoryUtil.NULL) {
                    paths.add(buf.getStringUTF8(0));
                    NFD_PathSet_FreePath(buf.get(0));
                }

                NFD_PathSet_FreeEnum(pathSet);
                NFD_PathSet_Free(pathSetPointer);

                if (paths.isEmpty()) {
                    return ObjectLists.emptyList();
                }
                return ObjectLists.unmodifiable(paths);
            }
        }
        return ObjectLists.emptyList();
    }

    public static String saveFile(String defaultPath, String defaultName, Filter... filters) {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            NFDFilterItem.Buffer filterBuf = NFDFilterItem.malloc(filters.length, stack);
            for (int i = 0; i < filters.length; i++) {
                filterBuf.get(i).name(stack.UTF8(filters[i].description())).spec(stack.UTF8(filters[i].filters()));
            }
            PointerBuffer buf = stack.mallocPointer(1);
            if (NFD_SaveDialog(buf, filterBuf, stack.UTF8(defaultPath), stack.UTF8(defaultName)) == NFD_OKAY) {
                try {
                    return buf.getStringUTF8(0);
                } finally {
                    NFD_FreePath(buf.get(0));
                }
            }
        }
        return null;
    }

}
