package math;

import java.util.Arrays;
import java.util.HashMap;

/**
 * 一些数学工具
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/19
 * @time 22:40
 * <p>
 * #### 数组相关
 * 从一组double数组中找到最大值的序号
 * 从一组double数组中找到最小值的序号
 * 把一组double数组按升序排序（返回一组序号）
 * #### 数组相关
 * 给定范围生成随机数
 * 给定范围生成一组随机数
 * 给定范围生成随机整数
 * 将目标数字从一个范围映射到另一个范围内的对应数字
 * <p>
 * 增加中……
 */
public final class ZMath {

    /*-------- 数组相关 --------*/

    /**
     * 从一组double数组中找到最大值的序号
     *
     * @param arr input double array
     * @return int
     */
    public static int getMaxIndex(double[] arr) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        int maxIndex = 0;//假设第一个元素为最大值 那么下标设为0
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[maxIndex] < arr[i + 1]) {
                maxIndex = i + 1;
            }
        }
        return maxIndex;
    }

    /**
     * 从一组double数组中找到最小值的序号
     *
     * @param arr input double array
     * @return int
     */
    public static int getMinIndex(double[] arr) {
        if (arr == null || arr.length == 0) {
            return -1;
        }
        int minIndex = 0;//假设第一个元素为最小值 那么下标设为0
        for (int i = 0; i < arr.length - 1; i++) {
            if (arr[minIndex] > arr[i + 1]) {
                minIndex = i + 1;
            }
        }
        return minIndex;
    }

    /**
     * 把一组double数组按升序排序（返回一组序号）
     *
     * @param arr array to be sorted
     * @return int[] - indices of input array
     */
    public static int[] getArraySortedIndices(double[] arr) {
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

    /**
     * 数组倒序
     *
     * @param array array to be reversed
     * @return java.lang.Object[]
     */
    @Deprecated
    public static Object[] reverseArray(Object[] array) {
        Object[] result = new Object[array.length];
        for (int i = 0; i < array.length; i++) {
            result[i] = array[array.length - 1 - i];
        }
        return result;
    }

    /*-------- 映射与随机相关 --------*/

    /**
     * 给定范围生成随机数
     *
     * @param min floor limit
     * @param max ceil limit
     * @return double
     */
    public static double random(double min, double max) {
        return Math.random() * (max - min) + min;
    }

    /**
     * 给定范围生成一组随机数
     *
     * @param length array length to generate
     * @param min    floor limit
     * @param max    ceil limit
     * @return double[]
     */
    public static double[] randomArray(int length, double min, double max) {
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
     * 给定范围生成随机整数
     *
     * @param min floor limit
     * @param max ceil limit
     * @return int
     */
    public static int randomInt(double min, double max) {
        return (int) (Math.random() * (max - min) + min);
    }

    /**
     * 将目标数字从一个范围映射到另一个范围内的对应数字
     *
     * @param num    number to be mapped
     * @param oldMin previous floor
     * @param oldMax previous cap
     * @param newMin new floor
     * @param newMax new cap
     * @return double
     */
    public static double mapToRegion(double num, double oldMin, double oldMax, double newMin, double newMax) {
        double k = (newMax - newMin) / (oldMax - oldMin);
        return k * (num - oldMin) + newMin;
    }
}
