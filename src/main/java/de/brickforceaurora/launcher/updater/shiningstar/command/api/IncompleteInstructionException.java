package de.brickforceaurora.launcher.updater.shiningstar.command.api;

public class IncompleteInstructionException extends Exception {

    private static final long serialVersionUID = -1930358727410256505L;
    
    public IncompleteInstructionException(String message) {
        super(message);
    }
    
    public IncompleteInstructionException(String message, Throwable cause) {
        super(message, cause);
    }

}
