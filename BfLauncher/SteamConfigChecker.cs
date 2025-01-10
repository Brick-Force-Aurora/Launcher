using System;
using System.Collections.Generic;
using System.Diagnostics;
using System.IO;
using System.Linq;
using System.Security.Policy;
using System.Text;
using System.Threading.Tasks;
using BfLauncher.IO;
using Microsoft.Win32;

namespace BfLauncher
{
    internal class SteamConfigChecker
    {
        private static Logger Logger;
        private static string GetSteamInstallPath()
        {
            // Check the registry for Steam's install path
            string registryKey = @"SOFTWARE\WOW6432Node\Valve\Steam";
            using (RegistryKey key = Registry.LocalMachine.OpenSubKey(registryKey))
            {
                if (key != null)
                {
                    object installPath = key.GetValue("InstallPath");
                    if (installPath != null)
                    {
                        Logger.Log("Read Registry, Found Steam Install Path: " + installPath);
                        return installPath.ToString();
                    }
                }
            }

            // Alternative: Check Current User
            registryKey = @"Software\Valve\Steam";
            using (RegistryKey key = Registry.CurrentUser.OpenSubKey(registryKey))
            {
                if (key != null)
                {
                    object steamPath = key.GetValue("SteamPath");
                    if (steamPath != null)
                    {
                        Logger.Log("Read Registry, Found Steam Install Path: " + steamPath);
                        return steamPath.ToString();
                    }
                }
            }

            return null;
        }

        private static string GetLibraryFoldersPath(string steamInstallPath)
        {
            string libraryPath = Path.Combine(steamInstallPath, "steamapps", "libraryfolders.vdf");
            if (File.Exists(libraryPath))
            {
                Logger.Log("Found Steam libraryfolders.vdf: " + libraryPath);
                return libraryPath;
            }

            // Fallback to the config folder
            libraryPath = Path.Combine(steamInstallPath, "config", "libraryfolders.vdf");
            if (File.Exists(libraryPath))
            {
                Logger.Log("Found Steam libraryfolders.vdf: " + libraryPath);
                return libraryPath;
            }

            return null;
        }

        private static List<string> ParseLibraryFolders(string libraryFilePath)
        {
            List<string> libraryPaths = new List<string>();

            if (!File.Exists(libraryFilePath))
            {
                return libraryPaths;
            }

            string[] lines = File.ReadAllLines(libraryFilePath);
            foreach (string line in lines)
            {
                string tline = line.Trim();
                if (tline.StartsWith("\"path\""))
                {
                    int start = tline.IndexOf("\"", 7) + 1;
                    int end = tline.LastIndexOf("\"");
                    string path = tline.Substring(start, end - start).Replace("\\\\", "\\");
                    libraryPaths.Add(path);
                }
            }

            Logger.Log($"Found  {libraryPaths.Count} Steam library folders.");
            return libraryPaths;
        }

        private static bool IsGameInstalled(string appId, List<string> libraryPaths)
        {
            foreach (string libraryPath in libraryPaths)
            {
                string appsPath = Path.Combine(libraryPath, "steamapps");
                if (!Directory.Exists(appsPath)) continue;

                string appManifest = Path.Combine(appsPath, $"appmanifest_{appId}.acf");
                if (File.Exists(appManifest))
                {
                    Logger.Log($"Found appmanifest.acf file for app id: {appId}.");
                    return true; // Game is installed if the manifest file exists
                }
            }

            return false;
        }

        public static void CheckSteamConfig(Form1 form)
        {
            Logger = form.Logger;
            string appId = Constants.STEAM_APP_ID;

            // Step 1: Get Steam Installation Path
            string steamInstallPath = GetSteamInstallPath();
            if (string.IsNullOrEmpty(steamInstallPath))
            {
                Logger.Log("Steam installation not found.");
                form.SteamCheckFailed = true;
                return;
            }

            // Step 2: Locate Library Folders File
            string libraryFilePath = GetLibraryFoldersPath(steamInstallPath);
            if (string.IsNullOrEmpty(libraryFilePath))
            {
                Logger.Log("Library folders file not found.");
                form.SteamCheckFailed = true;
                return;
            }

            // Step 3: Parse Library Folders
            List<string> libraryPaths = ParseLibraryFolders(libraryFilePath);
            if (libraryPaths.Count == 0)
            {
                Logger.Log("No Steam libraries found.");
                form.SteamCheckFailed = true;
                return;
            }

            // Step 4: Check if Game is Installed
            bool isInstalled = IsGameInstalled(appId, libraryPaths);

            Logger.Log(isInstalled
                ? $"Game with AppID {appId} is installed."
                : $"Game with AppID {appId} is not installed.");

            form.SteamCheckFailed = !isInstalled;
            return;
        }

        internal static void Install(string app_id)
        {
            try
            {
                // Open the URL in the default browser
                Process.Start(new ProcessStartInfo
                {
                    FileName = $"steam://install/{app_id}/",
                    UseShellExecute = true // Required to open the URL in the default browser
                });
            }
            catch (Exception ex)
            {
                Logger.Log($"Failed to open URL: {ex.Message}");
            }
        }
    }
}
