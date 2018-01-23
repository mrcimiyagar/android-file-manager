package kasper.android.file_explorer.utils;

import java.util.concurrent.LinkedBlockingQueue;

import kasper.android.file_explorer.models.BackTask;

public class TaskUtils {

    private static LinkedBlockingQueue<BackTask> backTasksQueue;
    private static LinkedBlockingQueue<Object[]> taskBackParams;
    private static Thread backRunnerEngine;
    private static boolean backEngineToNext = false;
    private static BackTask currentBackTask;
    private static Object[] currentBackTaskParams;

    public static void setup() {

        backTasksQueue = new LinkedBlockingQueue<>();

        taskBackParams = new LinkedBlockingQueue<>();

        backRunnerEngine = new Thread(new Runnable() {
            @Override
            public void run() {

                while (true) {

                    try {
                        currentBackTask = backTasksQueue.take();
                        currentBackTaskParams = taskBackParams.take();
                    }
                    catch (Exception ignored) { }

                    if (currentBackTask != null) {

                        if (backEngineToNext) {
                            backEngineToNext = false;
                            continue;
                        }
                        currentBackTask.doInBackground(currentBackTaskParams);
                    }
                }
            }
        });

        backRunnerEngine.start();
    }

    public static void startNewBackTask(BackTask task, Object... params) {

        taskBackParams.offer(params);
        backTasksQueue.offer(task);
    }

    public static void stopBackTask(BackTask task) {

        if (task == currentBackTask) {
            backEngineToNext = true;
            return;
        }

        backTasksQueue.remove(task);

        task.onCancelled();
    }
}