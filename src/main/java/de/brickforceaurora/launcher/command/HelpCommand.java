package de.brickforceaurora.launcher.command;

import java.util.List;

import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.Main;
import de.brickforceaurora.launcher.command.api.ICommand;
import me.lauriichan.laylib.command.Actor;
import me.lauriichan.laylib.command.CommandManager;
import me.lauriichan.laylib.command.Node;
import me.lauriichan.laylib.command.NodeAction;
import me.lauriichan.laylib.command.NodeArgument;
import me.lauriichan.laylib.command.NodeCommand;
import me.lauriichan.laylib.command.annotation.Action;
import me.lauriichan.laylib.command.annotation.Argument;
import me.lauriichan.laylib.command.annotation.Command;
import me.lauriichan.laylib.command.annotation.Description;
import me.lauriichan.snowframe.SnowFrame;
import me.lauriichan.snowframe.extension.Extension;

@Extension
@Command(name = "help", aliases = "?", description = "Shows the help for commands")
public class HelpCommand implements ICommand {
    
    private static final String BOX_START = "||  ";
    private static final String BOX_END = " ||";
    private static final char BOX_OUTLINE = '=';
    private static final char BOX_FILL = ' ';
    
    private static final String APP_NAME = "Aurora Launcher";

    @Action("")
    @Description("Shows the welcome help message or the help for one command")
    public void run(Actor<SnowFrame<LauncherApp>> actor, CommandManager commandManager, @Argument(name = "command", optional = true) String command) {
        if (command == null || command.isEmpty()) {
            doHelp(actor, commandManager);
            return;
        }
        NodeCommand nodeCommand = commandManager.getCommand(command);
        if (nodeCommand == null) {
            actor.sendMessage("Unknown command '%s'".formatted(command));
            return;
        }
        actor.sendMessage("");
        recursivePrint(actor, 0, nodeCommand.getNode());
        actor.sendMessage("");
    }

