package de.brickforceaurora.launcher.config.migration.updater;

import de.brickforceaurora.launcher.config.UpdaterConfig;
import me.lauriichan.snowframe.config.ConfigMigrationExtension;
import me.lauriichan.snowframe.config.Configuration;
import me.lauriichan.snowframe.extension.Extension;

@Extension
public class UpdaterConfigMigration_v2 extends ConfigMigrationExtension<UpdaterConfig> {

    public UpdaterConfigMigration_v2() {
        super(UpdaterConfig.class, 1, 2);
    }

    @Override
    public String description() {
        return "Reset shining star";
    }

    @Override
    public void migrate(Configuration config) throws Throwable {
        config.set("game.shining_star.directory", "game/shining_star");
    }

}
