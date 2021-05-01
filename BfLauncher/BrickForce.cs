using System;
using System.Diagnostics;

namespace BfLauncher
{
	public static class BrickForce
	{
		public static bool Execute()
		{
			try
			{
				ProcessStartInfo processStartInfo = new ProcessStartInfo();
				bool flag = Environment.OSVersion.Version.Major >= 6;
				if (flag)
				{
					processStartInfo.Verb = "runas";
				}
				processStartInfo.CreateNoWindow = false;
				processStartInfo.FileName = "BrickForce.exe";
				processStartInfo.UseShellExecute = false;
				processStartInfo.RedirectStandardInput = false;
				processStartInfo.RedirectStandardOutput = false;
				processStartInfo.RedirectStandardError = false;
				Process.Start(processStartInfo);
			}
			catch (Exception)
			{
				return false;
			}
			return true;
		}
	}
}
