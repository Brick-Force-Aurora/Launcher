package de.brickforceaurora.launcher.updater.shiningstar.command.api;

import java.util.UUID;

import me.lauriichan.laylib.command.Actor;
import me.lauriichan.laylib.localization.MessageManager;
import me.lauriichan.snowframe.resource.source.IDataSource;

public final class UpdateActor extends Actor<IDataSource> {

    private static final MessageManager MNG = new MessageManager();

    private final IDataSource target;

    public UpdateActor(IDataSource source, IDataSource target) {
        super(source, MNG);
        this.target = target;
    }

    @Override
    public UUID getId() {
        return Actor.IMPL_ID;
    }

    @Override
    public String getName() {
        return getHandle().getPath();
    }

    public IDataSource source() {
        return handle;
    }

    public IDataSource source(String path) {
        if (path.contains("..")) {
            path = path.replace("../", "");
            if (path.contains("..")) {
                path = path.replace("..", "");
            }
        }
        return handle.resolve(path);
    }

    public IDataSource target() {
        return target;
    }

    public IDataSource target(String path) {
        if (path == null || path.isBlank()) {
            path = "";
        } else if (path.contains("..")) {
            path = path.replace("../", "");
            if (path.contains("..")) {
                path = path.replace("..", "");
            }
        }
        return target.resolve(path);
    }

}
