package de.brickforceaurora.launcher;

import java.io.BufferedReader;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;

import org.lwjgl.glfw.GLFW;

import me.lauriichan.snowframe.ImGUIModule;
import me.lauriichan.snowframe.SnowFrame;
import me.lauriichan.snowframe.resource.source.IDataSource;
import me.lauriichan.snowframe.util.Version;

public final class Main {

    private Main() {
        throw new UnsupportedOperationException();
    }

    private static final AtomicBoolean SHUT_DOWN = new AtomicBoolean(false);
    private static SnowFrame<LauncherApp> snowFrame;
    private static Version version = new Version(0, 0, 0);

    public static void main(final String[] args) throws Exception {
        snowFrame = LauncherApp.init(args);

        // DO UPDATES BEFORE STARTUP CHAIN
        final IDataSource source = snowFrame.resource("META-INF/maven/de.brickforce-aurora/launcher/pom.properties");
        if (source.exists()) {
            final Properties properties = new Properties();
            try (BufferedReader reader = source.openReader()) {
                properties.load(reader);
            }
            // If we don't know our version we choose 0.0.0 as its not a release
            if (properties.containsKey("version")) {
                version = Version.parse(properties.getProperty("version"));
            }
        }

        snowFrame.lifecycle().execute(SnowFrame.LIFECYCLE_CHAIN_STARTUP);
        snowFrame.lifecycle().execute(ImGUIModule.STARTUP_CHAIN);
    }

    public static Version version() {
        return version;
    }

    public static boolean isShutdown() {
        return SHUT_DOWN.get();
    }

    public static void shutdown() {
        if (!SHUT_DOWN.compareAndSet(false, true)) {
            return;
        }
        GLFW.glfwSetWindowShouldClose(snowFrame.module(ImGUIModule.class).windowPointer(), true);
    }

}