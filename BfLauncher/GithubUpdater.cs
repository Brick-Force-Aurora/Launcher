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



        public static void UpdateBrickForce(Form1 form)
        {
            string repository = form.Storage.GetStringOr("github-repository", Constants.DEFAULT_REPO);
            Version currentVersion = new Version(form.Storage.GetStringOr("github-version", "1.0.0"));
            if(!repository.Contains("/")) {
                form.Updated = true;
                return;
            }
            ServicePointManager.Expect100Continue = true;
            ServicePointManager.SecurityProtocol = (SecurityProtocolType)3072;
            string[] parts = repository.Split('/');
            string tagUrl = String.Format(Constants.GITHUB_TAGS, parts[0], parts[1]);
            Version githubVersion = null;
            List<Version> updates = new List<Version>();
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
                        } catch(Exception)
                        {
                            continue;
                        }
                    }
                    if(data.Count != 40)
                    {
                        break;
                    }
                }
            } catch(Exception)
            {
                form.Updated = true;
                return;
            }
            if (githubVersion == null)
            {
                form.Updated = true;
                return;
            }
            TryPatchBrickForce(form, updates);
        }

        public static void TryPatchBrickForce(Form1 form, List<Version> github)
        {
            github.Sort();
            Version latest = github[github.Count - 1];
            if (form.Storage.GetBoolOr("auto-update", false))
            {
                RunPatchBrickForce(form, github);
                return;
            }
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
                    form.Updated = true;
                    return;
                }
            } catch(Exception)
            {
                form.Updated = true;
                return;
            }
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

                string path = form.Folder + "\\UpdateCache";
                Directory.CreateDirectory(path).Create();
                string url = GetAssetUrl(form.Storage.GetString("github-auth"), form.Storage.GetStringOr("github-repository", Constants.DEFAULT_REPO), gitVersion);
                if (url == null)
                {
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
                    ApplyPatch(form, path);
                    form.Progress = 1005;
                    Directory.Delete(path, true);
                    form.Storage.Update("github-version", gitVersion);
                    form.Storage.Save();
                } catch(Exception)
                {
                    form.Updated = true;
                    return;
                }
            }
            form.Updated = true;
        }

        public static void ApplyPatch(Form1 form, string path)
        {
            FastZip zip = new FastZip();
            string target = path + "\\patch";
            Directory.CreateDirectory(target);
            zip.ExtractZip(path + "\\patch.zip", target, "");
            StreamReader reader = File.OpenText(target + "\\info");
            string line;
            Dictionary<string, string> paths = new Dictionary<string, string>();
            List<string> delete = new List<string>();
            while((line = reader.ReadLine()) != null)
            {
                string[] info = line.ReadInfo();
                if (info.Length == 0)
                {
                    continue;
                }
                if(info[0].ToLower().Equals("delete"))
                {
                    delete.Add(info[1]);
                    continue;
                }
                paths[info[0]] = info[1];
            }
            reader.Close();
            PatchFiles(Directory.CreateDirectory(target), paths, form.Folder, "");
            foreach(string delPath in delete)
            {
                if (Directory.Exists(delPath))
                {
                    Directory.Delete(delPath, true);
                    continue;
                }
                if(!File.Exists(delPath))
                {
                    continue;
                }
                File.Delete(delPath);
            }
        }

        private static void PatchFiles(DirectoryInfo directory, Dictionary<string, string> paths, string mainPath, string current)
        {
            foreach (FileSystemInfo info in directory.GetFileSystemInfos())
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
                    targetPath += "\\";
                }
                else if (targetPath.EndsWith("/"))
                {
                    targetPath = "";
                }
                if (info is FileInfo)
                {
                    ((FileInfo)info).MoveTo(mainPath + "\\" + targetPath + info.Name);
                    continue;
                }
                DirectoryInfo drInfo = (DirectoryInfo)info;
                CopyDirectory(drInfo, Directory.CreateDirectory(mainPath + "\\" + targetPath));
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
                info.MoveTo(targetPath + info.Name);
            }
            foreach(DirectoryInfo info in input.GetDirectories())
            {
                DirectoryInfo redirect = Directory.CreateDirectory(targetPath + info.Name);
                CopyDirectory(info, redirect);
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
            } catch(Exception)
            {
                return null;
            }
        }

    }
}
