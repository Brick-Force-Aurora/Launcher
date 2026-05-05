package de.brickforceaurora.launcher;

import de.brickforceaurora.launcher.util.OSType;

public final class CondConstant {

    public static final String PROP_IS_LINUX_OS = "os_linux";
    public static final String PROP_IS_WIN_OS = "os_win";
    public static final String PROP_IS_MAC_OS = "os_mac";

    public static final OSType OS = OSType.detect();
    
    private CondConstant() {
        throw new UnsupportedOperationException();
    }

}
