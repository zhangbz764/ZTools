package math;

import org.apache.commons.lang3.ArrayUtils;

import java.util.*;

/**
 * math tools
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2020/10/19
 * @time 22:40
 * <p>
 * #### angle-related
 * sin, cos, tan of half-angle or double-angle
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
 * generate a series of random integer (start from 0)
 * map a number from old region to a new region
 * <p>
 * #### permutation & combination
 * factorial
 * <p>
 */
public final class ZMath {

    /*-------- angle-related --------*/

    /**
     * sine value of half-angle
     *
     * @param cos cosine value of original angle
     * @return double
     */
    public static double halfSin(double cos) {
        return Math.sqrt((1 - cos) * 0.5);
    }

    /**
     * cosine value of half-angle
     *
     * @param cos          cosine value of original angle
     * @param largerThanPI 0-PI or PI-TWOPI
     * @return double
     */
    public static double halfCos(double cos, boolean largerThanPI) {
        double halfCos = Math.sqrt((1 + cos) * 0.5);
        if (!largerThanPI) {
            return halfCos;
        } else {
            return -halfCos;
        }
    }

    /**
     * tangent value of half-angle
     *
     * @param cos          cosine value of original angle
     * @param largerThanPI 0-PI or PI-TWOPI
     * @return double
     */
    public static double halfTan(double cos, boolean largerThanPI) {
        double tan = Math.sqrt((1 - cos) / (1 + cos));
        if (!largerThanPI) {
            return tan;
        } else {
            return -tan;
        }
    }

    /**
     * sine value of double-angle
     *
     * @param sin sine value of original angle
     * @param cos cosine value of original angle
     * @return double
     */
    public static double doubleSin(double sin, double cos) {
        return 2 * sin * cos;
    }

    /**
     * cosine value of double-angle
     *
     * @param cos cosine value of original angle
     * @return double
     */
    public static double doubleCos(double cos) {
        return cos * cos * 2 - 1;
    }

    /**
     * tangent value of double-angle
     *
     * @param sin sine value of original angle
     * @param cos cosine value of original angle
     * @return double
     */
    public static double doubleTan(double sin, double cos) {
        return doubleSin(sin, cos) / doubleCos(cos);
    }

    /*-------- array-related --------*/

    /**
     * get the sum value of an array
     *
     * @param array double array
     * @return double
     */
    public static double sum(final double[] array) {
        double sum = 0;
        for (double v : array) {
            sum += v;
        }
        return sum;
    }

