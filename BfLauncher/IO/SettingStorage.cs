using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

namespace BfLauncher.IO
{
    public class SettingStorage
    {

        public const string fileName = "launcher.txt";

        private Dictionary<string, object> values = new Dictionary<string, object>();

        public void Load()
        {
            if (!File.Exists(fileName))
            {
                return;
            }
            StreamReader stream = File.OpenText(fileName);

            StringBuilder builder = new StringBuilder();
            string key = null;
            while (!stream.EndOfStream)
            {
                string line = stream.ReadLine();
                if (!line.Contains("="))
                {
                    if (key != null && line.Length != 0)
                    {
                        builder.Append(line);
                    }
                    continue;
                }
                if (key != null)
                {
                    string value = builder.ToString();
                    builder.Clear();
                    values[key] = value.Parse();
                }
                string[] parts = line.Split('=');
                key = parts[0];
                for (int index = 1; index < parts.Length; index++)
                {
                    builder.Append(parts[index]);
                }
            }
            if(builder.Length != 0)
            {
                string value = builder.ToString();
                builder.Clear();
                values[key] = value.Parse();
            }

            stream.Close();
        }

        public void Save()
        {
            StreamWriter stream = File.CreateText(fileName);
            foreach (string key in values.Keys)
            {
                stream.Write(key);
                stream.Write("=");
                stream.WriteLine(values[key]);
            }
            stream.Flush();
            stream.Close();
        }

        public void Update(string path, bool value)
        {
            values[path] = value;
        }

        public void Update(string path, string value)
        {
            values[path] = value;
        }

        public object Get(string path)
        {
            try
            {
                return values[path];
            } catch(KeyNotFoundException)
            {
                return null;
            }
        }

        public bool GetBool(string path)
        {
            return GetBoolOr(path, false);
        }

        public string GetString(string path)
        {
            return GetStringOr(path, null);
        }

        public object GetOr(string path, object fallback)
        {
            object value = Get(path);
            return value == null ? fallback : value;
        }

        public bool GetBoolOr(string path, bool fallback)
        {
            object value = Get(path);
            return value == null || value.GetType() != typeof(bool) ? fallback : (bool)value;
        }

        public string GetStringOr(string path, string fallback)
        {
            object value = Get(path);
            return value == null || value.GetType() != typeof(string) ? fallback : (string)value;
        }

    }
}
