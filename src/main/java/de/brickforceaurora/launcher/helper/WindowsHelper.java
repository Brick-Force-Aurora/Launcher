package de.brickforceaurora.launcher.helper;

import java.io.File;
import java.io.IOException;

import de.brickforceaurora.launcher.LauncherApp;
import de.brickforceaurora.launcher.util.ProcessUtil;
import de.brickforceaurora.launcher.util.ProcessUtil.ProgramResult;
import me.lauriichan.laylib.logger.util.StringUtil;

public final class WindowsHelper {

    private WindowsHelper() {
        throw new UnsupportedOperationException();
    }

    public static boolean isAuthorized(final String name, final String filePath) {
        final String checkAuthorizationCommand = StringUtil.format("netsh advfirewall firewall show rule name=\"{0}\" verbose",
            new Object[] {
                name
            });
        try {
            final String windowsFilePath = filePath.replace('/', '\\');
            ProgramResult executionResult = ProcessUtil.runAndRead(new String[] {
                "cmd.exe",
                "/c",
                checkAuthorizationCommand
            });
            if (executionResult.exception() != null) {
                return false;
            }
            String result = executionResult.result();
            boolean enabled = false, action = false, path = false;
            final String[] parts = result.split("\n");
            for (final String part : parts) {
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
                final String[] entry = part.split(":", 2);
                final String key = entry[0].trim();
                switch (key) {
                case "Action": {
                    action = "Allow".equals(entry[1].trim());
                    continue;
                }
                case "Program": {
                    path = entry[1].trim().equals(windowsFilePath);
                    continue;
                }
                case "Enabled": {
                    enabled = "Yes".equals(entry[1].trim());
                    continue;
                }
                }
            }
            return enabled && action && path;
        } catch (final Throwable thr) {
            LauncherApp.logger().warning("Failed to authorize firewall of '{0}' at '{1}'", thr, name, filePath);
            return false;
        }
    }

    public static void authorizeProgram(final String name, final String filePath) {
        final String authorizeCommand = StringUtil
            .format("netsh advfirewall firewall add rule name=\\\"{0}\\\" dir=in action=allow program=\\\"{1}\\\" enable=yes",
                new Object[] {
                    name,
                    filePath
                });
        ProcessUtil.runAndRead(new String[] {
            "powershell.exe",
            "-Command",
            "Start-Process -FilePath $Env:ComSpec -Verb runAs -Wait -PassThru -ArgumentList '/c','\"%s\"'".formatted(authorizeCommand)
        });
    }

    public static void startProgram(final boolean asAdmin, final File file) throws IOException {
        File directory = file.getAbsoluteFile().getParentFile();
        String program = file.getName();
        if (!asAdmin) {
            new ProcessBuilder(new String[] {
                "powershell.exe",
                "-Command",
                "Start-Process -FilePath %s".formatted(program)
            }).directory(directory).start();
            return;
        }
        new ProcessBuilder(new String[] {
            "powershell.exe",
            "-Command",
            "Start-Process -FilePath %s -Verb runAs".formatted(program)
        }).directory(directory).start();
    }

    public static void applyRegistryLanguageFix() {
        ProcessUtil.runAndRead(new String[] {
            "cmd.exe",
            "/c",
            "reg add \"HKEY_CURRENT_USER\\SOFTWARE\\EXE Games\\BrickForce\" /v BfVoice_h2155129175 /t REG_DWORD /d 1 /f"
        });
    }

    public static void applyWindowedFix() {
        ProcessUtil.runAndRead(new String[] {
            "cmd.exe",
            "/c",
            "reg add \"HKEY_CURRENT_USER\\SOFTWARE\\EXE Games\\BrickForce\" /v BfFullScreen_h3939460542  /t REG_DWORD /d 0 /f"
        });
        ProcessUtil.runAndRead(new String[] {
            "cmd.exe",
            "/c",
            "reg add \"HKEY_CURRENT_USER\\SOFTWARE\\EXE Games\\BrickForce\" /v BfScreenHeight_h1769511314  /t REG_DWORD /d 720 /f"
        });
        ProcessUtil.runAndRead(new String[] {
            "cmd.exe",
            "/c",
            "reg add \"HKEY_CURRENT_USER\\SOFTWARE\\EXE Games\\BrickForce\" /v BfScreenWidth_h596610443  /t REG_DWORD /d 1280 /f"
        });
        ProcessUtil.runAndRead(new String[] {
            "cmd.exe",
            "/c",
            "reg add \"HKEY_CURRENT_USER\\SOFTWARE\\EXE Games\\BrickForce\" /v \"Screenmanager Is Fullscreen mode_h3981298716\" /t REG_DWORD /d 0 /f"
        });
        ProcessUtil.runAndRead(new String[] {
            "cmd.exe",
            "/c",
            "reg add \"HKEY_CURRENT_USER\\SOFTWARE\\EXE Games\\BrickForce\" /v \"Screenmanager Resolution Height_h2627697771\" /t REG_DWORD /d 720 /f"
        });
        ProcessUtil.runAndRead(new String[] {
            "cmd.exe",
            "/c",
            "reg add \"HKEY_CURRENT_USER\\SOFTWARE\\EXE Games\\BrickForce\" /v \"Screenmanager Resolution Width_h182942802\" /t REG_DWORD /d 1280 /f"
        });
    }

}
