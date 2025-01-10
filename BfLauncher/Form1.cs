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


		private ExecutorService executor = new ExecutorService("Worker", 1);

		private int aniTick = 0;
		public string AniText { get; set; }

		public bool Updated { get; set; }
        public bool CheckSteam { get; set; }
        public bool SteamCheckFailed { get; set; }
        public string Folder { get; private set; }

		public int Progress { get; set; }

		public DialogResult UpdateResult { get; set; }
		public string CheckUpdate { get; set; }
		public Logger Logger { get; private set; }

		private void SetReadyToPlay()
		{
			this.progressBar.Value = 1005;
			this.labelMsg.Text = "Click start to play Brick-Force";
		}

		private void Form_Load(object sender, EventArgs e)
		{
			Updated = !Storage.GetBoolOr("check-update", true);
            CheckSteam = Storage.GetBoolOr("check-steam", false);
			SteamCheckFailed = false;
            Folder = Directory.GetCurrentDirectory();
			Logger = new Logger(Folder + "\\Launcher.log");
			string programFullPath = Path.Combine(Folder, "BfLauncher.exe");
			string programFullPath2 = Path.Combine(Folder, "BrickForce.exe");
			Firewall.UnauthorizeProgram("BFLauncher", programFullPath);
			Firewall.UnauthorizeProgram("BrickForce", programFullPath2);
			string text = "";
			string text2 = "";
			Firewall.AuthorizeProgram("BFLauncher", programFullPath, ref text, ref text2);
			Firewall.AuthorizeProgram("BrickForce", programFullPath2, ref text, ref text2);
			this.timer.Enabled = true;
			if (!Updated)
			{
				AniText = "Checking for updates";
				executor.Execute(() =>
				{
					GithubUpdater.UpdateBrickForce(this);
				});
            } else
            {
				Logger.Log("Update check is disabled!");
            }

			if (CheckSteam)
			{
                AniText = "Checking for Spacewar installation";
				SteamConfigChecker.CheckSteamConfig(this);
            }
            {
                Logger.Log("Steam check is disabled!");
            }
        }

		private void Timer_Tick(object sender, EventArgs e)
		{
			if(Updated)
			{
				this.SetReadyToPlay();
				return;
			}
            if (CheckUpdate != null)
            {
				string tmp = CheckUpdate;
				CheckUpdate = null;
				UpdateResult = MessageBox.Show(this, "Do you want to update your Brick-Force to version " + tmp + "?", "Brick-Force Aurora", MessageBoxButtons.YesNo);
				return;
			}
            if (SteamCheckFailed)
            {
                SteamCheckFailed = false;
                DialogResult result = MessageBox.Show(this, "Could not find Spacewar installation. Do you want to install it?", "Brick-Force Aurora", MessageBoxButtons.YesNo);
                if (result == DialogResult.Yes)
                {
                    SteamConfigChecker.Install(Constants.STEAM_APP_ID);
                }
                return;
            }
            this.labelMsg.Text = AniText;
			if (progressBar.Value != Progress)
            {
				progressBar.Value = Progress;
            }
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
            if (!Updated)
			{
				Logger.Log("INFO: User tried to start game while updating");
				MessageBox.Show(this, "Please wait for the updater to finish!", "Brick-Force Aurora");
				return;
			}
			Logger.Log("Starting BrickForce...");
			bool flag5 = !BrickForce.Execute();
			if (flag5)
			{
				Logger.Log("Failed to start BrickForce!");
				MessageBox.Show(this, "Failed to launch Brick-Force. Please try again.", "Brick-Force Aurora");
				return;
			}
			Logger.Log("BrickForce started successfully!");
			base.Close();
		}

		private void btnSettings_Click(object sender, EventArgs e)
		{
			if(!Updated)
			{
				Logger.Log("INFO: User tried to open settings while updating");
				MessageBox.Show(this, "Please wait for the updater to finish!", "Brick-Force Aurora");
				return;
            }
			if(settingPanel.Visible)
			{
				Logger.Log("Saving settings!");
				Storage.Save();
			}
			settingPanel.Visible = !settingPanel.Visible;
		}

		private void btnClose_Click(object sender, EventArgs e)
		{
			DialogResult dialogResult = MessageBox.Show("Are you sure you want to quit?", "Brick-Force Aurora", MessageBoxButtons.OKCancel);
			if (dialogResult == DialogResult.OK)
			{
				base.Close();
			}
		}

		private void ExitLauncher(object sender, EventArgs e) {
			executor.EnsureShutdown();
			Logger.Log("Goodbye!");
			Logger.Close();
		}

	}
}
