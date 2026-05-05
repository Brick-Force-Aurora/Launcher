package de.brickforceaurora.launcher.util;

public enum OSType {

    MAC,
    LINUX,
    WINDOWS,
    UNKNOWN;

    public static OSType detect() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return OSType.WINDOWS;
        }
        if (os.contains("mac")) {
            return OSType.MAC;
        }
        if (os.contains("nix") || os.contains("nux") || os.contains("aix")) {
            return OSType.LINUX;
        }
        return OSType.UNKNOWN;
    }

}
