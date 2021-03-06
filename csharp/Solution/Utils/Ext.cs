﻿using System;
using System.Collections.Generic;
using System.Globalization;
using System.Linq;
using System.Text;
using System.Threading.Tasks;


namespace Utils
{
    public static class Ext
    {
        public static String CheckEquals<T>(String name, T[] lhs, T[] rhs)
        {
            if (lhs.Length != rhs.Length)
                return String.Format("Counts not equal: {0} and {1}", lhs.Length, rhs.Length);

            StringBuilder sb = new StringBuilder();

            for(int i = 0; i < lhs.Length; ++i)
            {
                if (lhs[i].Equals(rhs[i]) == false)
                {
                    sb.AppendFormat("{0}[{1}] : {2} != {3} ", name, i, lhs[i], rhs[i]).AppendLine();
                    //return String.Format("element {0} not equal {1}, {2}", i, lhs[i], rhs[i]);
                }
            }

            return sb.ToString();

        }

        public static String CheckEquals<T>(String name, T[][] lhs, T[][] rhs)
        {
            if (lhs.Length != rhs.Length)
                return String.Format("Counts not equal: {0} and {1}", lhs.Length, rhs.Length);

            StringBuilder sb = new StringBuilder();

            for (int i = 0; i < lhs.Length; ++i)
            {
                if (lhs[i].Length != rhs[i].Length)
                    return String.Format("Counts not equal: {0} and {1}.  sub {2}", lhs.Length, rhs.Length, i);

                for(int j=0; j < lhs[i].Length; ++j)
                {
                    if (lhs[i][j].Equals(rhs[i][j]) == false)
                    {
                        sb.AppendFormat("{0}[{1}][{2}] : {3} != {4} ", name, i, j, lhs[i][j], rhs[i][j]).AppendLine();
                    }
                }
                
            }

            return sb.ToString();

        }

        public static void initList<T>(out List<T> list, int size, T defaultValue)
        {
            list = new List<T>();
            for(int i = 0; i < size; ++i)
            {
                list.Add(defaultValue);
            }
        }

        public static void createArrayWithFunc<T>(out T[] array, int d1, Func<int, T> init)
        {
            array = new T[d1];
            for (int i = 0; i < d1; ++i)
            {
                array[i] = init(i);
            }
        }

        public static void createArray<T>(out T[] array, int d1, T defValue)
        {
            array = new T[d1];
            for (int i = 0; i < d1; ++i)
            {
                
                array[i] = defValue;
                
            }
        }
        public static void createArray<T>(out T[][] array, int d1, int d2, T defValue)
        {
            array = new T[d1][];
            for (int i = 0; i < d1; ++i)
            {
                array[i] = new T[d2];

                for (int j = 0; j < d2; ++j)
                {
                    array[i][j] = defValue;
                }
            }
        }

        public static void createArray<T>(out T[][][] array, int d1, int d2, int d3, T defValue)
        {
            array = new T[d1][][];
            for (int i = 0; i < d1; ++i)
            {
                createArray(out array[i], d2, d3, defValue);
            }
        }

        public static void resetArray<T>(T[][][] array, int d1, int d2, int d3, T defValue)
        {            
            for (int i = 0; i < d1; ++i)
            {
                for (int j = 0; j < d2; ++j)
                {
                    for(int z = 0; z < d3; ++z)
                    {
                        array[i][j][z] = defValue;
                    }

                }
            }
        }

        public static void createArray<T>(out T[][][][] array, int d1, int d2, int d3, int d4, T defValue)
        {
            array = new T[d1][][][];
            for (int i = 0; i < d1; ++i)
            {
                createArray(out array[i], d2, d3, d4, defValue);
            }
        }

        public static void createArrayWithNew<T>(out T[] array, int d1) where T : new()
        {
            array = new T[d1];
            for (int i = 0; i < d1; ++i)
            {
                array[i] = new T();
            }
        }
        public static void createArrayWithNew<T>(out T[][] array, int d1, int d2) where T : new()
        {
            array = new T[d1][];
            for (int i = 0; i < d1; ++i)
            {
                createArrayWithNew(out array[i], d2);
            }
        }

        public static void swap<T>(ref T a, ref T b)
        {
            T temp = a;
            a = b;
            b = temp;
        }