    /**
     * get the average value of an array
     *
     * @param array double array
     * @return double
     */
    public static double average(final double[] array) {
        double sum = sum(array);
        return sum / array.length;
    }

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
     * create a list of ascending integer
     *
     * @param start start index
     * @param num   number of integers to create
     * @return int[]
     */
    public static List<Integer> createIntegerList(final int start, final int num) {
        List<Integer> result = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            result.add(start + i);
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
     * find the indices of maximum values in an array of double
     *
     * @param arr   input double array
     * @param count number to select
     * @return int[]
     */
    public static int[] getMaxIndices(final double[] arr, final int count) {
        if (arr == null || arr.length == 0 || count > arr.length) {
            int[] indices = new int[count];
            Arrays.fill(indices, 0);
            return indices;
        }
        int[] indices = new int[count];
        double[] temp = Arrays.copyOf(arr, arr.length);
        Arrays.sort(temp);
        for (int i = 0; i < count; i++) {
            indices[i] = ArrayUtils.indexOf(arr, temp[temp.length - 1 - i]);
        }
        return indices;
    }

    /**
     * find the indices of maximum values in an array of double, return List
     *
     * @param arr   input double array
     * @param count number to select
     * @return java.util.List<java.lang.Integer>
     */
    public static List<Integer> getMaxIndicesList(final double[] arr, final int count) {
        List<Integer> indices = new ArrayList<>();

        // 初始化索引列表
        for (int i = 0; i < arr.length; i++) {
            indices.add(i);
        }

        // 使用比较器对索引列表进行排序，降序排序以获取最大值的索引
        indices.sort(Comparator.comparingDouble(index -> -arr[index]));

        // 取前n个索引

        return indices.subList(0, count);
    }

    /**
     * find the indices of minimum values in an array of double
     *
     * @param arr   input double array
     * @param count number to select
     * @return int[]
     */
    public static int[] getMinIndices(final double[] arr, final int count) {
        if (arr == null || arr.length == 0 || count > arr.length) {
            int[] indices = new int[count];
            Arrays.fill(indices, 0);
            return indices;
        }
        int[] indices = new int[count];
        double[] temp = Arrays.copyOf(arr, arr.length);
        Arrays.sort(temp);
        for (int i = 0; i < count; i++) {
            indices[i] = ArrayUtils.indexOf(arr, temp[i]);
        }
        return indices;
    }

    /**
     * find the indices of minimum values in an array of double, return List
     *
     * @param arr   input double array
     * @param count number to select
     * @return java.util.List<java.lang.Integer>
     */
    public static List<Integer> getMinIndicesList(final double[] arr, final int count) {
        List<Integer> indices = new ArrayList<>();

        // 初始化索引列表
        for (int i = 0; i < arr.length; i++) {
            indices.add(i);
        }

        // 使用比较器对索引列表进行排序
        indices.sort(Comparator.comparingDouble(index -> arr[index]));

        // 取前n个索引

        return indices.subList(0, count);
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
        double[] sorted_arr = Arrays.copyOf(arr, arr.length);
        Arrays.sort(sorted_arr);

        // find orginal index of each value in sorted array
        for (int j = 0; j < sorted_arr.length; j++) {
            sortedIndices[j] = relation.get(sorted_arr[j]);
        }
        return sortedIndices;
    }

    /**
     * 2d euclidean distance
     *
     * @param x1 x1
     * @param y1 y1
     * @param x2 x2
     * @param y2 y2
     * @return double
     */
    public static double distance2D(final double x1, final double y1, final double x2, final double y2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
    }

    /**
     * 3d euclidean distance
     *
     * @param x1 x1
     * @param y1 y1
     * @param z1 z1
     * @param x2 x2
     * @param y2 y2
     * @param z2 z2
     * @return double
     */
    public static double distance3D(final double x1, final double y1, final double z1, final double x2, final double y2, final double z2) {
        return Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2) + (z1 - z2) * (z1 - z2));
    }

    /**
     * euclidean distance in n-dimension
     *
     * @param d1 vector 1
     * @param d2 vector 2
     * @return double
     */
    public static double distanceEuclidean(final double[] d1, final double[] d2) {
        assert d1.length == d2.length : "input arrays must be the same dimension";
        double sum = 0;
        for (int i = 0; i < d1.length; i++) {
            sum += (d1[i] - d2[i]) * (d1[i] - d2[i]);
        }
        return Math.sqrt(sum);
    }

    /**
     * 2d manhattan distance
     *
     * @param x1 x1
     * @param y1 y1
     * @param x2 x2
     * @param y2 y2
     * @return double
     */
    public static double distanceManhattan2D(final double x1, final double y1, final double x2, final double y2) {
        return Math.abs(x1 - x2) + Math.abs(y1 - y2);
    }

    /**
     * calculate the centroid of a series of array data
     *
     * @param data original data
     * @return double[]
     */
    public static double[] dataCentroid(final double[][] data) {
        int I = data.length;
        int J = data[0].length;
        double[] centroid = new double[J];
        for (int j = 0; j < J; j++) {
            double sum = 0;
            for (double[] datum : data) {
                sum += datum[j];
            }
            centroid[j] = sum / I;
        }
        return centroid;
    }

    /**
     * merge several int arrays (as domains)
     * https://blog.csdn.net/e891377/article/details/105559304
     *
     * @param intervals input
     * @return int[][]
     */
    public static int[][] mergeIntArray(int[][] intervals) {
        if (intervals.length == 0) return new int[0][0];
        List<int[]> ans = new ArrayList<>();
        int[] cur;
        sort(intervals, 0, intervals.length - 1);
        cur = intervals[0];
        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] <= cur[1]) {
                if (intervals[i][1] > cur[1]) cur[1] = intervals[i][1];
                else continue;
            } else {
                ans.add(cur);
                cur = intervals[i];
            }
        }
        ans.add(cur);

        return ans.toArray(new int[ans.size()][]);
    }

    private static void sort(int[][] intervals, int l, int r) {
        if (l >= r) return;
        int p = partition(intervals, l, r);
        sort(intervals, l, p - 1);
        sort(intervals, p + 1, r);
    }

    private static int partition(int[][] intervals, int l, int r) {
        int[] temp = intervals[l];
        while (l < r) {
            while (l < r) {
                if (intervals[r][0] < temp[0]) {
                    intervals[l] = intervals[r];
                    break;
                }
                r--;
            }
            while (l < r) {
                if (intervals[l][0] > temp[0]) {
                    intervals[r] = intervals[l];
                    break;
                }
                l++;
            }
        }
        intervals[l] = temp;
        return l;
    }

    /**
     * merge several double arrays (as domains)
     * https://blog.csdn.net/e891377/article/details/105559304
     *
     * @param intervals input
     * @return double[][]
     */
    public static double[][] mergeDoubleArray(double[][] intervals) {
        if (intervals.length == 0) return new double[0][0];
        List<double[]> ans = new ArrayList<>();
        double[] cur;
        sort(intervals, 0, intervals.length - 1);
        cur = intervals[0];
        for (int i = 1; i < intervals.length; i++) {
            if (intervals[i][0] <= cur[1]) {
                if (intervals[i][1] > cur[1]) cur[1] = intervals[i][1];
                else continue;
            } else {
                ans.add(cur);
                cur = intervals[i];
            }
        }
        ans.add(cur);

        return ans.toArray(new double[ans.size()][]);
    }

    private static void sort(double[][] intervals, int l, int r) {
        if (l >= r) return;
        int p = partition(intervals, l, r);
        sort(intervals, l, p - 1);
        sort(intervals, p + 1, r);
    }

    private static int partition(double[][] intervals, int l, int r) {
        double[] temp = intervals[l];
        while (l < r) {
            while (l < r) {
                if (intervals[r][0] < temp[0]) {
                    intervals[l] = intervals[r];
                    break;
                }
                r--;
            }
            while (l < r) {
                if (intervals[l][0] > temp[0]) {
                    intervals[r] = intervals[l];
                    break;
                }
                l++;
            }
        }
        intervals[l] = temp;
        return l;
    }

    /**
     * remove elements in array b from array a
     *
     * @param a array a
     * @param b array b
     * @return int[]
     */
    public static int[] arrayComplement(int[] a, int[] b) {
        return Arrays.stream(a)
                .filter(num -> Arrays.stream(b).noneMatch(bNum -> bNum == num))
                .toArray();
    }

    /*-------- matrix --------*/

    /**
     * create a covariance matrix for a series of vector samples
     *
     * @param sample samples of vector
     * @return double[][]
     */
    public static double[][] covarianceMatrix(final double[][] sample) {
        // get variable space
        double[][] variableSpace = new double[sample[0].length][sample.length];
        for (int i = 0; i < sample.length; i++) {
            for (int j = 0; j < sample[i].length; j++) {
                variableSpace[j][i] = sample[i][j];
            }
        }
        // get averages
        double[] average = new double[sample[0].length];
        for (int i = 0; i < average.length; i++) {
            average[i] = average(variableSpace[i]);
        }

        // get covariance matrix
        int n = sample.length; // row
        int m = sample[0].length; // column
        double[][] covarianceMatrix = new double[m][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < m; j++) {
                double sum = 0;
                for (int k = 0; k < n; k++) {
                    sum += (variableSpace[i][k] - average[i]) * (variableSpace[j][k] - average[j]);
                }
                covarianceMatrix[i][j] = sum / (n - 1);
            }
        }
