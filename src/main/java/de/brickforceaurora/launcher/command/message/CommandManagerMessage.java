package de.brickforceaurora.launcher.command.message;

import java.util.Arrays;
import java.util.stream.Collectors;

import me.lauriichan.laylib.localization.source.IMessageDefinition;

public enum CommandManagerMessage implements IMessageDefinition {

    CANCEL_UNAVAILABLE("You currently have no active command process to cancel!"),
    CANCEL_SUCCESS("Successfully cancelled the process of command '$command&7'!"),

    CREATE_NO$COMMAND("The command '$command' doesn't exist!"),
    CREATE_NO$ACTION("The command '$command' has no action to be executed!"),
    CREATE_SUCCESS("Successfully started command process for command '$command'!"),

    SKIP_UNSKIPPABLE("The argument '$argument' is required to execute the command '$command'!"),
    SKIP_SUCCESS("Successfully skipped the argument '$argument'!"),

    INPUT_SUGGESTION("Suggestion >> $input"),
    INPUT_USER("You >> $input"),
    INPUT_FAILED(new String[] {
        "Failed to parse input for argument of type $argument.type!",
        "$error"
    }),

    SUGGEST_HEADER("Did you mean any of these suggestions?"),
    SUGGEST_FORMAT("[$certainty]: $suggestion"),
    SUGGEST_HOVER("Click to enter this suggestion"),

    ARGUMENT_SPECIFY("Please enter the argument '$argument.name' of type $argument.type!"),
    ARGUMENT_CANCELABLE_MESSAGE("You can cancel this process using /cancel!"),
    ARGUMENT_CANCELABLE_HOVER("Click to use /cancel"),
    ARGUMENT_OPTIONAL_MESSAGE("This argument is optional and can be skipped using /skip!"),
    ARGUMENT_OPTIONAL_HOVER("Click to use /skip"),

    EXECUTION_FAILED(new String[] {
        "An error occured while executing command '$command'!",
        "Error: $error"
    }),

    NOT$PERMITTED("You are lacking the permission $permission to do this.");

    private final String id;
    private final String fallback;

    CommandManagerMessage() {
        this("");
    }

    CommandManagerMessage(final String[] fallback) {
        this(Arrays.stream(fallback).collect(Collectors.joining("\n")));
    }

    CommandManagerMessage(final String fallback) {
        this.id = "command.process." + name().replace('$', '-').toLowerCase().replace('_', '.');
        this.fallback = fallback;
    }

    @Override
    public String fallback() {
        return fallback;
    }

    @Override
    public String id() {
        return id;
    }

}