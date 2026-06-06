package de.brickforceaurora.launcher.updater.shiningstar.command;

import java.io.IOException;

import de.brickforceaurora.launcher.updater.shiningstar.command.api.IUpdateCommand;
import de.brickforceaurora.launcher.updater.shiningstar.command.api.UpdateActor;
import me.lauriichan.laylib.command.annotation.Action;
import me.lauriichan.laylib.command.annotation.Argument;
import me.lauriichan.laylib.command.annotation.Command;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.snowframe.extension.Extension;
import me.lauriichan.snowframe.resource.source.IDataSource;

@Extension
@Command(name = "move")
public class MoveCommand implements IUpdateCommand {

    @Action("")
    public void apply(ISimpleLogger logger, UpdateActor actor, @Argument(name = "source target path", index = 0) String sourceTargetPath,
        @Argument(name = "target path", index = 1) String targetPath) throws IOException {
        IDataSource src = actor.target(sourceTargetPath);
        if (!src.exists()) {
            logger.warning("Source of move instruction doesn't exist, skipping");
            return;
        }
        src.transferTo(actor.target(targetPath));
        src.delete();
    }

}
