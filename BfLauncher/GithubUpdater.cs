using BfLauncher.IO;
using ICSharpCode.SharpZipLib.Zip;
using Newtonsoft.Json;
using Newtonsoft.Json.Linq;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
using System.Threading;
using System.Windows.Forms;

namespace BfLauncher
{
    public static class GithubUpdater
    {
        private static Logger Logger;

        public static void UpdateBrickForce(Form1 form)
        {
            Logger = form.Logger;
            string repository = form.Storage.GetStringOr("github-repository", Constants.DEFAULT_REPO);
            Logger.Log("Trying to fetch update from \"" + repository + "\"");
            Version currentVersion = new Version(form.Storage.GetStringOr("github-version", "1.0.0"));
            Logger.Log("Current Version is " + currentVersion.ToString());
            if (!repository.Contains("/")) {
                Logger.Log("Invalid repository, skipping update check!");
                form.Updated = true;
                return;
            }
            ServicePointManager.Expect100Continue = true;
            ServicePointManager.SecurityProtocol = (SecurityProtocolType)3072;
            string[] parts = repository.Split('/');
            string tagUrl = String.Format(Constants.GITHUB_TAGS, parts[0], parts[1]);
            Version githubVersion = null;
            List<Version> updates = new List<Version>();
            Logger.Log("Requesting latest versions from github...");
            try
            {
                int currentPage = 0;
                string auth = form.Storage.GetString("github-auth");
                while (true)
                {
                    HttpWebRequest request = (HttpWebRequest)WebRequest.Create(tagUrl + currentPage);
                    request.Method = "GET";
                    request.Accept = "application/vnd.github.v3+json";
                    request.UserAgent = "BfLauncher";
                    if (auth != null)
                    {
                        request.Headers.Add("Authorization", "Basic " + auth);
                    }
                    HttpWebResponse response = (HttpWebResponse)request.GetResponse();
                    if(response.StatusCode != HttpStatusCode.OK)
                    {
                        if(githubVersion != null)
                        {
                            break;
                        }
                        form.Updated = true;
                        request.Abort();
                        return;
                    }
                    List<object> data = new List<object>();
                    JsonSerializer.CreateDefault().Populate(new JsonTextReader(new StreamReader(response.GetResponseStream())), data);
                    request.Abort();
                    if (data.Count == 0)
                    {
                        if (githubVersion != null)
                        {
                            break;
                        }
                        form.Updated = true;
                        return;
                    }
                    foreach(object var0 in data)
                    {
                        if(!(var0 is JObject))
                        {
                            continue;
                        }
                        JObject var1 = (JObject)var0;
                        if(!var1.ContainsKey("name"))
                        {
                            continue;
                        }
                        try {
                            StringBuilder builder = new StringBuilder();
                            foreach (char var in ((string)var1["name"]).ToCharArray())
                            {
                                if(!(char.IsDigit(var) || var == '.'))
                                {
                                    continue;
                                }
                                builder.Append(var);
                            }
                            Version version = new Version(builder.ToString());
                            if (version > currentVersion)
                            {
                                updates.Add(version);
                                if ((githubVersion == null || version > githubVersion)) { 
                                    githubVersion = version;
                                }
                            }
                        } catch(Exception exp0)
                        {
                            Logger.Log("Something went wrong while fetching github tags!").Log(exp0);
                            continue;
                        }
                    }
                    if(data.Count != 40)
                    {
                        break;
                    }
                }
            } catch(Exception exp)
            {
                Logger.Log("Failed to fetch updates!").Log(exp);
                form.Updated = true;
                return;
            }
            if (githubVersion == null)
            {
                Logger.Log("Game seems to be up2date!");
                form.Updated = true;
                return;
            }
            updates.Sort();
            Version latest = updates[updates.Count - 1];
            Logger.Log("Found " + updates.Count + " updates!");
            Logger.Log("Newest version found was " + latest.ToString());
            TryPatchBrickForce(form, latest, updates);
        }

