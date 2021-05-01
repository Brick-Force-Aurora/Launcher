using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Text.RegularExpressions;

namespace BfLauncher.Utils
{
    public static class StringExtensions
    {
        private static Regex numeric = new Regex("[0-9]+");

        public static bool IsNumeric(this string value)
        {
            return numeric.IsMatch(value);
        }

    }
}
