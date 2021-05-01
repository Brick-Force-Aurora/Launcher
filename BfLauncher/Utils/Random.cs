using System;

namespace BfLauncher.Utils
{
    static class Random
    {
        public static long NextLong(this System.Random random)
        {
            byte[] buffer = new byte[8];
            random.NextBytes(buffer);
            return BitConverter.ToInt64(buffer, 0);
        }

        public static long NextLong(this System.Random random, long max)
        {
            return NextLong(random, 0, max);
        }

        public static long NextLong(this System.Random random, long min, long max)
        {
            EnsureMinLEQMax(ref min, ref max);
            long numbersInRange = unchecked(max - min + 1);
            if (numbersInRange < 0)
                throw new ArgumentException("Size of range between min and max must be less than or equal to Int64.MaxValue");

            long randomOffset = NextLong(random);
            if (IsModuloBiased(randomOffset, numbersInRange))
                return NextLong(random, min, max); // Try again
            else
                return min + PositiveModuloOrZero(randomOffset, numbersInRange);
        }

        static bool IsModuloBiased(long randomOffset, long numbersInRange)
        {
            long greatestCompleteRange = numbersInRange * (long.MaxValue / numbersInRange);
            return randomOffset > greatestCompleteRange;
        }

        static long PositiveModuloOrZero(long dividend, long divisor)
        {
            long mod;
            Math.DivRem(dividend, divisor, out mod);
            if (mod < 0)
                mod += divisor;
            return mod;
        }

        static void EnsureMinLEQMax(ref long min, ref long max)
        {
            if (min <= max)
                return;
            long temp = min;
            min = max;
            max = temp;
        }
    }
}