//        System.out.println(Arrays.deepToString(covarianceMatrix));
        return covarianceMatrix;
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
     * generate a series of random integer (start from 0)
     *
     * @param length length of random integer series
     * @return int[]
     */
    public static int[] randomSeries(final int length) {
        double[] random = new double[length];
        for (int i = 0; i < length; i++) {
            random[i] = Math.random();
        }
        return getArraySortedIndices(random);
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

    /**
     * generate random integer by given floor and ceiling
     *
     * @param min floor limit
     * @param max ceil limit
     * @return int
     */
    public static int randomInteger(int min, int max) {
        if (min >= max) {
            throw new IllegalArgumentException("min must be less than max");
        }

        Random random = new Random();
        return random.nextInt(max - min + 1) + min;
    }

    /**
     * generate non-redundant random integers by given floor and ceiling
     *
     * @param min   floor limit
     * @param max   ceil limit
     * @param count amount
     * @return int[]
     */
    public static int[] randomIntegers(int min, int max, int count) {
        if (min >= max) {
            throw new IllegalArgumentException("min must be less than max");
        }
        if (count > max - min + 1) {
            throw new IllegalArgumentException("count must be less than or equal to the range");
        }

        Set<Integer> randomNumbersSet = new HashSet<>();
        Random random = new Random();

        while (randomNumbersSet.size() < count) {
            int randomNumber = random.nextInt(max - min + 1) + min;
            randomNumbersSet.add(randomNumber);
        }

        int[] randomNumbersArray = new int[count];
        int index = 0;
        for (int number : randomNumbersSet) {
            randomNumbersArray[index++] = number;
        }

        return randomNumbersArray;
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
