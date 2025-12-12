package de.brickforceaurora.launcher.updater;

import me.lauriichan.applicationbase.app.config.Config;
import me.lauriichan.applicationbase.app.config.ConfigValue;
import me.lauriichan.applicationbase.app.config.IConfigHandler;
import me.lauriichan.applicationbase.app.config.ISingleConfigExtension;
import me.lauriichan.applicationbase.app.config.handler.JsonConfigHandler;
import me.lauriichan.applicationbase.app.extension.Extension;

@Extension
@Config(automatic = true)
public class UpdaterConfigTemplate implements ISingleConfigExtension {

    @ConfigValue("game_directory")
    public String directory = "game";

    @ConfigValue("update.check")
    public boolean checkForUpdates = true;
    @ConfigValue("update.experimental")
    public boolean experimental = false;
    @ConfigValue("update.automatic")
    public boolean automaticUpdate = false;

    @ConfigValue("github.repository")
    public String githubRepository = "Brick-Force-Aurora/Brick-Force";
    @ConfigValue("github.auth.token")
    public String githubAuthToken;

    @Override
    public IConfigHandler handler() {
        return JsonConfigHandler.JSON;
    }

    @Override
    public String path() {
        return "user://updater.json";
    }

}
