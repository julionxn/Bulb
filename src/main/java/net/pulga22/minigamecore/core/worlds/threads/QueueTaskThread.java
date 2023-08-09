package net.pulga22.minigamecore.core.worlds.threads;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class QueueTaskThread extends Thread{

    private final Queue<Runnable> taskQueue = new ConcurrentLinkedQueue<>();

    public QueueTaskThread(String name) {
        this.setName(name);
    }

    public void addTask(Runnable task){
        this.taskQueue.add(task);
        synchronized (this) {
            if (this.getState() == State.NEW && !this.isAlive()){
                this.start();
            }
        }
    }

    @Override
    public void run() {
        while (!this.taskQueue.isEmpty()){
            Runnable task = this.taskQueue.poll();
            task.run();
        }
    }
}
