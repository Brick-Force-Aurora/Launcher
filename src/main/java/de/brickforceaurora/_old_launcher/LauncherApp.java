//package de.brickforceaurora.old_launcher;
//
//import java.io.File;
//import java.io.IOException;
//import java.net.URISyntaxException;
//import java.nio.ByteBuffer;
//import java.nio.IntBuffer;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.concurrent.TimeUnit;
//
//import org.lwjgl.glfw.GLFW;
//import org.lwjgl.glfw.GLFWImage;
//import org.lwjgl.stb.STBImage;
//import org.lwjgl.system.MemoryStack;
//
//import de.brickforceaurora.launcher.GameData;
//import de.brickforceaurora.launcher.LauncherData;
//import de.brickforceaurora.launcher.helper.UIActionHelper;
//import de.brickforceaurora.launcher.ui.clay.RenderManager;
//import de.brickforceaurora.launcher.updater.UpdateManager;
//import de.brickforceaurora.launcher.updater.UpdaterConfig;
//import de.brickforceaurora.launcher.updater.github.GithubUpdater;
//import de.brickforceaurora.launcher.util.IOUtil;
//import de.brickforceaurora.old_launcher.data.DataStore;
//import de.brickforceaurora.old_launcher.ui.imgui.ImGuiStyler;
//import imgui.ImGui;
//import imgui.internal.ImGuiContext;
//import me.lauriichan.applicationbase.app.BaseApp;
//import me.lauriichan.applicationbase.app.resource.ISourceProvider;
//import me.lauriichan.applicationbase.app.resource.source.IDataSource;
//import me.lauriichan.applicationbase.app.resource.source.PathDataSource;
//import me.lauriichan.applicationbase.app.ui.BaseUIApp;
//import me.lauriichan.applicationbase.app.ui.ImGuiHandle;
//import me.lauriichan.applicationbase.app.ui.dock.DockNode;
//import me.lauriichan.applicationbase.app.ui.dock.DockNode.SplitDirection;
//import me.lauriichan.applicationbase.app.ui.util.AnimationTickTimer;
//import me.lauriichan.applicationbase.app.ui.util.ComponentTickTimer;
//import me.lauriichan.applicationbase.app.util.FileLogger;
//import me.lauriichan.laylib.logger.ISimpleLogger;
//
//public final class LauncherApp extends BaseUIApp {
//
//    private static LauncherApp app;
//    private static Path gameDirectory, tempDirectory;
//    private static DataStore launcherData, gameData;
//
//    private static UpdateManager updateManager;
//
//    public static final ComponentTickTimer COMPONENT_TIMER = new ComponentTickTimer();
//    public static final AnimationTickTimer GENERIC_ANIMATION_TIMER = new AnimationTickTimer();
//
//    public static final long COMPONENT_TIMER_LENGTH = 16_666_667;
//    public static final long GENERIC_ANIMATION_TIMER_LENGTH = 16_666_667;
//
//    public static final float COMPONENT_TIMER_RATIO = ((float) COMPONENT_TIMER_LENGTH) / TimeUnit.SECONDS.toNanos(1L);
//    public static final float GENERIC_ANIMATION_TIMER_RATIO = ((float) GENERIC_ANIMATION_TIMER_LENGTH) / TimeUnit.SECONDS.toNanos(1L);
//
//    public static LauncherApp app() {
//        return app;
//    }
//
//    public static Path gameDirectory() {
//        return gameDirectory;
//    }
//
//    public static Path tempDirectory() {
//        return tempDirectory;
//    }
//
//    public static UpdateManager updateManager() {
//        return updateManager;
//    }
//
//    static DataStore launcherData() {
//        return launcherData;
//    }
//
//    static DataStore gameData() {
//        return gameData;
//    }
//    
//    private RenderManager renderManager;
//
//    public LauncherApp() throws URISyntaxException {
//        super(BaseApp.getJarFile(BaseApp.class));
//        app = this;
//        start();
//    }
//
//    @Override
//    protected ISimpleLogger createLogger() {
//        return new FileLogger(new File("logs"));
//    }
//
//    @Override
//    protected Path createDataRoot() {
//        return Paths.get("data");
//    }
//
//    public Path resourcePath(String path) {
//        return IOUtil.asPath(resource(path));
//    }
//
//    @Override
//    protected void onAppPreLoad() throws Throwable {
//        resourceManager().register("user", new ISourceProvider() {
//            private final Path basePath = Paths.get("user");
//
//            @Override
//            public IDataSource provide(BaseApp app, String path) {
//                return new PathDataSource(basePath.resolve(path));
//            }
//        });
//    }
//    
//    @Override
//    protected void onAppLoad() throws Throwable {
//        renderManager = new RenderManager(this);
//    }
//
//    @Override
//    protected void onAppReady() throws Throwable {
//        gameDirectory = Paths.get(configManager().config(UpdaterConfig.class).directory());
//        tempDirectory = resourcePath("data://temp");
//
//        resourceManager().register("game", (app, path) -> new PathDataSource(gameDirectory.resolve(path)));
//
//        launcherData = new DataStore(resource("data://launcher.dat"));
//        LauncherData.init();
//        gameData = new DataStore(resource("game://launcher_data.dat"));
//        GameData.init();
//
//        updateManager = new UpdateManager(logger(), new GithubUpdater());
//        
//        renderManager = new RenderManager(this);
//    }
//
//    @Override
//    protected void createDock(DockNode node) {
//        node.direction(SplitDirection.VERTICAL);
//
//        node.newChild().id("titlebar").weight(0.05f);
//        node.newChild().id("panorama").weight(0.775f);
//
//        DockNode bottombar = node.newChild().id("bottombar").weight(0.175f).direction(SplitDirection.VERTICAL);
//
//        DockNode control = bottombar.newChild().id("controlbar").weight(0.75f).direction(SplitDirection.HORIZONTAL);
//        control.newChild().id("progress").weight(0.6f);
//        control.newChild().id("start").weight(0.2f);
//        control.newChild().id("settings").weight(0.2f);
//
//        bottombar.newChild().id("footer").weight(0.25f);
//    }
//
//    @Override
//    protected void onAppImGuiConfigure(ImGuiHandle.Config config) throws Throwable {
//        config.title = "BrickForce Aurora";
//        config.height = 580;
//        config.width = 800;
//        config.borderless = true;
//        config.transparent = true;
//    }
//
//    @Override
//    protected void onAppImGuiSetup(ImGuiContext context, ImGuiHandle.Config config) throws Throwable {
//        setWindowIcon("logo.png");
//        FontAtlas.load(this);
//        ImGui.getIO().setIniFilename(resourcePath("data://ui.ini").toString());
//        ImGui.getIO().setFontDefault(FontAtlas.NOTO_SANS_NORMAL);
//        ImGuiStyler.apply();
//
//        Constant.updateVariables();
//    }
//
//    @Override
//    protected void onAppImGuiStart(long windowHandle) throws Throwable {
//        TextureAtlas.load(this);
//    }
//
//    @Override
//    protected void onAppImGuiPostStart(long windowHandle) throws Throwable {
//        // 16.666667 ms
//        COMPONENT_TIMER.setLength(COMPONENT_TIMER_LENGTH, TimeUnit.NANOSECONDS);
//        COMPONENT_TIMER.setPauseLength(50, TimeUnit.MILLISECONDS);
//        COMPONENT_TIMER.start();
//        // 16.666667 ms
//        GENERIC_ANIMATION_TIMER.setLength(GENERIC_ANIMATION_TIMER_LENGTH, TimeUnit.NANOSECONDS);
//        GENERIC_ANIMATION_TIMER.setPauseLength(50, TimeUnit.MILLISECONDS);
//        GENERIC_ANIMATION_TIMER.start();
//    }
//
//    @Override
//    protected void onAppPostDock() throws Throwable {
//        Thread thread = new Thread(() -> UIActionHelper.runUpdate(true), "UpdateThread");
//        thread.setDaemon(true);
//        thread.start();
//    }
//
//    @Override
//    protected void onAppPreUpdate() throws Throwable {
//    }
//
//    @Override
//    protected void onAppShutdown() throws Throwable {
//        configManager().save();
//        dataManager().save();
//        try {
//            logger().info("Saving launcher data");
//            launcherData.save();
//        } catch (IOException exp) {
//            logger().error("Failed to save launcher data", exp);
//        }
//        try {
//            logger().info("Saving game data");
//            gameData.save();
//        } catch (IOException exp) {
//            logger().error("Failed to save game data", exp);
//        }
//
//        ((FileLogger) logger()).close();
//    }
//
//    @Override
//    protected void onAppDispose() throws Throwable {
//        COMPONENT_TIMER.stop();
//        GENERIC_ANIMATION_TIMER.stop();
//    }
//    
//    /*
//     * Getter
//     */
//    
//    public final RenderManager renderManager() {
//        return renderManager;
//    }
//
//    /*
//     * Helper
//     */
//
//    private void setWindowIcon(String path) {
//        ByteBuffer image;
//        int width, height;
//        try (MemoryStack stack = MemoryStack.stackPush()) {
//            String filePath = IOUtil.asPath(externalResource("jar://image/%s".formatted(path), "data://resources/image/%s".formatted(path)))
//                .toAbsolutePath().toString();
//            IntBuffer channelsBuf = stack.mallocInt(1);
//            IntBuffer widthBuf = stack.mallocInt(1);
//            IntBuffer heightBuf = stack.mallocInt(1);
//            image = STBImage.stbi_load(filePath, widthBuf, heightBuf, channelsBuf, 4);
//            if (image == null) {
//                throw new IOException("Unable to load image from resource");
//            }
//            width = widthBuf.get();
//            height = heightBuf.get();
//        } catch (IOException e) {
//            logger().warning("Failed to set window icon", e);
//            return;
//        }
//        try (GLFWImage.Buffer imgBuf = GLFWImage.create(1)) {
//            imgBuf.get(0).set(width, height, image);
//            GLFW.glfwSetWindowIcon(handle().handle(), imgBuf);
//        }
//    }
//
//}
package de.brickforceaurora._old_launcher;


