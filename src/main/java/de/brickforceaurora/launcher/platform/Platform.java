package de.brickforceaurora.launcher.platform;

import java.nio.file.Path;

import de.brickforceaurora.launcher.CondConstant;
import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.animation.property.PropFloat;
import de.brickforceaurora.launcher.animation.property.PropString;
import de.brickforceaurora.launcher.platform.linux.LinuxPlatform;
import de.brickforceaurora.launcher.platform.windows.WindowsPlatform;

public abstract class Platform {
    
    public static final Platform createPlatform() {
        switch(CondConstant.OS) {
        case LINUX:
            return new LinuxPlatform(LauncherApp.get());
        case WINDOWS:
            return new WindowsPlatform(LauncherApp.get());
        default:
            throw new IllegalStateException("Unsupported OS");
        }
    }

    public abstract Path resolveGameDir(Path path);

    public void doPlatformSetup(PropString mainText, PropFloat mainProgress) {};

    public abstract void applyLanguageFix();

    public abstract void applyWindowedFix();

    public abstract boolean updateLauncher();

    public abstract void startGame();

}
