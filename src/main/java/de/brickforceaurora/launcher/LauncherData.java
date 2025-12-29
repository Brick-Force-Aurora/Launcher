package de.brickforceaurora.launcher;

import java.io.IOException;

import de.brickforceaurora.launcher.data.DataStore;

public final class LauncherData {

    private LauncherData() {
        throw new UnsupportedOperationException();
    }

    public static final DataStore STORE = LauncherApp.get().launcherData();

    static void init() {
        try {
            STORE.load();
        } catch (IOException e) {
            LauncherApp.logger().error("Failed to initialize launcher data", e);
        }
    }

}
