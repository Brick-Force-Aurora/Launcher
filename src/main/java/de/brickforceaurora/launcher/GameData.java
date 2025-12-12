package de.brickforceaurora.launcher;

import java.io.IOException;

import de.brickforceaurora.launcher.data.DataStore;
import de.brickforceaurora.launcher.data.StoredData;
import me.lauriichan.applicationbase.app.util.Version;

public final class GameData {

    private GameData() {
        throw new UnsupportedOperationException();
    }

    public static final DataStore STORE = LauncherApp.gameData();

    public static final StoredData<Version> GAME_VERSION = STORE.register("version", DataHandler.VERSION, new Version(0, 0, 0));

    static void init() {
        try {
            STORE.load();
        } catch (IOException e) {
            LauncherApp.app().logger().error("Failed to initialize game data", e);
        }
    }

}
