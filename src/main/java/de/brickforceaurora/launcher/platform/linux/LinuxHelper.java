package de.brickforceaurora.launcher.platform.linux;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;

import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.util.IOUtil;
import de.brickforceaurora.launcher.util.TaskTracker.Task;
import me.lauriichan.snowframe.util.Version;

public final class LinuxHelper {

    public static record ProtonReq(String name, int majorVersion) {
        public ProtonReq(String name, int majorVersion) {
            this.name = Objects.requireNonNull(name).toLowerCase();
            this.majorVersion = majorVersion;
        }
    }

    private static final Set<PosixFilePermission> WRITE_PERMISSIONS = Set.of(new PosixFilePermission[] {
        PosixFilePermission.OWNER_READ,
        PosixFilePermission.OWNER_WRITE,
        PosixFilePermission.OWNER_EXECUTE,

        PosixFilePermission.GROUP_READ,
        PosixFilePermission.GROUP_EXECUTE,

        PosixFilePermission.OTHERS_READ,
        PosixFilePermission.OTHERS_EXECUTE
    });

    private static final String[][] PROTON_FILES = {
        {
            "proton"
        },
        {
            "toolmanifest.vdf"
        },
        {
            "files/bin/wine64",
            "dist/bin/wine64"
        }
    };

    private static final String[] PREFIX_FILES = new String[] {
        "drive_c",
        "system.reg"
    };

    private static record ProtonInstallation(String name, Version version, Path path) implements Comparable<ProtonInstallation> {
        @Override
        public int compareTo(ProtonInstallation o) {
            int cmp = name.compareTo(o.name);
            if (cmp != 0) {
                return cmp;
            }
            return o.version.compareTo(version);
        }
    }

    private LinuxHelper() {
        throw new UnsupportedOperationException();
    }

    public static Path findProton(ProtonReq[] protons, Path[] paths) throws IOException {
        ArrayList<ProtonInstallation> installations = new ArrayList<>();
        for (Path protonPath : paths) {
            if (protonPath == null || !Files.isDirectory(protonPath)) {
                continue;
            }
            try (Stream<Path> fileStream = Files.list(protonPath)) {
                Iterator<Path> iter = fileStream.filter(Files::isDirectory).iterator();
                while (iter.hasNext()) {
                    Path path = iter.next();
                    int matches = 0;
                    for (String[] fileSet : PROTON_FILES) {
                        for (String filePath : fileSet) {
                            if (Files.isRegularFile(path.resolve(filePath))) {
                                matches++;
                                break;
                            }
                        }
                    }
                    if (matches != PROTON_FILES.length) {
                        continue;
                    }
                    ProtonInstallation installation = parseInstallation(path);
                    if (installation == null) {
                        continue;
                    }
                    installations.add(installation);
                }
            }
        }
        Collections.sort(installations);
        for (ProtonReq proton : protons) {
            Optional<ProtonInstallation> installation = installations.stream()
                .filter(install -> install.name.equals(proton.name) && install.version.major == proton.majorVersion).findFirst();
            if (installation.isPresent()) {
                return installation.get().path;
            }
        }
        return null;
    }

    private static ProtonInstallation parseInstallation(Path path) {
        String protonName = path.getFileName().toString();
        int startDigit = -1, endDigit = -1;
        for (int i = 0; i < protonName.length(); i++) {
            char ch = protonName.charAt(i);
            if (Character.isDigit(ch)) {
                if (startDigit == -1) {
                    startDigit = i;
                }
            } else if (startDigit != -1 && Character.isAlphabetic(ch) && endDigit == -1) {
                endDigit = i;
                break;
            }
        }
        if (startDigit == -1) {
            return null;
        }
        String name = protonName.substring(0, startDigit);
        while (name.length() > 0 && !Character.isLetterOrDigit(name.charAt(0))) {
            name = name.substring(1);
        }
        while (name.length() > 0 && !Character.isLetterOrDigit(name.charAt(name.length() - 1))) {
            name = name.substring(0, name.length() - 1);
        }
        if (name.isEmpty()) {
            return null;
        }
        Version version;
        try {
            version = Version.parse(protonName.substring(startDigit, endDigit == -1 ? protonName.length() : endDigit));
        } catch (IllegalArgumentException iae) {
            return null;
        }
        return new ProtonInstallation(name.toLowerCase(), version, path);
    }

