using BfLauncher.Utils;
using System;
using System.Collections.Generic;
using System.Linq;
using System.Threading;

namespace BfLauncher.Threading
{
    public class ExecutorService
    {

        public readonly Queue<QueuedTask> queue = new Queue<QueuedTask>();
        private readonly List<Executor> executors = new List<Executor>();
        private readonly System.Random random = new System.Random(854353789);
        private bool active = true;
        public string Name { get; }

        public ExecutorService(string name, int count)
        {
            this.Name = name;
            for(int index = 0; index < count; index++)
            {
                executors.Add(new Executor(this, name, index));
            }
        }

        public void Submit(ExecutionTask task)
        {
            if (!active)
                return;
            queue.Enqueue(new QueuedTask(task, FreeId()));
        }

        private long FreeId()
        {
            long value;
            while(true)
            {
                value = random.NextLong(1, long.MaxValue);
                if (IsQueued(value))
                {
                    continue;
                }
                if(GetExecutingThread(value) != null)
                {
                    continue;
                }
                return value;
            }
        }

        public void Execute(ExecutionTask task)
        {
            if (!active)
                return;
            Submit(task);
        }

        public int Count()
        {
            if (!active)
                return 0;
            return executors.Count;
        }

        public int CountFree()
        {
            if (!active)
                return 0;
            return executors.FindAll(executor => executor.TaskId == 0).Count;
        }

        public bool IsQueued(long taskId)
        {
            if (!active)
                return false;
            return queue.Any(task => task.TaskId == taskId);
        }

        public Thread GetExecutingThread(long taskId)
        {
            if (!active)
                return null;
            Executor executor = executors.Find(current => current.TaskId == taskId);
            return executor == null ? null : executor.Thread;
        }

        public void Shutdown()
        {
            if (!active)
                return;
            queue.Clear();
            if (executors.Count != 0)
            {
                foreach (Executor executor in executors)
                    executor.Shutdown();
            }
        }

        public void EnsureShutdown()
        {
            if (active)
                Shutdown();
            if(executors.Count != 0)
            {
                foreach (Executor executor in executors)
                    executor.EnsureShutdown();
                executors.Clear();
            }
        }

    }

}