    private void recursivePrint(Actor<SnowFrame<LauncherApp>> actor, int depth, Node node) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < depth; i++) {
            builder.append("  ");
        }
        if (depth == 0) {
            builder.append("/");
        }
        builder.append(node.getName());
        NodeAction action = node.getAction();
        if (action != null) {
            List<NodeArgument> arguments = action.getArguments();
            for (NodeArgument argument : arguments) {
                if (argument.isProvided()) {
                    continue;
                }
                builder.append(" (").append(argument.getName());
                if (argument.isOptional()) {
                    builder.append('?');
                }
                builder.append(')');
            }
            if (action.getDescription() != null && !(action.getDescription().isBlank() || action.getDescription().equals("N/A"))) {
                int textLength = builder.append(" - ").length();
                int maxLength = 80;
                int threshhold = Math.round(maxLength * 0.25f);
                
                int length = Math.max(maxLength - textLength, threshhold);
                String desc = action.getDescription();
                int descLength = desc.length();
                if (descLength <= length) {
                    builder.append(desc);
                } else {
                    String[] parts = desc.split(" ");
                    String part;
                    int partLen = 0;
                    int curLen = 0;
                    for (int i = 0; i < parts.length; i++) {
                        int add = i == 0 ? 0 : 1;
                        part = parts[i];
                        if ((partLen = part.length()) + curLen + add <= length) {
                            curLen += partLen + add;
                            if (i != 0) {
                                builder.append(' ');
                            }
                            builder.append(part);
                            continue;
                        }
                        int possibleLen = length - curLen - add;
                        if (partLen > length) {
                            if (i != 0) {
                                builder.append(' ');
                            }
                            int tmp;
                            int tmpStart = 0;
                            while (partLen > length) {
                                tmp = tmpStart + possibleLen - 1;
                                builder.append(part.substring(tmpStart, tmp)).append("-\n");
                                for (int s = 0; s < textLength; s++) {
                                    builder.append(' ');
                                }
                                partLen -= possibleLen - 1;
                                tmpStart = tmp;
                                possibleLen = length;
                            }
                            builder.append(part.substring(tmpStart));
                            curLen = partLen;
                            continue;
                        }
                        // Just do a next line
                        for (int l = curLen; l < length; l++) {
                            builder.append(' ');
                        }
                        builder.append("\n");
                        for (int s = 0; s < textLength; s++) {
                            builder.append(' ');
                        }
                        curLen = partLen;
                        builder.append(part);
                    }
                }
            }
            
        }
        actor.sendMessage(builder.toString());
        for (String name : node.getNames()) {
            recursivePrint(actor, depth + 1, node.getNode(name));
        }
    }
    
    private static void printFormatted(Actor<SnowFrame<LauncherApp>> actor, String start, String content, String end, char fill, int offset, int writableArea) {
        StringBuilder builder = new StringBuilder(start);
        for (int i = 0; i < offset; i++) {
            builder.append(fill);
        }
        builder.append(content);
        int fillLength = writableArea - offset - content.length();
        for (int i = 0; i < fillLength; i++) {
            builder.append(fill);
        }
        actor.sendMessage(builder.append(end).toString());
    }
    
    private static void printFilled(Actor<SnowFrame<LauncherApp>> actor, char character, int actorWidth) {
        StringBuilder filledLine = new StringBuilder();
        for (int i = 0; i < actorWidth; i++) {
            filledLine.append(character);
        }
        actor.sendMessage(filledLine.toString());
    }

    public static void doHelp(Actor<SnowFrame<LauncherApp>> actor, CommandManager commandManager) {
        int consoleWidth = 80;
        int writeableWidth = consoleWidth - 7;
        printFilled(actor, BOX_OUTLINE, consoleWidth);
        printFormatted(actor, BOX_START, "___           __   ____   _      ____   _____   _________   ___    _", BOX_END, BOX_FILL, 0, writeableWidth);
        printFormatted(actor, BOX_START, "\\  \\    _    /  / |  __| | |    |  __| |  _  | |  _   _  | |  __| | |", BOX_END, BOX_FILL, 0, writeableWidth);
        printFormatted(actor, BOX_START, "\\  \\  / \\  /  /  | |_   | |    | |    | | | | | | | | | | | |_   | |", BOX_END, BOX_FILL, 1, writeableWidth);
        printFormatted(actor, BOX_START, "\\  \\/   \\/  /   |  _|  | |    | |    | | | | | | |_| | | |  _|  |_|", BOX_END, BOX_FILL, 2, writeableWidth);
        printFormatted(actor, BOX_START, "\\    _    /    | |__  | |__  | |__  | |_| | | |     | | | |__   _", BOX_END, BOX_FILL, 3, writeableWidth);
        printFormatted(actor, BOX_START, "\\__/ \\__/     |____| |____| |____| |_____| |_|     |_| |____| |_|", BOX_END, BOX_FILL, 4, writeableWidth);
        printFormatted(actor, BOX_START, "", BOX_END, BOX_FILL, 0, writeableWidth);
        printFilled(actor, BOX_OUTLINE, consoleWidth);
        printFormatted(actor, BOX_START, "", BOX_END, BOX_FILL, 0, writeableWidth);
        printFormatted(actor, BOX_START, APP_NAME + " " + getVersion(APP_NAME.length() + 1, writeableWidth, 1), BOX_END, BOX_FILL, 0, writeableWidth);
        printFormatted(actor, BOX_START, "", BOX_END, BOX_FILL, 0, writeableWidth);
        printFormatted(actor, BOX_START, "", BOX_END, BOX_FILL, 0, writeableWidth);
        printFormatted(actor, BOX_START, "Type '/help' to see this again!", BOX_END, BOX_FILL, 0, writeableWidth);
        printFormatted(actor, BOX_START, "", BOX_END, BOX_FILL, 0, writeableWidth);
        printFilled(actor, BOX_OUTLINE, consoleWidth);
        printFormatted(actor, BOX_START, "", BOX_END, BOX_FILL, 0, writeableWidth);
        List<NodeCommand> commands = commandManager.getCommands();
        for (NodeCommand command : commands) {
            StringBuilder builder = new StringBuilder();
            builder.append("||  /").append(command.getName());
            int nameLen = 4 + command.getName().length();
            StringBuilder nameSpace = new StringBuilder();
            for (int l = 0; l < nameLen; l++) {
                nameSpace.append(' ');
            }
            int descMaxLength = writeableWidth - nameLen;
            String description = command.getDescription();
            int curLen = 0;
            if (!description.isBlank()) {
                builder.append(" - ");
                int descLength = description.length();
                if (descLength <= descMaxLength) {
                    builder.append(description);
                    curLen = descLength;
                } else {
                    String[] parts = description.split(" ");
                    String part;
                    int partLen = 0;
                    for (int i = 0; i < parts.length; i++) {
                        int add = i == 0 ? 0 : 1;
                        part = parts[i];
                        if ((partLen = part.length()) + curLen + add <= descMaxLength) {
                            curLen += partLen + add;
                            if (i != 0) {
                                builder.append(' ');
                            }
                            builder.append(part);
                            continue;
                        }
                        int possibleLen = descMaxLength - curLen - add;
                        if (partLen > descMaxLength) {
                            if (i != 0) {
                                builder.append(' ');
                            }
                            int tmp;
                            int tmpStart = 0;
                            while (partLen > descMaxLength) {
                                tmp = tmpStart + possibleLen - 1;
                                builder.append(part.substring(tmpStart, tmp)).append('-');
                                builder.append(" ||\n||  ").append(nameSpace);
                                partLen -= possibleLen - 1;
                                tmpStart = tmp;
                                possibleLen = descMaxLength;
                            }
                            builder.append(part.substring(tmpStart));
                            curLen = partLen;
                            continue;
                        }
                        // Just do a next line
                        for (int l = curLen; l < descMaxLength; l++) {
                            builder.append(' ');
                        }
                        builder.append(" ||\n||  ").append(nameSpace);
                        curLen = partLen;
                        builder.append(part);
                    }

                }
            } else {
                curLen -= 3;
            }
            for (int l = curLen; l < descMaxLength; l++) {
                builder.append(' ');
            }
            actor.sendMessage(builder.append(" ||").toString());
        }
        printFormatted(actor, BOX_START, "", BOX_END, BOX_FILL, 0, writeableWidth);
        printFilled(actor, BOX_OUTLINE, consoleWidth);
    }

    private static String getVersion(int textWidth, int writableArea, int wallOffset) {
        StringBuilder version = new StringBuilder(" v");
        version.append(Main.version().toString());
        int spaceLen = writableArea - textWidth - version.length() - wallOffset;
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < spaceLen; i++) {
            out.append(BOX_FILL);
        }
        return out.append(version).toString();
    }

}
