package net.pulga22.bulb.core.worlds.threads;

public class WorldsIOThreadManager {

    private static WorldsIOThreadManager INSTANCE;
    private volatile QueueTaskThread cloningThread;
    private volatile QueueTaskThread deletingThread;

    public static WorldsIOThreadManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new WorldsIOThreadManager();
        };
        return INSTANCE;
    }

    public void runTask(CloneWorldTask task){
        if (this.cloningThread == null || !this.cloningThread.isAlive()){
            this.cloningThread = new QueueTaskThread("WorldCloneThread");
        }
        this.cloningThread.addTask(task);
    }

    public void runTask(DeleteWorkTask task){
        if (this.deletingThread == null || !this.deletingThread.isAlive()){
            this.deletingThread = new QueueTaskThread("DeleteWorldThread");
        }
        this.deletingThread.addTask(task);
    }

}
