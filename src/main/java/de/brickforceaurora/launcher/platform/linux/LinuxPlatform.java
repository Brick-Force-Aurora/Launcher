package de.brickforceaurora.launcher.platform.linux;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.concurrent.atomic.AtomicReference;

import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.Main;
import de.brickforceaurora.launcher.animation.property.PropFloat;
import de.brickforceaurora.launcher.animation.property.PropString;
import de.brickforceaurora.launcher.config.UpdaterConfig;
import de.brickforceaurora.launcher.platform.Platform;
import de.brickforceaurora.launcher.platform.linux.update.JarLauncherUpdater;
import de.brickforceaurora.launcher.ui.UserInterface;
import de.brickforceaurora.launcher.updater.UpdateManager;
import de.brickforceaurora.launcher.util.DownloadTracker;
import de.brickforceaurora.launcher.util.IOUtil;
import de.brickforceaurora.launcher.util.ProcessUtil;
import de.brickforceaurora.launcher.util.TaskTracker;
import de.brickforceaurora.launcher.util.TaskTracker.Task;
import me.lauriichan.laylib.command.Actor;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.snowframe.ConfigModule;
import me.lauriichan.snowframe.util.http.HttpCode;
import me.lauriichan.snowframe.util.http.HttpRequest;
import me.lauriichan.snowframe.util.http.HttpResponse;
import me.lauriichan.snowframe.util.http.type.HttpContentType;

public class LinuxPlatform extends Platform {

    private static final String URL_GE_PROTON_9_27 = "https://github.com/GloriousEggroll/proton-ge-custom/releases/download/GE-Proton9-27/GE-Proton9-27.tar.gz";
    private static final String URL_WINETRICKS = "https://raw.githubusercontent.com/Winetricks/winetricks/refs/heads/master/src/winetricks";

    private static final LinuxHelper.ProtonReq[] ALLOWED_PROTON = new LinuxHelper.ProtonReq[] {
        new LinuxHelper.ProtonReq("GE-Proton", 9),
        new LinuxHelper.ProtonReq("Proton", 9)
    };

    private static final Path HOME_DIR = Paths.get(System.getProperty("user.home"));
    private static final Path PROTON_INSTALL_LOC = HOME_DIR.resolve(".local/share/Steam/compatibilitytools.d");
    private static final Path[] PROTON_PATHS = new Path[] {
        HOME_DIR.resolve(".steam/root/compatibilitytools.d"),
        HOME_DIR.resolve(".steam/steam/compatibilitytools.d"),
        PROTON_INSTALL_LOC,
        HOME_DIR.resolve(".steam/root/steamapps/common"),
        HOME_DIR.resolve(".local/share/Steam/steamapps/common"),
        Paths.get("/usr/share/steam/compatibilitytools.d")
    };

    private final LauncherApp app;
    private final ISimpleLogger logger;
    private final Path steamPath, compatPath, winePrefix, linkPath, toolsPath;

    private final AtomicReference<ProtonInstance> protonRef = new AtomicReference<>();

    public LinuxPlatform(LauncherApp app) {
        this.app = app;
        this.logger = app.snowFrame().logger();
        Path protonPath = app.appDirectory().resolve("proton");
        this.toolsPath = protonPath.resolve("tools");
        this.steamPath = protonPath.resolve("steam");
        this.compatPath = protonPath.resolve("compat");
        this.winePrefix = compatPath.resolve("pfx");
        this.linkPath = winePrefix.resolve("drive_c/game/brickforce");
    }

    @Override
    public boolean needsPlatformSetup() {
        return true;
    }