        public static void TryPatchBrickForce(Form1 form, Version latest, List<Version> github)
        {
            if (form.Storage.GetBoolOr("auto-update", false))
            {
                Logger.Log("Automatically installing update...");
                RunPatchBrickForce(form, github);
                return;
            }
            Logger.Log("Waiting for user response to update the game...");
            try
            {
                form.CheckUpdate = latest.ToString();
                DialogResult result = DialogResult.None;
                while (result == DialogResult.None)
                {
                    Thread.Sleep(100);
                    result = form.UpdateResult;
                }
                form.UpdateResult = DialogResult.None;
                if (result != DialogResult.Yes)
                {
                    Logger.Log("User aborted game update to " + latest.ToString());
                    form.Updated = true;
                    return;
                }
            } catch(Exception exp)
            {
                Logger.Log("Something went wrong while trying to get the response of the user, skipping update!").Log(exp);
                form.Updated = true;
                return;
            }
            Logger.Log("Installing update on request of user...");
            RunPatchBrickForce(form, github);
        }

        public static void RunPatchBrickForce(Form1 form, List<Version> github)
        {
            int index = 1;
            int count = github.Count;
            foreach (Version version in github)
            {
                form.Progress = 0;
                string gitVersion = version.ToString();
                string info = gitVersion + " (" + (index++) + "/" + count + ")";
                form.AniText = "Downloading Brick-Force Version " + info;
                Logger.Log(form.AniText);

                string path = form.Folder + "\\UpdateCache";
                Directory.CreateDirectory(path).Create();
                string url = GetAssetUrl(form.Storage.GetString("github-auth"), form.Storage.GetStringOr("github-repository", Constants.DEFAULT_REPO), gitVersion);
                if (url == null)
                {
                    Logger.Log("Unable to retrieve update as no asset package was found, skipping update!");
                    form.Updated = true;
                    return;
                }
                try
                {
                    FileStream output = File.Create(path + "\\patch.zip");
                    WebResponse response = WebRequest.Create(url).GetResponse();
                    Stream input = response.GetResponseStream();
                    int length = (int)response.ContentLength;
                    long next = length / 1000;
                    long point = next;
                    byte[] buffer = new byte[2048];
                    int current = 0;
                    int bytes = 0;
                    while (bytes != length)
                    {
                        current = input.Read(buffer, 0, buffer.Length);
                        output.Write(buffer, 0, current);
                        output.Flush();
                        bytes += current;
                        if (bytes > point)
                        {
                            form.Progress = (int)(bytes / next);
                            point += next;
                        }
                    }
                    output.Flush();
                    output.Close();
                    form.Progress = 1000;
                    form.AniText = "Applying Brick-Force Patch for Version " + info;
                    Logger.Log(form.AniText);
                    if (ApplyPatch(form, path))
                    {
                        form.Progress = 1005;
                        Directory.Delete(path, true);
                        form.Storage.Update("github-version", gitVersion);
                        form.Storage.Save();
                        Logger.Log("Successfully installed patch!");
                        continue;
                    }
                    form.Progress = 1005;
                    Directory.Delete(path, true);
                    form.Updated = true;
                    Logger.Log("Skipping update because of failed patch!");
                    return;
                } catch(Exception exp)
                {
                    Logger.Log("Failed to download update as no asset package was found, skipping update!").Log(exp);
                    form.Updated = true;
                    return;
                }
            }
            form.Updated = true;
            Logger.Log("Successfully updated to " + github[github.Count - 1].ToString());
        }

        public static bool ApplyPatch(Form1 form, string path)
        {
            try
            {
                FastZip zip = new FastZip();
                string target = path + "\\patch";
                Directory.CreateDirectory(target);
                zip.ExtractZip(path + "\\patch.zip", target, "");
                StreamReader reader = File.OpenText(target + "\\info");
                string line;
                Dictionary<string, string> paths = new Dictionary<string, string>();
                List<string> delete = new List<string>();
                while ((line = reader.ReadLine()) != null)
                {
                    string[] info = line.ReadInfo();
                    if (info.Length == 0)
                    {
                        continue;
                    }
                    if (info[0].ToLower().Equals("delete"))
                    {
                        delete.Add(info[1]);
                        continue;
                    }
                    paths[info[0]] = info[1];
                }
                reader.Close();
                PatchFiles(Directory.CreateDirectory(target), paths, form.Folder, "");
                foreach (string delPath in delete)
                {
                    if (Directory.Exists(delPath))
                    {
                        Directory.Delete(delPath, true);
                        continue;
                    }
                    if (!File.Exists(delPath))
                    {
                        continue;
                    }
                    File.Delete(delPath);
                }
            } catch(Exception exp)
            {
                Logger.Log("Failed to install patch!").Log("This has could cause some corruption!").Log(exp);
                return false;
            }
            return true;
        }