    public static boolean isPrefixSetup(Task task, Path prefix, ProtonInstance proton) {
        try (task) {
            if (!Files.isDirectory(prefix)) {
                return false;
            }
            int workPerFile = Math.floorDiv(task.availableWork(), PREFIX_FILES.length);
            for (String file : PREFIX_FILES) {
                if (!Files.exists(prefix.resolve(file))) {
                    return false;
                }
                task.work(workPerFile);
            }
            String dotnetResult = proton.queryRegistry("HKLM\\SOFTWARE\\Microsoft\\NET Framework Setup\\NDP\\v3.5", "Install");
            if (dotnetResult == null || !dotnetResult.equals("Install REG_DWORD 0x1")) {
                return false;
            }
            return true;
        }
    }

    public static boolean extractTar(Path filePath, Path targetPath) {
        targetPath = targetPath.toAbsolutePath().normalize();
        try (InputStream input = Files.newInputStream(filePath = filePath.toAbsolutePath())) {
            String fileName = filePath.getFileName().toString();
            InputStream finalIn = input;
            if (fileName.endsWith(".gz") || fileName.endsWith(".tgz")) {
                finalIn = new GZIPInputStream(input);
            }
            try (TarArchiveInputStream archive = new TarArchiveInputStream(finalIn)) {
                TarArchiveEntry entry;
                Path resolved;
                while ((entry = archive.getNextEntry()) != null) {
                    resolved = targetPath.resolve(entry.getName()).normalize();
                    if (!resolved.startsWith(targetPath)) {
                        throw new IOException("File is not inside target folder");
                    }
                    if (entry.isDirectory()) {
                        ensureWritableDirectory(resolved);
                        continue;
                    }
                    ensureWritableDirectory(resolved.getParent());
                    Files.copy(archive, resolved, StandardCopyOption.REPLACE_EXISTING);
                    ensureCorrectPermissions(resolved);
                }
            }
        } catch(IOException ie) {
            LauncherApp.LOGGER.error("Failed to extract tar file '{0}'", ie, filePath);
            return false;
        }
        return true;
    }

    public static void ensureWritableDirectory(Path path) throws IOException {
        if (!Files.exists(path)) {
            Files.createDirectories(path, PosixFilePermissions.asFileAttribute(WRITE_PERMISSIONS));
            return;
        } else if (!Files.isDirectory(path)) {
            IOUtil.delete(path);
            Files.createDirectories(path, PosixFilePermissions.asFileAttribute(WRITE_PERMISSIONS));
            return;
        }
        Files.setPosixFilePermissions(path, WRITE_PERMISSIONS);
    }

    public static void createWritableSymlink(Path symlink, Path target) throws IOException {
        if (Files.exists(symlink, LinkOption.NOFOLLOW_LINKS)) {
            Files.delete(symlink);
        } else if (symlink.getParent() != null && !Files.isDirectory(symlink.getParent())) {
            ensureWritableDirectory(symlink.getParent());
        }
        Files.createSymbolicLink(symlink, target);
        ensureCorrectPermissions(symlink);
    }

    public static void applyRegistryLanguageFix(ProtonInstance proton) {
        proton.setRegistry("HKEY_CURRENT_USER\\SOFTWARE\\EXE Games\\BrickForce", "BfVoice_h2155129175", "REG_DWORD", "1");
    }

    public static void applyWindowedFix(ProtonInstance proton) {
        proton.setRegistry("HKEY_CURRENT_USER\\SOFTWARE\\EXE Games\\BrickForce", "BfFullScreen_h3939460542", "REG_DWORD", "0");
        proton.setRegistry("HKEY_CURRENT_USER\\SOFTWARE\\EXE Games\\BrickForce", "BfScreenHeight_h1769511314", "REG_DWORD", "720");
        proton.setRegistry("HKEY_CURRENT_USER\\SOFTWARE\\EXE Games\\BrickForce", "BfScreenWidth_h596610443", "REG_DWORD", "1280");
        proton.setRegistry("HKEY_CURRENT_USER\\SOFTWARE\\EXE Games\\BrickForce", "Screenmanager Is Fullscreen mode_h3981298716",
            "REG_DWORD", "0");
        proton.setRegistry("HKEY_CURRENT_USER\\SOFTWARE\\EXE Games\\BrickForce", "Screenmanager Resolution Height_h2627697771", "REG_DWORD",
            "720");
        proton.setRegistry("HKEY_CURRENT_USER\\SOFTWARE\\EXE Games\\BrickForce", "Screenmanager Resolution Width_h182942802", "REG_DWORD",
            "1280");
    }

    public static void ensureCorrectPermissions(Path path) throws IOException {
        Files.setPosixFilePermissions(path, WRITE_PERMISSIONS);
    }

}
