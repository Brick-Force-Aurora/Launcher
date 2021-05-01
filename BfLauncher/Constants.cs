using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace BfLauncher
{
    public class Constants
    {

        public static string DEFAULT_REPO = "WeeaboosPls/Brick-Force-Aurora";

        public static string GITHUB_RELEASE = "https://api.github.com/repos/{0}/{1}/releases/tags/{2}";
        public static string GITHUB_TAGS = "https://api.github.com/repos/{0}/{1}/tags?per_page=40&page=";

    }
}
