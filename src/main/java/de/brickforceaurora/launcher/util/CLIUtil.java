package de.brickforceaurora.launcher.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public final class CLIUtil {
    
    private CLIUtil() {
        throw new UnsupportedOperationException();
    }

    public static String[] toArguments(String line) {
        ObjectArrayList<String> arguments = new ObjectArrayList<>();
        while (line.startsWith("/")) {
            line = line.substring(1);
        }
        StringReader reader = new StringReader(line);
        int prevIdx = 0, idx = 0;
        StringBuilder buffer = new StringBuilder();
        while (reader.hasNext()) {
            prevIdx = idx;
            if (Character.isWhitespace(reader.peek())) {
                reader.skipWhitespace();
                if (!buffer.isEmpty()) {
                    arguments.add(buffer.toString());
                    buffer = new StringBuilder();
                }
                if (!reader.hasNext()) {
                    break;
                }
            }
            String str = reader.read();
            idx = reader.getCursor();
            if (prevIdx == idx) {
                char ch = reader.peek();
                reader.skip();
                idx++;
                if (!Character.isWhitespace(ch)) {
                    buffer.append(ch);
                }
            }
            if (str.isBlank()) {
                continue;
            }
            buffer.append(str);
        }
        if (!buffer.isEmpty()) {
            arguments.add(buffer.toString());
        }
        return arguments.toArray(String[]::new);
    }

    public static int countSpace(final String path) {
        int count = 0;
        for (int i = 0; i < path.length(); i++) {
            if (path.charAt(i) == ' ') {
                count++;
            }
        }
        return count;
    }

}
