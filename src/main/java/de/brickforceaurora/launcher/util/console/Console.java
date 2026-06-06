package de.brickforceaurora.launcher.util.console;

import java.io.IOException;
import java.io.PrintWriter;

import org.jline.reader.ParsedLine;
import org.jline.reader.impl.DefaultParser;
import org.jline.reader.impl.LineReaderImpl;
import org.jline.reader.impl.history.DefaultHistory;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.lauriichan.snowframe.util.logger.IDelegateLogger;

public final class Console implements IDelegateLogger, AutoCloseable {

    public static final Console INSTANCE = new Console();

    private static final String RESET = "\u001B[0m";

    private static final String GRAY = "\u001B[38;5;15m";
    private static final String YELLOW = "\u001B[38;5;226m";
    private static final String RED = "\u001B[38;5;197m";
    private static final String CYAN = "\u001B[38;5;87m";
    private static final String GREEN = "\u001B[38;5;84m";
    private static final String PURPLE = "\u001B[38;5;128m";

    private static final String CUSTOM = PURPLE + "%s" + RESET + "\n";
    private static final String INFO = GRAY + "%s" + RESET + "\n";
    private static final String WARNING = YELLOW + "%s" + RESET + "\n";
    private static final String ERROR = RED + "%s" + RESET + "\n";
    private static final String DEBUG = CYAN + "%s" + RESET + "\n";
    private static final String TRACE = GREEN + "%s" + RESET + "\n";

    private final Terminal terminal;

    private final PrintWriter writer;
    private final JLineReader reader;

    private Console() {
        if (INSTANCE != null) {
            throw new UnsupportedOperationException("Singleton");
        }
        try {
            this.terminal = TerminalBuilder.builder().system(true).build();
            this.writer = terminal.writer();
            this.reader = new JLineReader(terminal);
            reader.setParser(new DefaultParser());
            reader.setHistory(new DefaultHistory());
            reader.setKeyMap(JLineReader.MAIN);
            reader.variable(JLineReader.WORDCHARS, "*?_-.[]~&;!#$%^(){}<>");
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize terminal", e);
        }
    }
    
    @Override
    public void custom(String message) {
        writer.printf(CUSTOM, message);
        writer.flush();
    }

    @Override
    public void info(String message) {
        writer.printf(INFO, message);
        writer.flush();
    }

    @Override
    public void warning(String message) {
        writer.printf(WARNING, message);
        writer.flush();
    }

    @Override
    public void error(String message) {
        writer.printf(ERROR, message);
        writer.flush();
    }

    @Override
    public void track(String message) {
        writer.printf(TRACE, message);
        writer.flush();
    }

    @Override
    public void debug(String message) {
        writer.printf(DEBUG, message);
        writer.flush();
    }

    @Override
    public void close() throws Exception {
        terminal.close();
    }
    
    public String readLine() {
        String line = reader.readLine();
        ParsedLine parsed = reader.getParsedLine();
        if (parsed != null) {
            return parsed.line();
        }
        return line;
    }

    /*
     * Reader
     */

    private static class JLineReader extends LineReaderImpl {

        public JLineReader(Terminal terminal) throws IOException {
            super(terminal, terminal.getName(), new Object2ObjectOpenHashMap<>());
        }

    }

}
