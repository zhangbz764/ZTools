package math;

import java.util.Arrays;
import java.util.HashMap;

/**
 * math tools
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/19
 * @time 22:40
 * <p>
 * #### array-related
 * create a series of ascending integer
 * find the index of maximum value in an array of double
 * find the index of minimum value in an array of double
 * sort a double by ascending order (return indices)
 * <p>
 * #### map & random
 * generate random number by given floor and ceiling
 * generate a series of random number by given floor and ceiling
 * generate random integer by given floor and ceiling
 * map a number from old region to a new region
 * <p>
 * #### permutation & combination
 * factorial
 * <p>
 */
public final class ZMath {

    /*-------- array-related --------*/

    /**
     * create a series of ascending integer
     *
     * @param start start index
     * @param num   number of integers to create
     * @return int[]
     */
    public static int[] createIntegerSeries(final int start, final int num) {
        int[] result = new int[num];
        for (int i = 0; i < num; i++) {
            result[i] = start + i;
        }
        return result;
    }

    /**
     * find the index of maximum value in an array of double
     *
     * @param arr input double array
     * @return int
     */
    public static int getMaxIndex(final double[] arr) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        int maxIndex = 0;
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[maxIndex] < arr[i + 1]) {
                maxIndex = i + 1;
            }
        }
        return maxIndex;
    }

    /**
     * find the index of minimum value in an array of double
     *
     * @param arr input double array
     * @return int
     */
    public static int getMinIndex(final double[] arr) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        int minIndex = 0;
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[minIndex] > arr[i + 1]) {
                minIndex = i + 1;
            }
        }
        return minIndex;
    }

    /**
     * sort a double by ascending order (return indices)
     *
     * @param arr array to be sorted
     * @return int[] - indices of input array
     */
    public static int[] getArraySortedIndices(final double[] arr) {
        int[] sortedIndices = new int[arr.length];

        // build relations between array value and index
        HashMap<Double, Integer> relation = new HashMap<>();
        for (int i = 0; i < arr.length; i++) {
            relation.put(arr[i], i);
        }

        // use Arrays.sort() to sort input array (ascending order)
        double[] sorted_arr = arr;
        Arrays.sort(sorted_arr);

        // find orginal index of each value in sorted array
        for (int j = 0; j < sorted_arr.length; j++) {
            sortedIndices[j] = relation.get(sorted_arr[j]);
        }
        return sortedIndices;
    }

    /*-------- map & random --------*/

    /**
     * generate random number by given floor and ceiling
     *
     * @param min floor limit
     * @param max ceil limit
     * @return double
     */
    public static double random(final double min, final double max) {
        return Math.random() * (max - min) + min;
    }

    /**
     * generate a series of random number by given floor and ceiling
     *
     * @param length array length to generate
     * @param min    floor limit
     * @param max    ceil limit
     * @return double[]
     */
    public static double[] randomArray(final int length, final double min, final double max) {
        if (length > 0) {
            double[] array = new double[length];
            for (int i = 0; i < length; i++) {
                array[i] = random(min, max);
            }
            return array;
        } else {
            throw new IllegalArgumentException("invalid input : array length must > 0");
        }
    }

    /**
     * generate random integer by given floor and ceiling
     *
     * @param min floor limit
     * @param max ceil limit
     * @return int
     */
    public static int randomInt(final double min, final double max) {
        return (int) (Math.random() * (max - min) + min);
    }

    /**
     * map a number from old region to a new region
     *
     * @param num    number to be mapped
     * @param oldMin previous floor
     * @param oldMax previous cap
     * @param newMin new floor
     * @param newMax new cap
     * @return double
     */
    public static double mapToRegion(final double num, final double oldMin, final double oldMax, final double newMin, final double newMax) {
        double k = (newMax - newMin) / (oldMax - oldMin);
        return k * (num - oldMin) + newMin;
    }

    /*-------- other --------*/

    /**
     * factorial
     *
     * @param num number to process
     * @return int
     */
    public static int factorial(final int num) {
        if (num <= 1) {
            return 1;
        } else {
            return num * factorial(num - 1);
        }
    }

}
