package de.brickforceaurora.launcher;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import de.brickforceaurora.launcher.data.DataStore;
import de.brickforceaurora.launcher.helper.UIActionHelper;
import de.brickforceaurora.launcher.ui.UserInterface;
import de.brickforceaurora.launcher.ui.clay.RenderManager;
import de.brickforceaurora.launcher.ui.imgui.ImGuiStyler;
import de.brickforceaurora.launcher.updater.UpdateManager;
import de.brickforceaurora.launcher.updater.UpdaterConfig;
import de.brickforceaurora.launcher.updater.github.GithubUpdater;
import de.brickforceaurora.launcher.util.IOUtil;
import imgui.ImGui;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.snowframe.ConfigModule;
import me.lauriichan.snowframe.ISnowFrameApp;
import me.lauriichan.snowframe.ImGUIModule;
import me.lauriichan.snowframe.SignalModule;
import me.lauriichan.snowframe.SnowFrame;
import me.lauriichan.snowframe.lifecycle.Lifecycle;
import me.lauriichan.snowframe.lifecycle.LifecyclePhase.Stage;
import me.lauriichan.snowframe.signal.SignalManager;
import me.lauriichan.snowframe.util.logger.FileLogger;

public final class LauncherApp implements ISnowFrameApp<LauncherApp> {
    
    public static final ScheduledExecutorService SCHEDULER = Executors.newScheduledThreadPool(1, Thread.ofVirtual().name("Scheduler").factory());

    private static SnowFrame<LauncherApp> snowFrame;

    static SnowFrame<LauncherApp> init(String[] args) {
        if (snowFrame != null) {
            return snowFrame;
        }

        // TODO: Do actual command line parsing
        ISimpleLogger logger = new FileLogger(new File("data/logs"));
        logger.setDebug(Arrays.stream(args).anyMatch(str -> str.equalsIgnoreCase("--debug")));

        return snowFrame = SnowFrame.builder(new LauncherApp()).logger(logger).build();
    }

    public static LauncherApp get() {
        return snowFrame.app();
    }

    public static ISimpleLogger logger() {
        return snowFrame.logger();
    }

    private Path gameDirectory, tempDirectory;
    private DataStore launcherData, gameData;

    private UpdateManager updateManager;
    private RenderManager renderManager;

    private SignalManager signalManager;

    private UserInterface userInterface;

    @Override
    public void registerLifecycle(Lifecycle<LauncherApp> lifecycle) {
        lifecycle.startupChain().register("load", Stage.PRE, (frame) -> {
            File dir = new File("").getAbsoluteFile();
            String path = "";
            if (dir.getName().equals("bin")) {
                path = "../";
            }

            frame.resourceManager().register("app", Paths.get(path));
            frame.resourceManager().register("user", Paths.get(path + "user"));
            frame.resourceManager().register("data", Paths.get(path + "data"));
        }).register("ready", Stage.MAIN, (frame) -> {
            gameDirectory = IOUtil.asPath(frame.resource("app://" + frame.module(ConfigModule.class).manager().config(UpdaterConfig.class).directory()));
            tempDirectory = IOUtil.asPath(frame.resource("data://temp"));

            frame.resourceManager().register("game", gameDirectory);
            frame.resourceManager().register("tmp", tempDirectory);

            launcherData = new DataStore(frame.resource("data://launcher.dat"));
            LauncherData.init();
            gameData = new DataStore(frame.resource("game://launcher_data.dat"));
            GameData.init();

            updateManager = new UpdateManager(frame.logger(), new GithubUpdater());

            renderManager = new RenderManager(frame);

            signalManager = frame.module(SignalModule.class).signalManager();
        });
        lifecycle.shutdownChain().register("shutdown", Stage.MAIN, (_) -> {
            launcherData.save();
            gameData.save();
        }).register("shutdown", Stage.POST, (frame) -> {
            if (frame.logger() instanceof FileLogger logger) {
                logger.close();
            }
        });
        lifecycle.chainOrThrow(ImGUIModule.STARTUP_CHAIN).register("setup", Stage.POST, frame -> {
            frame.module(ImGUIModule.class)
                .setWindowIcon(frame.externalResource("jar://image/logo.png", "data://resources/image/logo.png"));
            FontAtlas.load(frame);
            ImGui.getIO().setIniFilename(IOUtil.asPath(frame.resource("data://ui.ini")).toString());
            ImGui.getIO().setFontDefault(FontAtlas.NOTO_SANS_NORMAL);
            ImGuiStyler.apply();

            Constant.updateVariables();
        }).register("start", Stage.PRE, frame -> {
            TextureAtlas.load(frame);

            userInterface = new UserInterface(this);
            SCHEDULER.schedule(() -> UIActionHelper.runUpdate(true, false), 1, TimeUnit.SECONDS);
        }).register("start", Stage.POST, frame -> {
            // We call Main.shutdown(), this will notify the GLFW to close.
            // However we already know it should close since this lambda is called.
            // So either the user requested to close, which means the application isn't aware yet, so we make it aware.
            // Or Main.shutdown() was called already in which case this does nothing.
            Main.shutdown();
            frame.lifecycle().execute(SnowFrame.LIFECYCLE_CHAIN_SHUTDOWN);
        });
        lifecycle.chainOrThrow(ImGUIModule.RENDER_CHAIN).register("render", Stage.MAIN, _ -> userInterface.render());
    }

    @Override
    public SnowFrame<LauncherApp> snowFrame() {
        return snowFrame;
    }

    public Path gameDirectory() {
        return gameDirectory;
    }

    public Path tempDirectory() {
        return tempDirectory;
    }

    public SignalManager signalManager() {
        return signalManager;
    }

    public UpdateManager updateManager() {
        return updateManager;
    }

    public RenderManager renderManager() {
        return renderManager;
    }

    public UserInterface userInterface() {
        return userInterface;
    }

    DataStore launcherData() {
        return launcherData;
    }

    DataStore gameData() {
        return gameData;
    }

}
