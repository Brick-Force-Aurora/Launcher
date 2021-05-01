using System;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;
using static System.Windows.Forms.Control;

namespace BfLauncher.UI
{
    public static class UIExtensions
    {
        public static void ApplyBackImage(this ButtonBase button, Image image)
        {
            button.Size = new Size(image.Width, image.Height);
            button.BackgroundImage = image;
        }
        public static void ApplyImage(this ButtonBase button, Image image)
        {
            button.Size = new Size(image.Width, image.Height);
            button.Image = image;
        }

        public static void ApplyStyle(this ButtonBase button)
        {
            button.BackColor = Color.Transparent;
            button.FlatStyle = FlatStyle.Flat;
            button.UseVisualStyleBackColor = true;
            FlatButtonAppearance appearance = button.FlatAppearance;
            appearance.BorderSize = 0;
            appearance.BorderColor = Color.Empty;
            appearance.CheckedBackColor = Color.Empty;
            appearance.MouseDownBackColor = Color.Empty;
            appearance.MouseOverBackColor = Color.Empty;
        }

        public static void Add(this Panel panel, Control control)
        {
            Control last = panel.Controls.Last();
            panel.Controls.Add(control);
            if (last != null)
            {
                control.Location = new Point(last.Location.X, last.Location.Y + last.Size.Height + 12);
            }
        }

        public static Control Last(this ControlCollection collection)
        {
            return collection.Count == 0 ? null : collection[collection.Count - 1];
        }

    }
}
