package de.brickforceaurora.launcher.helper;

import java.util.stream.Collectors;

import de.brickforceaurora.launcher.LauncherApp;
import me.lauriichan.laylib.logger.util.StringUtil;

public final class WindowsHelper {

    public static record ProgramResult(boolean success, String result, String error) {}

    private WindowsHelper() {
        throw new UnsupportedOperationException();
    }

    public static boolean isAuthorized(String name, String filePath) {
        String checkAuthorizationCommand = StringUtil.format("netsh advfirewall firewall show rule name=\"{0}\" verbose", new Object[] {
            name
        });
        try {
            String windowsFilePath = filePath.replace('/', '\\');
            Process process = Runtime.getRuntime().exec(new String[] {
                "cmd.exe",
                "/c",
                checkAuthorizationCommand
            });
            process.waitFor();
            String result = process.inputReader().lines().collect(Collectors.joining("\n"));
            String error = process.errorReader().lines().collect(Collectors.joining("\n"));
            if (!error.isBlank()) {
                return false;
            }
            boolean enabled = false, action = false, path = false;
            String[] parts = result.split("\n");
            for (String part : parts) {
                if (part.contains("-----")) {
                    if (enabled && action && path) {
                        return true;
                    }
                    enabled = false;
                    action = false;
                    path = false;
                    continue;
                }
                if (part.isBlank() || !part.contains(":")) {
                    continue;
                }
                String[] entry = part.split(":", 2);
                String key = entry[0].trim();
                switch (key) {
                case "Action": {
                    action = entry[1].trim().equals("Allow");
                    continue;
                }
                case "Program": {
                    path = entry[1].trim().equals(windowsFilePath);
                    continue;
                }
                case "Enabled": {
                    enabled = entry[1].trim().equals("Yes");
                    continue;
                }
                }
            }
            return enabled && action && path;
        } catch (Throwable thr) {
            LauncherApp.logger().warning("Failed to authorize firewall of '{0}' at '{1}'", thr, name, filePath);
            return false;
        }
    }

    public static ProgramResult authorizeProgram(String name, String filePath) {
        String authorizeCommand = StringUtil
            .format("netsh advfirewall firewall add rule name=\\\"{0}\\\" dir=in action=allow program=\\\"{1}\\\" enable=yes", new Object[] {
                name,
                filePath
            });
        try {
            Process process = Runtime.getRuntime().exec(new String[] {
                "powershell.exe",
                "-Command",
                "Start-Process -FilePath $Env:ComSpec -Verb runAs -Wait -PassThru -ArgumentList '/c','\"%s\"'".formatted(authorizeCommand)
            });
            process.waitFor();
            String result = process.inputReader().lines().collect(Collectors.joining("\n"));
            String error = process.errorReader().lines().collect(Collectors.joining("\n"));
            return new ProgramResult(true, result, error);
        } catch (Throwable thr) {
            LauncherApp.logger().warning("Failed to authorize firewall of '{0}' at '{1}'", thr, name, filePath);
            return new ProgramResult(false, "", "");
        }
    }

    public static ProgramResult applyRegistryLanguageFix() {
        try {
            Process process = Runtime.getRuntime().exec(new String[] {
                "cmd.exe",
                "/c",
                "reg add \"HKEY_CURRENT_USER\\SOFTWARE\\EXE Games\\BrickForce\" /v BfVoice_h2155129175 /t REG_DWORD /d 00000001 /f"
            });
            process.waitFor();
            String result = process.inputReader().lines().collect(Collectors.joining());
            String error = process.errorReader().lines().collect(Collectors.joining());
            return new ProgramResult(true, result, error);
        } catch (Throwable thr) {
            LauncherApp.logger().warning("Failed to apply registry language fix", thr);
            return new ProgramResult(false, "", "");
        }
    }

}
