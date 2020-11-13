package math;

import java.util.Arrays;
import java.util.HashMap;

/**
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/19
 * @time 22:40
 * @description some advanced math methods
 * 1.从一组double数组中找到最大值的序号
 * 2.从一组double数组中找到最小值的序号
 * 3.把一组double数组按升序排序（返回一组序号）
 * 4.将目标数字从一个范围映射到另一个范围内的对应数字
 */
public class ZMath {

    /*-------- array methods --------*/

    /**
     * @return int
     * @description get max index of a float array
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
     * @return int
     * @description get min index of a float array
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
     * @return int[]
     * @description get ascending sort indices of a double array
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

    /*-------- mapping methods --------*/

    /**
     * @return float
     * @description map a number from old region to a new region
     */
    public static double mapToRegion(double num, double oldMin, double oldMax, double newMin, double newMax) {
        double k = (newMax - newMin) / (oldMax - oldMin);
        return k * (num - oldMin) + newMin;
    }
}
