using BfLauncher._Emulator.Resources;
using BfLauncher.Properties;
using BfLauncher.UI;
using System;
using System.ComponentModel;
using System.Drawing;
using System.IO;
using System.Reflection;
using System.Runtime.Serialization;
using System.Windows.Forms;

namespace BfLauncher
{
	public partial class Form1 : Form
	{

		private IContainer components;

		private ProgressBar progressBar;
		private Label labelMsg;

		private Timer timer;
		private Panel panel;

		private Panel settingPanel;
		private Panel settingContainerLeft;
		private Panel settingContainerRight;

		private PictureBox bfThumbnail;

		private Button btnStart;
		private Button btnSettings;
		private Button btnClose;

		private BfCheckBox stnCheckUpdate;
		private BfCheckBox stnAutoUpdate;
		private BfCheckBox stnExperimental;
		private Label stnCheckUpdateMsg;
		private Label stnAutoUpdateMsg;
		private Label stnExperimentalMsg;

		private void InitializeComponent()
		{
			this.components = new Container();
			this.timer = new Timer(components);

			this.panel = new Panel();
			this.bfThumbnail = new PictureBox();

			this.labelMsg = new Label();
			this.progressBar = new ProgressBar();

			this.btnStart = new Button();
			this.btnSettings = new Button();
			this.btnClose = new Button();

			this.settingPanel = new Panel();
			this.settingContainerLeft = new Panel();
			this.settingContainerRight = new Panel();

			this.stnCheckUpdate = new BfCheckBox();
			this.stnAutoUpdate = new BfCheckBox();
			this.stnExperimental = new BfCheckBox();
			this.stnCheckUpdateMsg = new Label();
			this.stnAutoUpdateMsg = new Label();
			this.stnExperimentalMsg = new Label();

			timer.Tick += new EventHandler(Timer_Tick);

			btnStart.Click += new EventHandler(btnStart_Click);
			btnSettings.Click += new EventHandler(btnSettings_Click);
			btnClose.Click += new EventHandler(btnClose_Click);

			FormClosing += new FormClosingEventHandler(ExitLauncher);
			Load += new EventHandler(Form_Load);

			settingContainerRight.SuspendLayout();
			settingContainerLeft.SuspendLayout();
			settingPanel.SuspendLayout();
			panel.SuspendLayout();
			SuspendLayout();

			ComponentResourceManager resources = new ComponentResourceManager(typeof(BfLauncher.Form1));
			SetupPanel(resources, panel);
			SetupSettingsPanel(resources, settingPanel, settingContainerLeft, settingContainerRight);
			SetupForm(resources);

			settingContainerRight.ResumeLayout(false);
			settingContainerLeft.ResumeLayout(false);
			settingPanel.ResumeLayout(false);
			panel.ResumeLayout(false);
			ResumeLayout(false);
		}

		private void SetupForm(ComponentResourceManager resources)
        {
			bfThumbnail.Image = EmuResources.thumbnail;
			bfThumbnail.Size = new Size(784, 471);
			bfThumbnail.Location = new Point(0, 0);

			Controls.Add(bfThumbnail);

			resources.ApplyResources(this, "$this");
			AutoScaleMode = AutoScaleMode.None;
			FormBorderStyle = FormBorderStyle.FixedDialog;
			MaximizeBox = false;
			MinimizeBox = false;
			Name = "Form";
			Text = "Brick-Force Aurora";
		}

		private void SetupSettingsPanel(ComponentResourceManager resources, Panel panel, Panel leftContainer, Panel rightContainer)
		{
			panel.Controls.Add(leftContainer);
			panel.Controls.Add(rightContainer);
			Controls.Add(panel);

			SetupLeftSettingsContainer(leftContainer);
			SetupRightSettingsContainer(rightContainer);

			panel.BackgroundImage = EmuResources.settingThumbnail;
			panel.BackgroundImageLayout = ImageLayout.None;
			panel.BackColor = Color.Transparent;
			panel.ForeColor = Color.Transparent;
			panel.Location = new Point(122, 0);
			panel.Size = new Size(540, 471);
			panel.Visible = false;
		}

