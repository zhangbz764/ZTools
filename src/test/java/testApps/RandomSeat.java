package testApps;

import math.ZMath;

import java.util.Arrays;

/**
 * description
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/7/5
 * @time 17:20
 */
public class RandomSeat {
    public static void main(String[] args) {
        String[] name1 = new String[]{
                "lihongjian",
                "wangxiao",
                "moyichen",
                "zhangqiyan",
                "lijinze",
                "caichenyi",
                "xujianan"
        };
        String[] name2 = new String[]{
                "chenyulong",

                "zhaowenrui",
                "wujiaqian",
                "zengleijun",

                "songzhehao",
                "pengzixuan",
                "zhangbaizhou",
                "wangbingqi",
                "lijingyun",
                "xuanshuying",
                "shaojiayan",
                "shiyuan",
                "lidongyun",

                "hesiyuan",
                "wangbei",
                "wulingju",
                "zhangxuelu",
                "lijiajun",
                "lishuai",
                "wangyujiao",
                "huqian",
                "huangruike"
        };

        double[] array1 = new double[name1.length];
        for (int i = 0; i < name1.length; i++) {
            array1[i] = ZMath.random(0, 1);
        }
        int[] sort1 = ZMath.getArraySortedIndices(array1);

        double[] array2 = new double[name2.length];
        for (int i = 0; i < name2.length; i++) {
            array2[i] = ZMath.random(0, 1);
        }
        System.out.println(Arrays.toString(array2));
        int[] sort2 = ZMath.getArraySortedIndices(array2);
        System.out.println(Arrays.toString(array2));

        System.out.println("小黑屋摇号人数：" + name1.length);
        System.out.println("公区摇号人数：" + name2.length);

        System.out.println("小黑屋");
        for (int i = 0; i < name1.length; i++) {
            System.out.println(sort1[i] + 1 + "  " + name1[i]);
        }
        System.out.println("\n");
        System.out.println("公区");
        for (int j = 0; j < name2.length; j++) {
            System.out.println(sort2[j] + 1 + "  " + name2[j]);
        }
    }
}