    @Override
    public boolean doPlatformSetup(PropString mainText, PropFloat mainProgress) throws Exception {
        TaskTracker tracker = new TaskTracker(mainText, mainProgress, 1f, 100);
        Path protonPath;
        try (Task task = tracker.allocate("Setting up proton", 40)) {
            task.task("Finding supported proton version...");
            protonPath = LinuxHelper.findProton(ALLOWED_PROTON, PROTON_PATHS);
            task.work(10);
            if (protonPath == null) {
                task.task("Downloading GE-Proton9-27");
                LinuxHelper.ensureWritableDirectory(PROTON_INSTALL_LOC);
                LinuxHelper.ensureWritableDirectory(toolsPath);
                Path protonGePath = toolsPath.resolve("GEProton.tar.gz").toAbsolutePath();
                HttpResponse<byte[]> response = new HttpRequest().url(URL_GE_PROTON_9_27).readTimeout(2500)
                    .downloadListener(new DownloadTracker(task, 25)).call(HttpContentType.BINARY);
                if (response == null || response.code() != HttpCode.OK) {
                    logger.warning("Failed to install GE-Proton9-27 [HTTP: {0}]",
                        response == null ? "N/A" : response.code().code() + " " + response.code().name());
                    mainText.set("Failed to download GE-Proton9-27, please install it manually.");
                    return false;
                }
                try (FileOutputStream outputStream = new FileOutputStream(protonGePath.toFile())) {
                    outputStream.write(response.data().value());
                }
                task.task("Extracting GE-Proton9-27");
                try {
                    if (!LinuxHelper.extractTar(protonGePath, PROTON_INSTALL_LOC)) {
                        logger.warning("Failed to install GE-Proton9-27 [Extraction failed]");
                        mainText.set("Failed to install GE-Proton9-27, please install it manually.");
                        return false;
                    }
                } finally {
                    IOUtil.delete(protonGePath);
                }
                protonPath = PROTON_INSTALL_LOC.resolve("GE-Proton9-27");
            }
            if (!Files.exists(compatPath)) {
                LinuxHelper.ensureWritableDirectory(compatPath);
            }
            if (!Files.exists(steamPath.resolve("steamapps"))) {
                LinuxHelper.ensureWritableDirectory(steamPath.resolve("steamapps"));
            }
        }
        ProtonInstance proton = new ProtonInstance(protonPath, compatPath, steamPath);
        this.protonRef.set(proton);

        var protonResult = proton.executeProtonAwait("version");
        if (protonResult.exception() != null || protonResult.code() != 0) {
            if (protonResult.exception() != null) {
                logger.error("Failed to check proton version", protonResult.exception());
            } else {
                logger.error("Failed to check proton version (code: {0}): {1}", protonResult.code(), protonResult.result());
            }
            mainText.set("Failed to check proton version");
            return false;
        }
        logger.info("Using proton '{0}'", protonResult.result());

        if (!LinuxHelper.isPrefixSetup(tracker.allocate("Checking proton prefix...", 10), winePrefix, proton)) {
            try (Task task = tracker.allocate("Creating proton prefix...", 35)) {
                task.task("Clearing old prefix");
                if (Files.isDirectory(winePrefix)) {
                    // Wipe if exists to setup a new prefix instead
                    IOUtil.delete(winePrefix);
                }
                task.work(5);
                task.task("Getting winetricks");
                String wineTricksPath = proton.wineTricksPath();
                boolean worked = false;
                if (wineTricksPath == null) {
                    if (ProcessUtil.runAndRead(new String[] {
                        "which",
                        "winetricks"
                    }).code() == 0) {
                        wineTricksPath = "winetricks";
                    } else {
                        if (ProcessUtil.runAndRead(new String[] {
                            "which",
                            "cabextract"
                        }).code() != 0) {
                            logger.warning("Failed to install winetricks because 'cabextract' is missing");
                            mainText.set("Can't setup winetricks, please install winetricks or cabextract and try again.");
                            return false;
                        }
                        Path wineTricks = toolsPath.resolve("winetricks");
                        wineTricksPath = wineTricks.toAbsolutePath().toString();
                        if (!Files.exists(wineTricks)) {
                            worked = true;
                            HttpResponse<byte[]> response = new HttpRequest().url(URL_WINETRICKS).readTimeout(2500)
                                .downloadListener(new DownloadTracker(task, 14)).call(HttpContentType.BINARY);
                            if (response == null || response.code() != HttpCode.OK) {
                                logger.warning("Failed to download winetricks [HTTP: {0}]",
                                    response == null ? "N/A" : response.code().code() + " " + response.code().name());
                                mainText.set("Failed to download winetricks, please install it manually.");
                                return false;
                            }
                            LinuxHelper.ensureWritableDirectory(toolsPath);
                            try (FileOutputStream outputStream = new FileOutputStream(wineTricks.toFile())) {
                                outputStream.write(response.data().value());
                            }
                            LinuxHelper.ensureCorrectPermissions(wineTricks);
                            task.work(1);
                        }
                    }
                }
                if (!worked) {
                    task.work(15);
                }
                task.task("Creating prefix with dotnet");
                LinuxHelper.ensureWritableDirectory(winePrefix);
                if (proton.executeAwait(wineTricksPath, "-q", "dotnet35").code() != 0) {
                    logger.warning("Failed to setup .net3.5 using winetricks");
                    mainText.set("Failed to setup proton prefix, please try again.");
                    return false;
                }
            }
            if (!LinuxHelper.isPrefixSetup(tracker.allocate("Checking proton prefix...", 10), winePrefix, proton)) {
                // Great the setup is fucked
                logger.warning("IDK, proton prefix is just fucked");
                mainText.set("Failed to setup proton prefix, please try again.");
                return false;
            }
        }

        try (Task _ = tracker.allocate("Setting up game link", 5)) {
            LinuxHelper.ensureWritableDirectory(winePrefix);
            LinuxHelper.createWritableSymlink(winePrefix.resolve(linkPath), app.gameDirectory());
        }
        return true;
    }

