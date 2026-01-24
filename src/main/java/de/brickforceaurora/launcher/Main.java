package de.brickforceaurora.launcher;

import java.util.concurrent.atomic.AtomicBoolean;

import org.lwjgl.glfw.GLFW;

import me.lauriichan.snowframe.ImGUIModule;
import me.lauriichan.snowframe.SnowFrame;

public final class Main {

    private Main() {
        throw new UnsupportedOperationException();
    }

    private static final AtomicBoolean SHUT_DOWN = new AtomicBoolean(false);
    private static SnowFrame<LauncherApp> snowFrame;

    public static void main(String[] args) throws Exception {
        snowFrame = LauncherApp.init(args);
        snowFrame.lifecycle().execute(SnowFrame.LIFECYCLE_CHAIN_STARTUP);
        snowFrame.lifecycle().execute(ImGUIModule.STARTUP_CHAIN);
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