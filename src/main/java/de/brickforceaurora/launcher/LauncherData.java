package de.brickforceaurora.launcher;

import java.io.IOException;

import de.brickforceaurora.launcher.data.DataStore;

public final class LauncherData {

    private LauncherData() {
        throw new UnsupportedOperationException();
    }

    public static final DataStore STORE = LauncherApp.launcherData();

    static void init() {
        try {
            STORE.load();
        } catch (IOException e) {
            LauncherApp.app().logger().error("Failed to initialize launcher data", e);
        }
    }

}