        public static string ToUsString(this double d, int places)
        {
            return d.ToString("0." + new String('#', places), new CultureInfo("en-US"));


            //To avoid rounding, so .49999999999999 shows up as .49999 and not .50
            /*string str = d.ToString("G17", new CultureInfo("en-US"));
            int pIdx = str.IndexOf('.');

            if (pIdx == -1)
                return str;

            return str.Substring(0, Math.Min(str.Length, pIdx + places+1));*/

        }

        public static string ToBinaryString(this int i, int len = 16)
        {
            return Convert.ToString(i, 2).PadLeft(len, '0').Substring(0, len);
        }
        public static string ToBinaryString(this long i, int len = 16)
        {
            return Convert.ToString(i, 2).PadLeft(len, '0').Substring(0, len);
        }

        public static long FromBinaryString(this string str)
        {
            long ans = 0;
            for (int i = 0; i < str.Length; ++i)
            {
                int bitPos = str.Length - 1 - i;
                if (str[i] == '1')
                {
                    ans += 1L << bitPos;
                }
            }
            return ans;
        }

        public static string ToCommaString<T>(this IEnumerable<T> list)
        {
            return string.Join(", ", list);
        }
        
        public static char ToChar(this int intAsChar, char startChar = 'a')
        {
        	return (char) ((int)startChar + intAsChar);
        }

        //Extension method to be able to do "{0}".Format directly
        public static string FormatThis(this string str, params object[] args)
        {
            return String.Format(str, args);
        }

        //Add extension to list to get last value
        public static T GetLastValue<T>(this IList<T> list)
        {
            return list[list.Count - 1];
        }

        public static void ForEach(this string str, Action<int, char> a)
        {
            for(int i = 0; i < str.Length; ++i)
            {
                a(i, str[i]);
            }
        }

        public static void pop_back<T>(this List<T> list)
        {
            list.RemoveAt(list.Count - 1);
        }

        public static Tuple<T, T> binarySearch<T>(this List<T> list, T target) where T : IComparable<T>
        {
            int lowIdx, hiIdx;
            return binarySearch(list, target, out lowIdx, out hiIdx);
        }

        public static int lowerBound<T>(this List<T> list, T target) where T : IComparable<T>
        {
            return lowerBound(list, 0, list.Count, target);
        }
        public static int lowerBound<T>(this List<T> list, int lowIdx, int hiIdx, T target) where T : IComparable<T>
        {

            //using binary search ; invariant lo < sum <= hi
            //lowIdx = 0;
            //hiIdx = list.Count - 1;
            Preconditions.checkState(lowIdx <= hiIdx);

            if (list[lowIdx].CompareTo(target) >= 0)
            {
                return lowIdx;
            }

            while (hiIdx - lowIdx > 1)
            {
                int midIdx = lowIdx + (hiIdx - lowIdx) / 2;

                if (list[midIdx].CompareTo(target) >= 0)
                {
                    hiIdx = midIdx;
                }
                else
                {
                    lowIdx = midIdx;
                }
            }

            return lowIdx + 1;
        }

        //binary search sorted list
        public static Tuple<T, T> binarySearch<T>(this List<T> list, T target, out int lowIdx, out int hiIdx) where T : IComparable<T>
        {

            //using binary search ; invariant lo <= sum < hi
            lowIdx = 0;
            hiIdx = list.Count - 1;

            while (hiIdx - lowIdx > 1)
            {
                int midIdx = lowIdx + (hiIdx - lowIdx) / 2;

                if (list[midIdx].CompareTo(target) > 0)
                {
                    hiIdx = midIdx;
                }
                else
                {
                    lowIdx = midIdx;
                }
            }

            return new Tuple<T, T>(list[lowIdx], list[hiIdx]);
        }

        //binary search sorted list
        public static bool binarySearchGT<T>(this List<T> list, T target, out int lowIdx, out int hiIdx) where T : IComparable<T>
        {


            //using binary search ; invariant lo < sum <= hi
            lowIdx = 0;
            hiIdx = list.Count - 1;

            if (target.CompareTo(list[lowIdx]) <= 0)
            {
                return false;
            }

            if (target.CompareTo(list[hiIdx]) > 0)
            {
                return false;
            }

            while (hiIdx - lowIdx > 1)
            {
                int midIdx = lowIdx + (hiIdx - lowIdx) / 2;

                if (list[midIdx].CompareTo(target) >= 0)
                {
                    hiIdx = midIdx;
                }
                else
                {
                    lowIdx = midIdx;
                }
            }

            return true;
        }

    }
}
