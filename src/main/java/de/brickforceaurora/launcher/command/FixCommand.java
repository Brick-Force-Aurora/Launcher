package de.brickforceaurora.launcher.command;

import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.command.api.ICommand;
import de.brickforceaurora.launcher.helper.WindowsHelper;
import me.lauriichan.laylib.command.Actor;
import me.lauriichan.laylib.command.annotation.Action;
import me.lauriichan.laylib.command.annotation.Command;
import me.lauriichan.laylib.command.annotation.Description;
import me.lauriichan.snowframe.SnowFrame;
import me.lauriichan.snowframe.extension.Extension;

@Extension
@Command(name = "fix", description = "Various fix command")
public class FixCommand implements ICommand {

    @Action("language")
    @Description("Sets the language of BrickForce to english in order to fix the 'Download Once' bug.")
    public void languageFix(Actor<SnowFrame<LauncherApp>> actor) {
        WindowsHelper.applyRegistryLanguageFix();
        actor.sendMessage("Applied registry language fix.");
    }

}
