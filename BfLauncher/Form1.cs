using BfLauncher.IO;
using BfLauncher.Threading;
using System;
using System.Drawing;
using System.IO;
using System.Windows.Forms;

namespace BfLauncher
{
	public partial class Form1 : Form {

		public SettingStorage Storage { get; } = new SettingStorage();

		public Form1()
		{
			Storage.Load();
			this.InitializeComponent();
		}


		private ExecutorService executor = new ExecutorService("Worker", 2);
		private bool updateChecked = true;
		private int aniTick = 0;

		private void SetReadyToPlay()
		{
			this.progressBar.Value = 100;
			this.labelMsg.Text = "Click start to play Brick-Force";
			this.btnStart.Enabled = true;
		}

		private void Form_Load(object sender, EventArgs e)
		{
			updateChecked = !Storage.GetBoolOr("check-update", true);
			string path = Directory.GetCurrentDirectory();
			string programFullPath = Path.Combine(path, "BfLauncher.exe");
			string programFullPath2 = Path.Combine(path, "BrickForce.exe");
			Firewall.UnauthorizeProgram("BFLauncher", programFullPath);
			Firewall.UnauthorizeProgram("BrickForce", programFullPath2);
			string text = "";
			string text2 = "";
			Firewall.AuthorizeProgram("BFLauncher", programFullPath, ref text, ref text2);
			Firewall.AuthorizeProgram("BrickForce", programFullPath2, ref text, ref text2);
			this.timer.Enabled = true;
			executor.Execute(() =>
			{
				Updater.UpdateBrickForce(progressBar);
			});
		}

		private void Timer_Tick(object sender, EventArgs e)
		{
			if(updateChecked)
			{
				this.SetReadyToPlay();
				return;
			} 
			this.labelMsg.Text = "Checking for updates";
			switch (aniTick)
            {
				case 0:
				case 1:
				case 2:
				case 3:
					aniTick++;
					break;
				case 4:
				case 5:
				case 6:
				case 7:
					this.labelMsg.Text += ".";
					aniTick++;
					break;
				case 8:
				case 9:
				case 10:
				case 11:
					this.labelMsg.Text += "..";
					aniTick++;
					break;
				case 12:
				case 13:
				case 14:
					this.labelMsg.Text += "...";
					aniTick++;
					break;
				case 15:
					this.labelMsg.Text += "...";
					aniTick = 0;
					break;
			}
		}

		private void btnStart_Click(object sender, EventArgs e)
		{
            if (!updateChecked)
			{
				MessageBox.Show(this, "Please wait for the updater to finish!", "Brick-Force Aurora");
				return;
			}
			bool flag5 = !BrickForce.Execute();
			if (flag5)
			{
				MessageBox.Show(this, "Failed to launch Brick-Force. Please try again.", "Brick-Force Aurora");
			}
			base.Close();
		}

		private void btnSettings_Click(object sender, EventArgs e)
		{
			settingPanel.Visible = !settingPanel.Visible;
		}

		private void btnClose_Click(object sender, EventArgs e)
		{
			DialogResult dialogResult = MessageBox.Show("Are you sure you want to quit?", "Brick-Force Aurora", MessageBoxButtons.OKCancel);
			bool flag = dialogResult == DialogResult.OK;
			if (flag)
			{
				base.Close();
			}
		}

		private void ExitLauncher(object sender, EventArgs e) {
			executor.EnsureShutdown();
			Storage.Save();
		}

	}
}
