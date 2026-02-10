package de.brickforceaurora.launcher.util;

import java.util.Iterator;
import java.util.function.Predicate;

public class StringReader implements Iterator<Character> {

    public static final char ESCAPE = '\\';
    public static final char DOUBLE_QUOTE = '"';
    public static final char SINGLE_QUOTE = '\'';
    public static final char HEX_INDICATOR = '#';

    public static boolean isUnquotedCharacter(final char character) {
        return character >= '0' && character <= '9' || character >= 'A' && character <= 'Z' || character >= 'a' && character <= 'z'
            || character == '_' || character == '-' || character == '+' || character == '.';
    }

    public static boolean isQuote(final char character) {
        return character == DOUBLE_QUOTE || character == SINGLE_QUOTE;
    }

    /*
     * 
     */

    private final String content;
    private final int length;
    private int cursor;

    public StringReader(final String content) {
        this.content = content;
        this.length = content.length();
    }

    /*
     * Getter
     */

    public String getContent() {
        return content;
    }

    public int getCursor() {
        return cursor;
    }

    public int getLength() {
        return length - cursor;
    }

    public int getTotalLength() {
        return length;
    }

    public String getRead() {
        return content.substring(0, cursor);
    }

    public String getContent(final int start, final int end) {
        return content.substring(start, end);
    }

    public String getRemaining() {
        return content.substring(cursor);
    }

    public String getRemaining(final int end) {
        return content.substring(cursor, end);
    }

    /*
     * Setter
     */

    public StringReader setCursor(final int cursor) {
        this.cursor = cursor;
        return this;
    }

    /*
     * State
     */

    public boolean hasNext(final int length) {
        return cursor + length <= this.length;
    }

    @Override
    public boolean hasNext() {
        return hasNext(1);
    }

    public char peek() {
        return content.charAt(cursor);
    }

    public char peek(final int offset) {
        return content.charAt(cursor + offset);
    }

    @Override
    public Character next() {
        return content.charAt(cursor++);
    }

    /*
     * Skip
     */

    public StringReader skip() {
        cursor++;
        return this;
    }

    public StringReader skipUntil(final Predicate<Character> predicate) {
        while (hasNext() && predicate.test(peek())) {
            skip();
        }
        return this;
    }

    public StringReader skipWhitespace() {
        return skipUntil(Character::isWhitespace);
    }

    /*
     * Reading
     */

    public String readUntil(final Predicate<Character> predicate) {
        final int start = cursor;
        while (hasNext() && predicate.test(peek())) {
            skip();
        }
        return content.substring(start, cursor);
    }

    public String readUntilUnescaped(final char terminator) {
        final StringBuilder builder = new StringBuilder();
        boolean escaped = false;
        while (hasNext()) {
            final char character = next();
            if (escaped) {
                if (character == terminator || character == ESCAPE) {
                    builder.append(character);
                    escaped = false;
                    continue;
                }
                setCursor(getCursor() - 1);
                throw new IllegalArgumentException("Invalid escape at " + getCursor());
            }
            if (character == ESCAPE) {
                escaped = true;
                continue;
            }
            if (character == terminator) {
                return builder.toString();
            } else {
                builder.append(character);
                continue;
            }
        }
        throw new IllegalArgumentException("Quoted String didn't stop at " + getCursor());
    }

    public String read() {
        if (!hasNext()) {
            throw new IllegalArgumentException("There is nothing to read!");
        }
        return isQuote(peek()) ? readQuoted() : readUnquoted();
    }

    public String readUnquoted() {
        return readUntil(StringReader::isUnquotedCharacter);
    }

    public String readQuoted() {
        if (!hasNext()) {
            throw new IllegalArgumentException("There is nothing to read!");
        }
        final char quote = peek();
        if (!isQuote(quote)) {
            throw new IllegalArgumentException("Expected quote at start!");
        }
        return skip().readUntilUnescaped(quote);
    }

}