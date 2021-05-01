using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace BfLauncher.IO
{
    public static class Parser
    {

        public static object Parse(this string value)
        {
            if(value.IsBool())
            {
                return value.AsBool();
            }
            return value;
        }

        public static bool IsBool(this string value)
        {
            return value.ToLower().Equals("true") || value.ToLower().Equals("false") ? true : false;
        }

        public static bool AsBool(this string value)
        {
            return value.ToLower().Equals("true");
        }

    }
}
