package de.brickforceaurora.launcher.updater.shiningstar;

import java.io.IOException;
import java.util.Arrays;

import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.command.argument.LoggerArgument;
import de.brickforceaurora.launcher.config.UpdaterConfig;
import de.brickforceaurora.launcher.updater.IUpdate;
import de.brickforceaurora.launcher.updater.IUpdater;
import de.brickforceaurora.launcher.updater.shiningstar.command.api.IUpdateCommand;
import de.brickforceaurora.launcher.updater.shiningstar.command.api.IncompleteInstructionException;
import de.brickforceaurora.launcher.updater.shiningstar.command.api.UpdateActor;
import de.brickforceaurora.launcher.util.CLIUtil;
import de.brickforceaurora.launcher.util.StringReader;
import it.unimi.dsi.fastutil.objects.ObjectList;
import me.lauriichan.laylib.command.ArgumentRegistry;
import me.lauriichan.laylib.command.CommandManager;
import me.lauriichan.laylib.command.CommandProcess;
import me.lauriichan.laylib.command.Node;
import me.lauriichan.laylib.command.NodeArgument;
import me.lauriichan.laylib.command.NodeCommand;
import me.lauriichan.laylib.command.util.Triple;
import me.lauriichan.laylib.json.IJson;
import me.lauriichan.laylib.json.JsonArray;
import me.lauriichan.laylib.json.JsonObject;
import me.lauriichan.laylib.json.io.JsonWriter;
import me.lauriichan.laylib.logger.ISimpleLogger;
import me.lauriichan.snowframe.SnowFrame;
import me.lauriichan.snowframe.util.Version;
import me.lauriichan.snowframe.util.http.HttpCode;
import me.lauriichan.snowframe.util.http.HttpRequest;
import me.lauriichan.snowframe.util.http.HttpResponse;
import me.lauriichan.snowframe.util.http.data.MultiFormData;
import me.lauriichan.snowframe.util.http.type.HttpContentType;

public final class ShiningStarUpdater implements IUpdater {

    static final JsonWriter JSON_WRITER = new JsonWriter().setPretty(false);

    static void logJsonResponse(ISimpleLogger logger, HttpResponse<IJson<?>> response) throws IOException {
        String info = "";
        if (response.data().isError()) {
            info = response.data().error().getMessage();
        } else {
            info = response.data().value() == null ? "NO_CONTENT" : JSON_WRITER.toString(response.data().value());
        }
        logger.debug("Received unexpected response from updater service: " + info);
    }

    static void logFormResponse(ISimpleLogger logger, HttpResponse<MultiFormData> response) throws IOException {
        String info = "";
        if (response.data().isError()) {
            info = response.data().error().getMessage();
        } else {
            info = response.data().value() == null ? "NO_CONTENT" : response.data().value().toString();
        }
        logger.debug("Received unexpected response from updater service: " + info);
    }

    private final CommandManager commandManager;

    public ShiningStarUpdater(SnowFrame<LauncherApp> snowFrame) {
        commandManager = new CommandManager(snowFrame.logger());
        ArgumentRegistry registry = commandManager.getRegistry();
        registry.setProvider(new LoggerArgument(snowFrame.logger()));
        snowFrame.extension(IUpdateCommand.class, false).callClasses(commandManager::register);
    }

    @Override
    public void checkForUpdate(UpdaterConfig config, ISimpleLogger logger, Version current, ObjectList<IUpdate> updates)
        throws IOException {
        HttpResponse<IJson<?>> response = new HttpRequest().url(config.shiningStarUrl("product/id"))
            .param("product_name", config.productType()).call(HttpContentType.JSON);
        if (response.code() != HttpCode.OK) {
            logJsonResponse(logger, response);
            throw new IOException("Unexpected response from updater service: " + response.code().code());
        }
        String productId = response.data().value().asJsonObject().getAsObject("info").getAsString("id");

        HttpRequest request = new HttpRequest().url(config.shiningStarUrl("update/list")).param("product_id", productId);
        int maxPage = 0, page = 0;
        while (page <= maxPage) {
            response = request.param("page", page++).call(HttpContentType.JSON);
            if (response.code() != HttpCode.OK) {
                logJsonResponse(logger, response);
                throw new IOException("Unexpected response from updater service: " + response.code().code());
            }
            JsonObject object = response.data().value().asJsonObject().getAsObject("data");
            maxPage = object.getAsInt("max_page", 0);
            JsonArray versionArray = object.getAsArray("versions");
            for (IJson<?> versionRaw : versionArray) {
                Version version = Version.parse(versionRaw.asString());
                if (!version.isHigher(current)) {
                    continue;
                }
                updates.add(new ShiningStarUpdate(this, config, productId, current, version));
            }
        }
    }

    @Override
    public boolean bunbledUpdates() {
        return true;
    }

    final CommandProcess parseInstruction(ISimpleLogger logger, UpdateActor actor, String instruction)
        throws IncompleteInstructionException {
        String[] args = CLIUtil.toArguments(instruction);
        String label = args[0];
        args = Arrays.copyOfRange(args, 1, args.length);
        final Triple<NodeCommand, Node, String> triple = commandManager.findNode(label, args);
        if (triple == null || triple.getB().getAction() == null) {
            throw new IncompleteInstructionException("Unknown instruction '%s' for '%s'".formatted(label, instruction));
        }
        final CommandProcess process = new CommandProcess(triple.getC(), triple.getB().getAction(), triple.getA().getInstance());
        NodeArgument argument = process.findNext(actor);
        if (argument == null) {
            return process;
        }
        final StringReader reader = new StringReader(CLIUtil.recombine(label, args));
        String data;
        while (argument != null) {
            if (!reader.skipWhitespace().hasNext()) {
                if (argument.isOptional()) {
                    process.skip(actor);
                    argument = process.findNext(actor);
                    continue;
                }
                throw new IncompleteInstructionException("Instruction argument was not provided");
            }
            data = reader.read();
            try {
                process.provide(actor, data);
            } catch (final IllegalArgumentException exp) {
                throw new IncompleteInstructionException("Couldn't parse instruction argument; " + exp.getMessage(), exp);
            }
            argument = process.findNext(actor);
        }
        return process;
    }

    final void executeInstruction(UpdateActor actor, CommandProcess instruction) throws Throwable {
        commandManager.executeProcessWithError(actor, instruction);
    }

}
