package testFunc;

import processing.core.PApplet;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2023/3/9
 * @time 15:45
 */
public class TestMultiThreads extends PApplet {

    MyRunnable runnable = new MyRunnable();
    Thread thread = new Thread(runnable);

}

class MyRunnable implements Runnable {
    @Override
    public void run() {
        System.out.println("threads ");
    }
}