    @Override
    public void applyLanguageFix(Actor<?> actor) {
        ProtonInstance proton = protonRef.get();
        if (proton == null) {
            actor.sendMessage("To run this command you have to start the game at least once while the launcher is open.");
            return;
        }
        LinuxHelper.applyRegistryLanguageFix(proton);
        actor.sendMessage("Applied registry language fix.");
    }

    @Override
    public void applyWindowedFix(Actor<?> actor) {
        ProtonInstance proton = protonRef.get();
        if (proton == null) {
            actor.sendMessage("To run this command you have to start the game at least once while the launcher is open.");
            return;
        }
        LinuxHelper.applyWindowedFix(proton);
        actor.sendMessage("Applied registry windowed fix.");
    }

    @Override
    public boolean updateLauncher() {
        final UpdaterConfig config = app.snowFrame().module(ConfigModule.class).manager().config(UpdaterConfig.class);
        final UserInterface userInterface = app.userInterface();

        userInterface.newVersionAvailable.set(false);
        userInterface.newVersionText.set("");

        userInterface.mainText.set("Checking for launcher updates...");
        userInterface.mainProgress.set(0.00f);

        final UpdateManager updateManager = new UpdateManager(app.snowFrame().logger(), new JarLauncherUpdater());
        if (!updateManager.checkForUpdates(config, Main.version()) || !updateManager.hasUpdates()) {
            return false;
        }

        if (Main.JAR_FILE_NAME == null) {
            // We can't update if we don't know the jar file name, YOU ARE RUNNING IN IDE bruh
            return false;
        }

        if (updateManager.applyUpdates(new TaskTracker(userInterface.mainText, userInterface.mainProgress, 1f, 100),
            app.appDirectory()) != null) {
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                Path appPath = app.appDirectory().resolve(Main.JAR_FILE_NAME);
                try {
                    Files.move(app.appDirectory().resolve(JarLauncherUpdater.NEW_LAUNCHER_JAR), appPath,
                        StandardCopyOption.REPLACE_EXISTING);
                } catch (final IOException _) {
                }
                String[] arguments = new String[logger.isDebug() ? 4 : 3];
                arguments[0] = "setsid";
                arguments[1] = "%s/bin/java".formatted(System.getProperty("java.home"));
                arguments[2] = appPath.toAbsolutePath().toString();
                arguments[3] = "--debug";
                try {
                    new ProcessBuilder(arguments).start();
                } catch (final IOException _) {
                }
            }));
            Main.shutdown();
            return true;
        }
        return false;
    }

    @Override
    public void startGame() {
        final String gamePath = linkPath.resolve("BrickForce.exe").toString();
        logger.info("Launching game '{0}'", gamePath);
        try {
            ProtonInstance instance = protonRef.get();
            if (instance == null) {
                throw new IllegalStateException("Proton is not set up, how?");
            }
            instance.execute(linkPath, gamePath);
        } catch (final IOException e) {
            logger.error("Failed to launch game", e);
        }
    }

}
