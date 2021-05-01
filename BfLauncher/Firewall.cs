using System;
using System.Diagnostics;

namespace BfLauncher
{
	public static class Firewall
	{
		public static bool UnauthorizeProgram(string name, string programFullPath)
		{
			try
			{
				string format = Firewall.UFirewallCmd;
				bool flag = Environment.OSVersion.Version.Major >= Firewall.VistaMajorVersion;
				if (flag)
				{
					format = Firewall.UAdvanceFirewallCmd;
				}
				string str = string.Format(format, name, programFullPath);
				ProcessStartInfo processStartInfo = new ProcessStartInfo();
				processStartInfo.CreateNoWindow = true;
				processStartInfo.FileName = "cmd.exe";
				processStartInfo.UseShellExecute = false;
				processStartInfo.RedirectStandardInput = true;
				processStartInfo.RedirectStandardOutput = true;
				processStartInfo.RedirectStandardError = true;
				Process process = new Process();
				process.EnableRaisingEvents = false;
				process.StartInfo = processStartInfo;
				process.Start();
				process.StandardInput.Write(str + Environment.NewLine);
				process.StandardInput.Close();
				process.StandardOutput.ReadToEnd();
				process.StandardError.ReadToEnd();
				process.WaitForExit();
				process.Close();
			}
			catch
			{
				return false;
			}
			return true;
		}

		public static bool AuthorizeProgram(string name, string programFullPath, ref string result, ref string error)
		{
			result = "";
			error = "";
			try
			{
				string format = Firewall.FirewallCmd;
				bool flag = Environment.OSVersion.Version.Major >= Firewall.VistaMajorVersion;
				if (flag)
				{
					format = Firewall.AdvanceFirewallCmd;
				}
				string str = string.Format(format, name, programFullPath);
				ProcessStartInfo processStartInfo = new ProcessStartInfo();
				processStartInfo.CreateNoWindow = true;
				processStartInfo.FileName = "cmd.exe";
				processStartInfo.UseShellExecute = false;
				processStartInfo.RedirectStandardInput = true;
				processStartInfo.RedirectStandardOutput = true;
				processStartInfo.RedirectStandardError = true;
				Process process = new Process();
				process.EnableRaisingEvents = false;
				process.StartInfo = processStartInfo;
				process.Start();
				process.StandardInput.Write(str + Environment.NewLine);
				process.StandardInput.Close();
				result = process.StandardOutput.ReadToEnd();
				error = process.StandardError.ReadToEnd();
				process.WaitForExit();
				process.Close();
			}
			catch
			{
				return false;
			}
			return true;
		}

		private static readonly string FirewallCmd = "netsh firewall add allowedprogram \"{1}\" \"{0}\" ENABLE";

		private static readonly string AdvanceFirewallCmd = "netsh advfirewall firewall add rule name=\"{0}\" dir=in action=allow program=\"{1}\" enable=yes";

		private static readonly int VistaMajorVersion = 6;

		private static readonly string UFirewallCmd = "netsh firewall delete allowedprogram \"{0}\"";

		private static readonly string UAdvanceFirewallCmd = "netsh advfirewall firewall delete rule name=\"{0}\" program=\"{1}\"";
	}
}