        private static void PatchFiles(DirectoryInfo directory, Dictionary<string, string> paths, string mainPath, string current)
        {
            foreach (FileSystemInfo info in directory.GetFileSystemInfos())
            {
                try
                {
                    string key = current + info.Name;
                    if (!paths.ContainsKey(key))
                    {
                        if (info is DirectoryInfo)
                        {
                            PatchFiles((DirectoryInfo)info, paths, mainPath, key + "/");
                        }
                        continue;
                    }
                    Console.WriteLine(key);
                    string targetPath = paths[key];
                    if (targetPath.Equals("/"))
                    {
                        targetPath = "";
                    }
                    else if (!targetPath.EndsWith("/"))
                    {
                        targetPath += "\\";
                    }
                    if (info is FileInfo)
                    {
                        string path = mainPath + "\\" + targetPath;
                        if (!Directory.Exists(path))
                        {
                            Directory.CreateDirectory(path);
                        }
                        path = path + info.Name;
                        if(File.Exists(path))
                        {
                            File.Delete(path);
                        }
                        ((FileInfo)info).MoveTo(path);
                        continue;
                    }
                    DirectoryInfo drInfo = (DirectoryInfo)info;
                    CopyDirectory(drInfo, Directory.CreateDirectory(mainPath + "\\" + targetPath));
                } catch(Exception exp)
                {
                    Logger.Log("Failed to install a patch-file!").Log("This has could cause some corruption!").Log(exp);
                }
            }
        }

        private static void CopyDirectory(DirectoryInfo input, DirectoryInfo output)
        {
            string targetPath = output.FullName;
            if (!(targetPath.EndsWith("/") || targetPath.EndsWith("\\")))
            {
                targetPath += "\\";
            }
            foreach (FileInfo info in input.GetFiles())
            {
                try
                {
                    string path = targetPath + info.Name;
                if (File.Exists(path))
                {
                    File.Delete(path);
                }
                info.MoveTo(path);
                } catch (Exception exp)
                {
                    Logger.Log("Failed to install a patch-file!").Log("This has could cause some corruption!").Log(exp);
                }
            }
            foreach(DirectoryInfo info in input.GetDirectories())
            {
                try
                {
                    DirectoryInfo redirect = Directory.CreateDirectory(targetPath + info.Name);
                    CopyDirectory(info, redirect);
                } catch (Exception exp)
                {
                    Logger.Log("Failed to install a patch-file!").Log("This has could cause some corruption!").Log(exp);
                }
            }
        }

        public static string GetAssetUrl(string auth, string repository, string version)
        {
            try
            {
                string[] parts = repository.Split('/');
                HttpWebRequest request = (HttpWebRequest)WebRequest.Create(String.Format(Constants.GITHUB_RELEASE, parts[0], parts[1], version));
                request.Method = "GET";
                request.Accept = "application/vnd.github.v3+json";
                request.UserAgent = "BfLauncher";
                if (auth != null)
                {
                    request.Headers.Add("Authorization", "Basic " + auth);
                }
                HttpWebResponse response = (HttpWebResponse)request.GetResponse();
                if (response.StatusCode != HttpStatusCode.OK)
                {
                    return null;
                }
                Dictionary<string, object> data = new Dictionary<string, object>();
                JsonSerializer.CreateDefault().Populate(new JsonTextReader(new StreamReader(response.GetResponseStream())), data);
                if (!data.ContainsKey("assets"))
                {
                    return null;
                }
                JArray list = (JArray) data["assets"];
                foreach (JObject var1 in list)
                {
                    if (!var1.ContainsKey("name") || !(((string)var1["name"]).EndsWith(".zip") || ((string)var1["name"]).EndsWith(".rar")) || !var1.ContainsKey("browser_download_url"))
                    {
                        continue;
                    }
                    return (string)var1["browser_download_url"];
                }
                return null;
            } catch(Exception exp)
            {
                Logger.Log("Something went wrong while fetching asset url!").Log(exp);
                return null;
            }
        }

    }
}
