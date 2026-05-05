package de.brickforceaurora.launcher.platform.linux;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.animation.property.PropFloat;
import de.brickforceaurora.launcher.animation.property.PropString;
import de.brickforceaurora.launcher.config.UpdaterConfig;
import de.brickforceaurora.launcher.platform.Platform;
import de.brickforceaurora.launcher.ui.UserInterface;
import me.lauriichan.laylib.command.util.Reference;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.snowframe.ConfigModule;

public class LinuxPlatform extends Platform {

    private final LauncherApp app;
    private final ISimpleLogger logger;
    private final Reference<Path> winePrefix = Reference.of();

    public LinuxPlatform(LauncherApp app) {
        this.app = app;
        this.logger = app.snowFrame().logger();
    }

    @Override
    public Path resolveGameDir(Path path) {
        winePrefix.set(path);
        return path.resolve("drive_c/users/bfuser/game");
    }
    
    @Override
    public void doPlatformSetup(PropString mainText, PropFloat mainProgress) {
        Path path = winePrefix.get();
        if (Files.exists(path.resolve("drive_c"))) {
            // If this exists we expect the prefix to be set up
            return;
        }
        try {
            Files.createDirectories(path);
        } catch (IOException e) {
            logger.warning("Failed to create wine prefix directory", e); 
        }
        // TODO: Setup wine
    }

    @Override
    public void applyLanguageFix() {
        // TODO: Figure out how to run registry in wine prefix
    }

    @Override
    public void applyWindowedFix() {
        // TODO: Figure out how to run registry in wine prefix
    }

    @Override
    public boolean updateLauncher() {
        final UpdaterConfig config = app.snowFrame().module(ConfigModule.class).manager().config(UpdaterConfig.class);
        final UserInterface userInterface = app.userInterface();

        return true;
    }

    @Override
    public void startGame() {
        
    }

}
