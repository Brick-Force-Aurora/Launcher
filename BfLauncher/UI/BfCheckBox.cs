using BfLauncher.Utils.Functions;
using System;
using System.CodeDom;
using System.Collections.Generic;
using System.Drawing;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace BfLauncher.UI
{
    public class BfCheckBox : ButtonBase
    {

        public event Consumer<bool> OnChecked;

        public bool Checked { 
            get {
                return clicked;
            }
            set {
                clicked = value;
                base.Refresh();
            } 
        }

        private bool clicked = false;
        private bool click = false;

        public Image CheckImage { get; set; }

        public new Size Size
        {
            get
            {
                return base.Size;
            }
            set
            {
                Size output = value;
                if(output.Width != output.Height)
                {
                    output.Width = Math.Max(value.Width, value.Height);
                    output.Height = output.Width;
                }
                base.Size = output;
            }
        }

        public BfCheckBox()
        {
            Size = new Size(16, 16);
        }

        protected override void OnMouseDown(MouseEventArgs mevent)
        {
            if (mevent.Button != MouseButtons.Left)
            {
                return;
            }
            if(!click) {
                click = true;
                Checked = !clicked;
                OnChecked?.Invoke(clicked);
            }
        }

        protected override void OnMouseUp(MouseEventArgs mevent)
        {
            if(mevent.Button != MouseButtons.Left)
            {
                return;
            }
            click = false;
        }

        protected override void OnPaint(PaintEventArgs pevent)
        {
            pevent.Graphics.Clear(BackColor);

            Rectangle rect = pevent.ClipRectangle;
            pevent.Graphics.DrawRectangle(new Pen(ForeColor), new Rectangle(rect.Location, new Size(rect.Size.Width - 1, rect.Size.Height - 1)));

            if(Checked && CheckImage != null)
            {
                pevent.Graphics.DrawImage(CheckImage, rect);
            }
        }

    }
}
