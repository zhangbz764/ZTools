package system;

import com.google.gson.JsonObject;

import java.lang.reflect.Field;
import java.util.Queue;
import java.util.concurrent.*;

/**
 * description
 *
 * @author Baizhou Zhang zhangbz
 * @project Ztools
 * @date 2024/12/4
 * @time 10:35
 */
public class ZSystemMana {


    /* ------------- constructor ------------- */

    public ZSystemMana() {

    }

    /* ------------- member function ------------- */

    /**
     * HE_Mesh memory cleaner
     *
     * @param
     * @return void
     */
    public void startHE_MeshCleanupTask() {
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            try {
                // 通过 instance() 方法获取 WB_ProgressTracker 的单例
                Class<?> trackerClass = Class.forName("wblut.core.WB_ProgressReporter$WB_ProgressTracker");
                Object trackerInstance = trackerClass.getMethod("instance").invoke(null);

                // 获取 trackerInstance 中的 statuses 队列字段
                Field statusesField = trackerClass.getDeclaredField("statuses");
                statusesField.setAccessible(true);
                Queue<?> statuses = (Queue<?>) statusesField.get(trackerInstance);

                // 清空 statuses 队列
                statuses.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }, 0, 2, TimeUnit.MINUTES); // 每隔1分钟执行一次检查任务
    }

    /**
     * core server executor function
     *
     * @param executor ExecutorService
     * @param future   Future<Void>
     */
    private void executorByTimeout(ExecutorService executor, Future<Void> future, int timeout) {
        try {
            future.get(timeout, TimeUnit.SECONDS);
        } catch (TimeoutException e) {
            // 超时
            System.out.println("Code execution time exceeded " + timeout + " seconds.");
            future.cancel(true); // 终止执行
        } catch (Exception e) {
            // 其他异常
            e.printStackTrace();
        } finally {
            executor.shutdownNow(); // 关闭线程池
        }
    }
}