		private void SetupLeftSettingsContainer(Panel container)
		{
			stnCheckUpdate.ApplyStyle();
			stnCheckUpdate.Name = "stnCheckUpdate";
			stnCheckUpdate.Enabled = true;
			stnCheckUpdate.CheckImage = EmuResources.check;
			stnCheckUpdate.BackColor = Color.FromArgb(1, 10, 18);
			stnCheckUpdate.ForeColor = Color.FromArgb(7, 69, 127);
			stnCheckUpdate.Checked = Storage.GetBoolOr("check-update", true);
			stnCheckUpdate.OnChecked += (state) => Storage.Update("check-update", state);
			container.Add(stnCheckUpdate);

			stnAutoUpdate.ApplyStyle();
			stnAutoUpdate.Name = "stnAutoUpdate";
			stnAutoUpdate.Enabled = true;
			stnAutoUpdate.CheckImage = EmuResources.check;
			stnAutoUpdate.BackColor = Color.FromArgb(1, 10, 18);
			stnAutoUpdate.ForeColor = Color.FromArgb(7, 69, 127);
			stnAutoUpdate.Checked = Storage.GetBoolOr("auto-update", true);
			stnAutoUpdate.OnChecked += (state) => Storage.Update("auto-update", state);
			container.Add(stnAutoUpdate);

			stnExperimental.ApplyStyle();
			stnExperimental.Name = "stnExperimental";
			stnExperimental.Enabled = true;
			stnExperimental.CheckImage = EmuResources.check;
			stnExperimental.BackColor = Color.FromArgb(1, 10, 18);
			stnExperimental.ForeColor = Color.FromArgb(7, 69, 127);
			stnExperimental.Checked = Storage.GetBoolOr("experimental", false);
			stnExperimental.OnChecked += (state) => Storage.Update("experimental", state);
			container.Add(stnExperimental);

			container.BackColor = Color.Transparent;
			container.Location = new Point(26, 80);
			container.Size = new Size(244, 360);
		}

		private void SetupRightSettingsContainer(Panel container)
		{
			stnCheckUpdateMsg.Text = "Check for Brick-Force updates";
			stnCheckUpdateMsg.Name = "stnCheckUpdateMsg";
			stnCheckUpdateMsg.Font = labelMsg.Font;
			stnCheckUpdateMsg.BackColor = Color.Transparent;
			stnCheckUpdateMsg.ForeColor = Color.White;
			stnCheckUpdateMsg.TextAlign = ContentAlignment.MiddleRight;
			stnCheckUpdateMsg.Size = new Size(244, labelMsg.Font.Height);
			container.Add(stnCheckUpdateMsg);

			stnAutoUpdateMsg.Text = "Automatically update Brick-Force";
			stnAutoUpdateMsg.Name = "stnAutoUpdateMsg";
			stnAutoUpdateMsg.Font = labelMsg.Font;
			stnAutoUpdateMsg.BackColor = Color.Transparent;
			stnAutoUpdateMsg.ForeColor = Color.White;
			stnAutoUpdateMsg.TextAlign = ContentAlignment.MiddleRight;
			stnAutoUpdateMsg.Size = new Size(244, labelMsg.Font.Height);
			container.Add(stnAutoUpdateMsg);

			stnExperimentalMsg.Text = "Use experimental branch";
			stnExperimentalMsg.Name = "stnExperimentalMsg";
			stnExperimentalMsg.Font = labelMsg.Font;
			stnExperimentalMsg.BackColor = Color.Transparent;
			stnExperimentalMsg.ForeColor = Color.White;
			stnExperimentalMsg.TextAlign = ContentAlignment.MiddleRight;
			stnExperimentalMsg.Size = new Size(244, labelMsg.Font.Height);
			container.Add(stnExperimentalMsg);

			container.BackColor = Color.Transparent;
			container.Location = new Point(270, 80);
			container.Size = new Size(244, 360);
		}

		private void SetupPanel(ComponentResourceManager resources, Panel panel)
		{
			Controls.Add(panel);

			resources.ApplyResources(progressBar, "patchProgressBar"); // Use old name because of resources
			progressBar.BackColor = Color.Gold;
			progressBar.ForeColor = Color.Gold;
			progressBar.Name = "progressBar";
			panel.Controls.Add(progressBar);

			resources.ApplyResources(labelMsg, "labelMsg");
			labelMsg.BackColor = Color.Transparent;
			labelMsg.ForeColor = Color.White;
			labelMsg.Name = "labelmsg";
			panel.Controls.Add(labelMsg);

			resources.ApplyResources(btnStart, "action"); // Use old name because of resources
			btnStart.BackgroundImage = EmuResources.start;
			btnStart.Name = "btnStart";
			btnStart.Enabled = true;
			btnStart.UseVisualStyleBackColor = true;
			btnStart.FlatAppearance.BorderSize = 0;
			panel.Controls.Add(btnStart);

			resources.ApplyResources(btnSettings, "btnSettings");
			btnSettings.BackgroundImage = EmuResources.settings;
			btnSettings.Name = "btnSettings";
			btnSettings.Enabled = true;
			btnSettings.UseVisualStyleBackColor = true;
			btnSettings.FlatAppearance.BorderSize = 0;
			panel.Controls.Add(btnSettings);

			resources.ApplyResources(btnClose, "BtnClose"); // Use old name because of resources
			btnClose.BackgroundImage = EmuResources.quit;
			btnClose.Name = "btnClose";
			btnClose.Enabled = true;
			btnClose.UseVisualStyleBackColor = true;
			btnClose.FlatAppearance.BorderSize = 0;
			panel.Controls.Add(btnClose);

			resources.ApplyResources(panel, "panel1"); // Use old name because of resources
			panel.BackgroundImage = EmuResources.background;
            panel.Name = "panel";
		}

		protected override void Dispose(bool disposing)
		{
			bool flag = disposing && this.components != null;
			if (flag)
			{
				this.components.Dispose();
			}
			base.Dispose(disposing);
		}
	}
}
