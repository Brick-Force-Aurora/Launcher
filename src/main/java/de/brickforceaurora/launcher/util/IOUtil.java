package de.brickforceaurora.launcher.util;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.DirectoryIteratorException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Objects;

import me.lauriichan.laylib.json.IJson;
import me.lauriichan.laylib.json.io.JsonWriter;
import me.lauriichan.snowframe.resource.source.FileDataSource;
import me.lauriichan.snowframe.resource.source.IDataSource;
import me.lauriichan.snowframe.resource.source.PathDataSource;

public final class IOUtil {

    private static final JsonWriter TECHNICAL_JSON = new JsonWriter().setPretty(false);

    private IOUtil() {
        throw new UnsupportedOperationException();
    }

    public static String asString(final IJson<?> json) {
        try {
            return TECHNICAL_JSON.toString(json);
        } catch (final IOException e) {
            return "";
        }
    }

    public static Path asPath(final IDataSource source) {
        Objects.requireNonNull(source, "IDataSource can't be null");
        if (source instanceof final FileDataSource fileSource) {
            return fileSource.getSource().toPath();
        }
        if (source instanceof final PathDataSource pathSource) {
            return pathSource.getSource();
        }
        throw new IllegalArgumentException("Can't convert IDataSource of type '%s' to Path".formatted(source.getClass().getSimpleName()));
    }

    public static void delete(final Path path) throws IOException {
        if (!Files.exists(path)) {
            return;
        }
        if (!Files.isDirectory(path)) {
            Files.delete(path);
            return;
        }
        final Iterator<Path> iter = list(path);
        while (iter.hasNext()) {
            delete(iter.next());
        }
        Files.delete(path);
    }

    public static Iterator<Path> list(final Path path) throws IOException {
        final Iterator<Path> delegate = Files.newDirectoryStream(path).iterator();
        return new Iterator<>() {
            @Override
            public boolean hasNext() {
                try {
                    return delegate.hasNext();
                } catch (final DirectoryIteratorException e) {
                    throw new UncheckedIOException(e.getCause());
                }
            }

            @Override
            public Path next() {
                try {
                    return delegate.next();
                } catch (final DirectoryIteratorException e) {
                    throw new UncheckedIOException(e.getCause());
                }
            }
        };
    }

}
