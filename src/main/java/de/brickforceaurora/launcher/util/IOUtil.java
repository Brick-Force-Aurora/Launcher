package de.brickforceaurora.launcher.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;

import me.lauriichan.applicationbase.app.resource.source.FileDataSource;
import me.lauriichan.applicationbase.app.resource.source.IDataSource;
import me.lauriichan.applicationbase.app.resource.source.PathDataSource;
import me.lauriichan.laylib.json.IJson;
import me.lauriichan.laylib.json.io.JsonWriter;

public final class IOUtil {

    private static final JsonWriter TECHNICAL_JSON = new JsonWriter().setPretty(false);
    
    private IOUtil() {
        throw new UnsupportedOperationException();
    }
    
    public static String asString(IJson<?> json) {
        try {
            return TECHNICAL_JSON.toString(json);
        } catch (IOException e) {
            return "";
        }
    }

    public static Path asPath(IDataSource source) {
        Objects.requireNonNull(source, "IDataSource can't be null");
        if (source instanceof FileDataSource fileSource) {
            return fileSource.getSource().toPath();
        } else if (source instanceof PathDataSource pathSource) {
            return pathSource.getSource();
        }
        throw new IllegalArgumentException("Can't convert IDataSource of type '%s' to Path".formatted(source.getClass().getSimpleName()));
    }

    public static void delete(Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        if (!Files.isDirectory(path)) {
            Files.delete(path);
            return;
        }
        Iterator<Path> iter = list(path);
        while (iter.hasNext()) {
            delete(iter.next());
        }
        Files.delete(path);
    }

    public static Iterator<Path> list(Path path) throws IOException {
        final Iterator<Path> delegate = Files.newDirectoryStream(path).iterator();
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                try {
                    return delegate.hasNext();
                } catch (DirectoryIteratorException e) {
                    throw new UncheckedIOException(e.getCause());
                }
            }

            @Override
            public Path next() {
                try {
                    return delegate.next();
                } catch (DirectoryIteratorException e) {
                    throw new UncheckedIOException(e.getCause());
                }
            }
        };
    }

}
