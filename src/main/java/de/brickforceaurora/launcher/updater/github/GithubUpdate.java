package de.brickforceaurora.launcher.updater.github;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import de.brickforceaurora.launcher.updater.IUpdate;
import de.brickforceaurora.launcher.util.IOUtil;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.ObjectArraySet;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.snowframe.util.Version;
import me.lauriichan.snowframe.util.http.HttpCode;
import me.lauriichan.snowframe.util.http.HttpRequest;
import me.lauriichan.snowframe.util.http.HttpResponse;
import me.lauriichan.snowframe.util.http.type.HttpContentType;

final class GithubUpdate implements IUpdate {

    private final GithubAuthenticator authenticator;

    private final String url;
    private final Version version;

    public GithubUpdate(GithubAuthenticator authenticator, String url, Version version) {
        this.authenticator = authenticator;
        this.url = url;
        this.version = version;
    }

    @Override
    public void applyUpdate(ISimpleLogger logger, Path gameDirectory, Path tempDirectory) throws IOException {
        HttpResponse<byte[]> response = GithubUpdater.callGithub(
            new HttpRequest().url(url).authenticator(authenticator).readTimeout(10000).param(url, authenticator), HttpContentType.BINARY);
        if (response == null || response.code() != HttpCode.OK) {
            throw new IOException("Couldn't download update");
        }
        Files.createDirectories(tempDirectory);
        try {
            try (ZipInputStream zip = new ZipInputStream(new FastByteArrayInputStream(response.data().value()))) {
                ZipEntry entry;
                Path path, parent;
                while ((entry = zip.getNextEntry()) != null) {
                    if (entry.isDirectory()) {
                        path = tempDirectory.resolve(entry.getName());
                        if (!Files.exists(path)) {
                            Files.createDirectories(path);
                        }
                        continue;
                    }
                    path = tempDirectory.resolve(entry.getName());
                    if (!Files.exists(parent = path.getParent())) {
                        Files.createDirectories(parent);
                    }
                    Files.copy(zip, path, StandardCopyOption.REPLACE_EXISTING);
                }
            }
            Path infoFile = tempDirectory.resolve("info");
            if (!Files.exists(infoFile)) {
                logger.warning("Found invalid update zip for version {0}: no info file available", version);
                return;
            }
            ObjectArraySet<String> deletePaths = new ObjectArraySet<>();
            Object2ObjectArrayMap<String, String> paths = new Object2ObjectArrayMap<>();
            try (BufferedReader reader = Files.newBufferedReader(infoFile)) {
                String line;
                String[] parts;
                while ((line = reader.readLine()) != null) {
                    if (!line.contains("=")) {
                        continue;
                    }
                    parts = line.split("=");
                    if (parts.length > 2) {
                        continue;
                    }
                    // Don't allow target to be outside of the target folder, generally we don't need '..'
                    if (parts[1].contains("..")) {
                        continue;
                    }
                    if (parts[0].equalsIgnoreCase("delete")) {
                        deletePaths.add(parts[1]);
                        continue;
                    }
                    paths.put(parts[0], parts[1]);
                }
            }
            applyPatch(tempDirectory, paths, gameDirectory, "");
            for (String deletePath : deletePaths) {
                IOUtil.delete(gameDirectory.resolve(deletePath));
            }
        } finally {
            IOUtil.delete(tempDirectory);
        }
    }

    private void applyPatch(Path sourceDirectory, Object2ObjectArrayMap<String, String> targetPaths, Path gameDirectory, String currentPath)
        throws IOException {
        Iterator<Path> iterator = IOUtil.list(sourceDirectory);
        Path path;
        String fileName;
        while (iterator.hasNext()) {
            path = iterator.next();
            fileName = path.getFileName().toString();
            String pathKey = currentPath + fileName;
            if (!targetPaths.containsKey(pathKey)) {
                if (Files.isDirectory(path)) {
                    applyPatch(path, targetPaths, gameDirectory, pathKey + '/');
                }
                continue;
            }
            String targetPath = targetPaths.get(pathKey);
            if (targetPath.equals("/")) {
                targetPath = "";
            } else if (!targetPath.endsWith("/")) {
                targetPath += "/";
            }
            boolean isFile;
            if (isFile = !Files.isDirectory(path)) {
                targetPath += fileName;
            }
            copy(path, gameDirectory.resolve(targetPath), isFile);
        }
    }

    private void copy(Path source, Path target, boolean isFile) throws IOException {
        if (isFile) {
            Path parentTarget = target.getParent();
            if (!Files.exists(parentTarget)) {
                Files.createDirectories(parentTarget);
            }
            Files.copy(source, target, StandardCopyOption.REPLACE_EXISTING);
            return;
        }
        if (!Files.exists(target)) {
            Files.createDirectories(target);
        }
        Iterator<Path> iterator = IOUtil.list(source);
        Path path;
        while (iterator.hasNext()) {
            copy(path = iterator.next(), target.resolve(path.getFileName()), !Files.isDirectory(path));
        }
    }

    @Override
    public Version getVersion() {
        return version;
    }

}
