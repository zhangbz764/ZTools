package math;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * perform permutation and combination
 * https://www.cnblogs.com/zzlback/p/10947064.html
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/4/30
 * @time 14:50
 */
public class ZPermuCombi {
    private static Stack<Integer> stack = new Stack<Integer>();

    private List<List<Integer>> combinationResults;
    private List<List<Integer>> permutationResults;

    /* ------------- constructor ------------- */

    public ZPermuCombi() {
        this.combinationResults = new ArrayList<>();
        this.permutationResults = new ArrayList<>();
    }

    /* ------------- member function ------------- */

    /**
     * perform permutation
     *
     * @param intList
     * @param tar
     * @param curr
     * @return void
     */
    public void permutation(int[] intList, int tar, int curr) {
        if (curr == tar) {
            List<Integer> list = new ArrayList<>(stack);
            this.permutationResults.add(list);
            return;
        }

        for (int i = 0; i < intList.length; i++) {
            if (!stack.contains(intList[i])) {
                stack.add(intList[i]);
                permutation(intList, tar, curr + 1);
                stack.pop();
            }
        }
    }

    /**
     * perform combination
     *
     * @param intList
     * @param tar
     * @param has
     * @param curr
     * @return void
     */
    public void combination(int[] intList, int tar, int has, int curr) {
        if (has == tar) {
            List<Integer> list = new ArrayList<>(stack);
            combinationResults.add(list);
            return;
        }

        for (int i = curr; i < intList.length; i++) {
            if (!stack.contains(intList[i])) {
                stack.add(intList[i]);
                combination(intList, tar, has + 1, i);
                stack.pop();
            }
        }
    }

    /* ------------- setter & getter ------------- */

    public List<List<Integer>> getCombinationResults() {
        return combinationResults;
    }

    public List<List<Integer>> getPermutationResults() {
        return permutationResults;
    }
}
