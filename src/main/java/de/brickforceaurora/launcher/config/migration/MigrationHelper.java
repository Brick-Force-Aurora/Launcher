package de.brickforceaurora.launcher.config.migration;

import me.lauriichan.snowframe.config.Configuration;

public final class MigrationHelper {

    private MigrationHelper() {
        throw new UnsupportedOperationException();
    }

    public static final void copy(Configuration config, String pathA, String pathB) {
        if (!config.contains(pathA)) {
            return;
        }
        internalCopy(config, pathA, pathB);
    }

    public static final void move(Configuration config, String pathA, String pathB) {
        if (!config.contains(pathA)) {
            return;
        }
        internalCopy(config, pathA, pathB);
        config.remove(pathA);
    }

    private static final void internalCopy(Configuration config, String pathA, String pathB) {
        if (!config.isConfiguration(pathA)) {
            config.set(pathB, config.get(pathA));
            return;
        }
        Configuration source = config.getConfiguration(pathA);
        Configuration target = config.getConfiguration(pathB, true);
        for (String key : source.keySet()) {
            copySection(source, target, key);
        }
    }

    private static final void copySection(Configuration source, Configuration target, String key) {
        if (!source.isConfiguration(key)) {
            target.set(key, source.get(key));
            return;
        }
        Configuration newSource = source.getConfiguration(key);
        Configuration newTarget = target.getConfiguration(key, true);
        for (String newKey : newSource.keySet()) {
            copySection(newSource, newTarget, newKey);
        }
    }

}
