using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;
using System.Windows.Forms;

namespace BfLauncher.IO
{ 
    public class Logger
    {

        private StreamWriter writer;
        private bool opened = false;

        public bool Open {
            get {
                return opened;
            }
            set
            {
                if (!opened || value)
                {
                    return;
                }
                opened = value;
                Close0();
            }
        }

        public Logger(string path) : this(File.CreateText(path))
        {

        }

        public Logger(StreamWriter writer)
        {
            this.writer = writer;
            this.opened = true;
        }

        public Logger PushDate()
        {
            if (!opened)
            {
                return this;
            }
            DateTime now = DateTime.Now;
            writer.Write('[');
            writer.Write(now.Day);
            writer.Write('.');
            writer.Write(now.Month);
            writer.Write('/');
            writer.Write(now.Hour);
            writer.Write(':');
            writer.Write(now.Minute);
            writer.Write(':');
            writer.Write(now.Second);
            writer.Write('-');
            writer.Write(now.Millisecond);
            writer.Write("] ");
            return this;
        }

        public Logger Push(object value)
        {
            if(!opened)
            {
                return this;
            }
            writer.Write(value);
            return this;
        }

        public Logger Send()
        {
            if (!opened)
            {
                return this;
            }
            writer.WriteLine();
            writer.Flush();
            return this;
        }

        public Logger Log(object value)
        {
            if (!opened)
            {
                return this;
            }
            return PushDate().Push(value).Send();
        }

        public void Close()
        {
            Open = false;
        }

        private void Close0()
        {
            writer.Flush();
            writer.Close();
            writer = null;
        }

    }
}
