using System;
using System.Diagnostics;
using System.IO;
using System.Threading;
using System.Windows.Forms;

namespace BfLauncher
{
	internal static class Program
	{
		[STAThread]
		private static void Main()
		{
			bool flag2;
			Mutex mutex = new Mutex(true, "BfLauncher", out flag2);
			bool flag3 = !flag2;
			if (flag3)
			{
				MessageBox.Show("The Brick-Force launcher is already running.");
			}
			else
			{
				Application.EnableVisualStyles();
				Application.SetCompatibleTextRenderingDefault(false);
				Application.Run(new Form1());
				mutex.ReleaseMutex();
			}
		}
	}
}
