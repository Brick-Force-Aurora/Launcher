namespace BfLauncher.Threading
{
    public class QueuedTask
    {

        public long TaskId { get; }
        public ExecutionTask Task { get; }

        public QueuedTask(ExecutionTask task, long taskId)
        {
            this.TaskId = taskId;
            this.Task = task;
        }

        public void execute()
        {
            Task();
        }

    }
}
