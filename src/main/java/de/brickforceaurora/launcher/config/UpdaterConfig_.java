package de.brickforceaurora.launcher.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.atomic.AtomicReference;

import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.config.common.ProductType;
import de.brickforceaurora.launcher.config.common.UpdaterType;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.snowframe.SnowFrame;
import me.lauriichan.snowframe.config.Config;
import me.lauriichan.snowframe.config.ConfigValue;
import me.lauriichan.snowframe.config.ConfigValueValidator;
import me.lauriichan.snowframe.config.Configuration;
import me.lauriichan.snowframe.config.IConfigHandler;
import me.lauriichan.snowframe.config.ISingleConfigExtension;
import me.lauriichan.snowframe.config.handler.JsonConfigHandler;
import me.lauriichan.snowframe.extension.Extension;
import me.lauriichan.snowframe.resource.ResourceManager;

@Extension
@Config(automatic = true)
public class UpdaterConfig_ implements ISingleConfigExtension {

    public final AtomicReference<Path> gameDirectory = new AtomicReference<>();

    @ConfigValue("game.aurora.directory")
    public String auroraDirectory = "game/aurora";
    @ConfigValue("game.shining_star.directory")
    public String shiningStarDirectory = "game/shining_star";

    @ConfigValue("game.selected_game")
    public ProductType productType = ProductType.AURORA;

    @ConfigValue("updater.type")
    public UpdaterType updaterType = UpdaterType.SHINING_STAR;

    @ConfigValue("updater.common.check_for_update")
    public boolean checkForUpdates = true;
    @ConfigValue("updater.common.experimental")
    public boolean experimental = false;
    @ConfigValue("updater.common.automatic_install")
    public boolean automaticUpdate = false;

    @ConfigValue("updater.shining_star.host")
    public String shiningStarHost = "https://api.brickforce-aurora.de/updater/v1";

    @ConfigValue("updater.github.repository")
    public String githubRepository = "Brick-Force-Aurora/Brick-Force";
    @ConfigValue("updater.github.auth_token")
    public String githubAuthToken;

    private final SnowFrame<LauncherApp> snowFrame;

    public UpdaterConfig_(SnowFrame<LauncherApp> snowFrame) {
        this.snowFrame = snowFrame;
        updateGameDirectory();
    }

    @Override
    public IConfigHandler handler() {
        return JsonConfigHandler.JSON;
    }

    @Override
    public String path() {
        return "user://updater.json";
    }

    @ConfigValueValidator(value = "updater.shining_star.host")
    public String ensureSomewhatValidUrl(String value) {
        if (value.charAt(value.length() - 1) != '/') {
            value += '/';
        }
        if (!(value.startsWith("http://") || value.startsWith("https://"))) {
            value = "http://" + value;
        }
        return value;
    }

    public String shiningStarUrl(String path) {
        return shiningStarHost + path;
    }

    @Override
    public void onLoad(ISimpleLogger logger, Configuration configuration) throws Exception {
        updateGameDirectory();
    }

    private void auroraDirectory(String string) {}

    private void shiningStarDirectory(String string) {}

    public void gameDirectory(Path path) {
        switch (productType) {
        case AURORA:
            auroraDirectory(path.toString());
        default:
        case SHINING_STAR:
            shiningStarDirectory(path.toString());
        }
        updateGameDirectory();
    }

    private void updateGameDirectory() {
        String gameDirPath = switch (productType) {
        case AURORA:
            yield auroraDirectory;
        default:
        case SHINING_STAR:
            yield shiningStarDirectory;
        };
        Path path = Paths.get(gameDirPath);
        if (!path.isAbsolute()) {
            path = snowFrame.app().appDirectory().resolve(path);
        }
        gameDirectory.set(path);
        ResourceManager<LauncherApp> resources = snowFrame.resourceManager();
        resources.unregister("game");
        resources.register("game", path);
    }

}
