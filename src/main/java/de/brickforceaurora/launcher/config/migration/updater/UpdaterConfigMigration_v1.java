package de.brickforceaurora.launcher.config.migration.updater;

import static de.brickforceaurora.launcher.config.migration.MigrationHelper.*;

import de.brickforceaurora.launcher.config.UpdaterConfig;
import de.brickforceaurora.launcher.config.common.UpdaterType;
import me.lauriichan.snowframe.config.ConfigMigrationExtension;
import me.lauriichan.snowframe.config.Configuration;
import me.lauriichan.snowframe.extension.Extension;

@Extension
public class UpdaterConfigMigration_v1 extends ConfigMigrationExtension<UpdaterConfig> {

    public UpdaterConfigMigration_v1() {
        super(UpdaterConfig.class, 0, 1);
    }

    @Override
    public String description() {
        return "Updater rework (Addition of Shining Star)";
    }

    @Override
    public void migrate(Configuration config) throws Throwable {
        move(config, "game_directory", "game.aurora.directory");
        move(config, "update.check", "updater.common.check_for_update");
        move(config, "update.experimental", "updater.common.experimental");
        move(config, "update.automic", "updater.common.automatic_install");
        config.remove("update");
        move(config, "github", "updater.github");
        if ("Brick-Force-Aurora/Brick-Force".equals(config.get("updater.github.repository", String.class))) {
            config.set("updater.type", UpdaterType.SHINING_STAR);
        }
    }

}
