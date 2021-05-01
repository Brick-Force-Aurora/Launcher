using BfLauncher.IO;
using ICSharpCode.SharpZipLib.Zip;
using Newtonsoft.Json;
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Net;
using System.Text;
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
            string[] parts = repository.Split('/');
            string tagUrl = String.Format(Constants.GITHUB_TAGS, parts[0], parts[1]);
            Version githubVersion = null;
            List<Version> updates = new List<Version>();
            try
            {
                int currentPage = 0;
                while (true)
                {
                    HttpWebRequest request = (HttpWebRequest)WebRequest.Create(tagUrl + currentPage);
                    request.ContentType = "application/x-www-form-urlencoded";
                    request.Method = "GET";
                    request.Accept = "application/vnd.github.v3+json";
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
                        if(!(var0 is Dictionary<string, object>))
                        {
                            continue;
                        }
                        Dictionary<string, object> var1 = (Dictionary<string, object>)var0;
                        if(!var1.ContainsKey("name"))
                        {
                            continue;
                        }
                        try {
                            Version version = new Version(((string)var1["name"]).Substring(1));
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

            DialogResult result = MessageBox.Show(form, "Do you want to update your Brick-Force to version " + latest.ToString() + "?", "Brick-Force Aurora", MessageBoxButtons.YesNo);
            if(result != DialogResult.OK)
            {
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
                string gitVersion = github.ToString();
                string info = gitVersion + " (" + (index++) + "/" + count + ")";
                form.AniText = "Downloading Brick-Force Version " + info;

                string path = form.Folder + "\\UpdateCache";
                Directory.CreateDirectory(path).Create();
                string url = GetAssetUrl(form.Storage.GetStringOr("github-repository", Constants.DEFAULT_REPO), gitVersion);
                if (url == null)
                {
                    form.Updated = true;
                    return;
                }
                FileStream output = File.Create(path + "\\patch.zip");
                WebResponse response = WebRequest.Create(url).GetResponse();
                Stream input = response.GetResponseStream();
                int length = (int)response.ContentLength;
                int value = 0;
                int next = length / 1000;
                for(int bytes = 0; bytes < length; bytes++)
                {
                    value = input.ReadByte();
                    output.WriteByte((byte) value);
                    bytes++;
                    if(bytes % next == 0)
                    {
                        form.Progress = bytes / next;
                    }
                }
                form.Progress = 1000;
                form.AniText = "Applying Brick-Force Patch for Version " + info;
                ApplyPatch(form, path);
                form.Progress = 1005;
                Directory.Delete(path, true);
            }
            form.Updated = true;
        }

        public static void ApplyPatch(Form1 form, string path)
        {
            FastZip zip = new FastZip();
            string target = path + "\\patch";
            zip.ExtractZip(path + "\\patch.zip", target, "*");
            StreamReader reader = File.OpenText(target + "\\info");
            string line;
            Dictionary<string, string> paths = new Dictionary<string, string>();
            List<string> delete = new List<string>();
            while((line = reader.ReadLine()) != null)
            {
                string[] info = line.ReadInfo();
                if(info.Length == 0)
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
            foreach (FileInfo info in Directory.CreateDirectory(target).GetFiles())
            {
                if(!paths.ContainsKey(info.FullName))
                {
                    continue;
                }
                string targetPath = paths[info.FullName];
                if(!(targetPath.EndsWith("/") || targetPath.EndsWith("\\")))
                {
                    targetPath += "\\";
                }
                info.CopyTo(targetPath + info.FullName, true);
            }
            foreach(string delPath in delete)
            {
                if(!File.Exists(delPath))
                {
                    continue;
                }
                File.Delete(delPath);
            }
        }

        public static string GetAssetUrl(string repository, string version)
        {
            string[] parts = repository.Split('/');
            HttpWebRequest request = (HttpWebRequest)WebRequest.Create(String.Format(Constants.GITHUB_RELEASE, parts[0], parts[1], 'v' + version));
            request.ContentType = "application/json";
            request.Method = "GET";
            request.Accept = "application/vnd.github.v3+json";
            HttpWebResponse response = (HttpWebResponse)request.GetResponse();
            if(response.StatusCode != HttpStatusCode.OK)
            {
                return null;
            }
            Dictionary<string, object> data = new Dictionary<string, object>();
            JsonSerializer.CreateDefault().Populate(new JsonTextReader(new StreamReader(response.GetResponseStream())), data);
            if(!data.ContainsKey("assets"))
            {
                return null;
            }
            object var0 = data["assets"];
            if(!(var0 is List<object>))
            {
                return null;
            }
            List<object> list = (List<object>)var0;
            foreach (object var1 in list)
            {
                if (!(var1 is Dictionary<string, object>))
                {
                    continue;
                }
                Dictionary<string, object> var2 = (Dictionary<string, object>)var1;
                if (!var2.ContainsKey("name") || !((string)var2["name"]).EndsWith(".zip") || !var2.ContainsKey("browser_download_url"))
                {
                    continue;
                }
                return (string)var2["browser_download_url"];
            }
            return null;
        }

    }
}
