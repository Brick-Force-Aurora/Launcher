package de.brickforceaurora.launcher.command.argument;

import me.lauriichan.laylib.command.Actor;
import me.lauriichan.laylib.command.IProviderArgumentType;
import me.lauriichan.laylib.logger.ISimpleLogger;

public final class LoggerArgument implements IProviderArgumentType<ISimpleLogger> {
    
    private final ISimpleLogger logger;
    
    public LoggerArgument(ISimpleLogger logger) {
        this.logger = logger;
    }

    @Override
    public ISimpleLogger provide(Actor<?> actor) {
        return logger;
    }

}
