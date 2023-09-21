package testApps;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * description
 *
 * @author ZHANG Bai-zhou zhangbz
 * @project shopping_mall
 * @date 2021/7/5
 * @time 17:20
 */
public class SeatLottery {
    public static void main(String[] args) throws InterruptedException {
        System.out.println("🔊 Inst.AAA 座位大乐透 2023 现在开始 !!!");
        Thread.sleep(1000);

        System.out.println();
        for (int countdown = 5; countdown >= 0; countdown--) {
            System.out.print("\r倒计时: " + countdown);
            Thread.sleep(1000);
        }
        System.out.println();
        System.out.println("--------🎈🎈🎈🎈🎈🎈--------");
        System.out.println();
        Thread.sleep(1000);

        String[] masNames = new String[]{
                "胡潜", "李帅",
                "闻健", "金艺丹", "朱雪融",
                "李祎", "田野", "王佳钺",

                "王蓓", "贺思远", "章雪璐", "李佳骏",
                "史季", "张超", "王炎钰",
                "朱建皓", "刘雨晴", "曾令通", "程世纪",

                "吴凌菊",
                "刘逸卓", "张笑凡", "邹雨菲",
                "张远", "洪方东", "钱叶柯",

                "黄瑞克",
                "冯以恒", "武文忻",
                "徐耀新", "徐宇飞",
        };

        List<Integer> remainingIndices = new ArrayList<>();
        for (int i = 0; i < masNames.length; i++) {
            remainingIndices.add(i);
        }
        Random rand = new Random();

        int index = 1;
        while (!remainingIndices.isEmpty()) {
            int randomIndex = rand.nextInt(remainingIndices.size());
            int selectedIndex = remainingIndices.get(randomIndex);

            if (index == 1) {
                System.out.println(index + "号签：" + masNames[selectedIndex] + "  恭喜🥇~");
            } else if (index ==2) {
                System.out.println(index + "号签：" + masNames[selectedIndex] + "  恭喜🥈~");
            } else if (index == 3) {
                System.out.println(index + "号签：" + masNames[selectedIndex] + "  恭喜🥉~");
            } else if (index == masNames.length) {
                System.out.println(index + "号签：" + masNames[selectedIndex] + "  👈从某种程度上来说，TA很幸运。");
            } else {
                System.out.println(index + "号签：" + masNames[selectedIndex]);
            }

            remainingIndices.remove(randomIndex);

            Thread.sleep(500);
            index++;
        }

        System.out.println();
        System.out.println("----------------------");
        System.out.println("全部抽签已结束，请按照签位顺序选择座位，在对应桌子标注自己的序号后，截图并在群内公示。");
    }
}
