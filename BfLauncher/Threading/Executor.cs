using System;
using System.Threading;

namespace BfLauncher.Threading
{
    class Executor
    {
        private readonly ExecutorService service;

        private QueuedTask task;
        private bool active = true;
        public Thread Thread { get; }

        public string Name
        {
            set
            {
                Thread.Name = value;
            }
            get
            {
                return Thread.Name;
            }
        }
        public long TaskId
        {
            get {
                if(task == null)
                    return 0;
                return task.TaskId;
            }
        }
        
        public Executor(ExecutorService service, string name, int index)
        {
            this.service = service;

            this.Thread = new Thread(Execute);
            this.Thread.Start();

            this.Name = name + '-' + index;
        }

        public void Shutdown()
        {
            active = false;
        }

        public void EnsureShutdown()
        {
            Shutdown();
            Thread.Interrupt();
        }

        private void Execute()
        {
            while(active)
            {
                if (service.queue.Count == 0)
                {
                    try
                    {
                        Thread.Sleep(50);
                    } catch(ThreadInterruptedException)
                    {

                    }
                    continue;
                }
                try
                {
                    task = service.queue.Dequeue();
                    if(task == null)
                    {
                        continue;
                    }
                    task.Task();
                } catch(Exception)
                {
                }
                task = null;
            }
        }

    }
}
