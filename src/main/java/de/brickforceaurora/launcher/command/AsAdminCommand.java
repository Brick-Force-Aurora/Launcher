package de.brickforceaurora.launcher.command;

import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.command.api.ICommand;
import de.brickforceaurora.launcher.platform.windows.WindowsPlatform;
import me.lauriichan.laylib.command.Actor;
import me.lauriichan.laylib.command.annotation.Action;
import me.lauriichan.laylib.command.annotation.Command;
import me.lauriichan.laylib.command.annotation.Description;
import me.lauriichan.snowframe.SnowFrame;
import me.lauriichan.snowframe.extension.Extension;
import me.lauriichan.snowframe.extension.ExtensionCondition;

@Extension
@ExtensionCondition(name = "os_win", condition = true, activeByDefault = false)
@Command(name = "as_admin", description = "Enables/Disables 'Start as Admin' for starting the game (until launcher is closed)")
public class AsAdminCommand implements ICommand {

    @Action("")
    @Description("Enables/Disables 'Start as Admin' for starting the game (until launcher is closed)")
    public void asAdmin(Actor<SnowFrame<LauncherApp>> actor) {
        WindowsPlatform.START_AS_ADMIN = !WindowsPlatform.START_AS_ADMIN;
        if (WindowsPlatform.START_AS_ADMIN) {
            actor.sendMessage("BrickForce will now be launched as admin");
        } else {
            actor.sendMessage("BrickForce will no longer be launched as admin");
        }
    }

}
