namespace BfLauncher.Utils.Functions
{

    public delegate T Supplier<T> ();

    public delegate Supply<T, V> BiSupplier<T, V>();


    public class Supply<T, V>
    {
        public T Value1 { set; get; }
        public V Value2 { set;  get; }

    }

}
