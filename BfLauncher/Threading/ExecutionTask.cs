using BfLauncher.Utils.Functions;

namespace BfLauncher.Threading
{

    public delegate void ExecutionTask();

    public static class ExecutionTools
    {
        public static ExecutionTask From<T>(Supplier<T> supplier, Consumer<T> consumer)
        {
            return () => consumer(supplier());
        }

        public static ExecutionTask From<T, V>(BiSupplier<T, V> supplier, BiConsumer<T, V> consumer)
        {
            return () =>
            {
                Supply<T, V> supply = supplier();
                consumer(supply.Value1, supply.Value2);
            };
        }

        public static ExecutionTask From<T>(Supplier<T> supplier)
        {
            return () => supplier();
        }

        public static ExecutionTask From<T, V>(BiSupplier<T, V> supplier)
        {
            return () => supplier();
        }

        public static ExecutionTask From<T>(Consumer<T> consumer)
        {
            return () => consumer(default(T));
        }

        public static ExecutionTask From<T>(Consumer<T> consumer, T consume)
        {
            return () => consumer(consume);
        }

        public static ExecutionTask From<T, V>(BiConsumer<T, V> consumer)
        {
            return () => consumer(default(T), default(V));
        }

        public static ExecutionTask From<T, V>(BiConsumer<T, V> consumer, T consume)
        {
            return () => consumer(consume, default(V));
        }

        public static ExecutionTask From<T, V>(BiConsumer<T, V> consumer, V consume)
        {
            return () => consumer(default(T), consume);
        }

        public static ExecutionTask From<T, V>(BiConsumer<T, V> consumer, T consume1, V consume2)
        {
            return () => consumer(consume1, consume2);
        }

        public static ExecutionTask From<T, V>(BiConsumer<T, V> consumer, Supply<T, V> supply)
        {
            return () => consumer(supply.Value1, supply.Value2);
        }

    }

}
