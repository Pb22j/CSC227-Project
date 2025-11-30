public class MemoryLoaderThread implements Runnable {
    
    private Queue jobQueue;
    private ReadyQueue readyQueue;
    private volatile boolean running;
    private int currentTime;
    
    public MemoryLoaderThread(Queue jobQueue, ReadyQueue readyQueue) {
        this.jobQueue = jobQueue;
        this.readyQueue = readyQueue;
        this.running = true;
        this.currentTime = 0;
    }
    
    @Override
    public void run() {
        System.out.println("Memory Loader Thread started...");
        
        while (running) {
            // Check if we can load more jobs
            if (!jobQueue.isEmpty() && !readyQueue.isLock()) {
                PCB nextJob = jobQueue.peek();
                
                if (nextJob != null && nextJob.getSize() <= readyQueue.getAvailableMemory()) {
                    jobQueue.dequeue();
                    
                    // Set degree of multiprogramming and arrival time
                    nextJob.setDegreeOfMultiprogramming(readyQueue.size());
                    nextJob.setReadyQueueArrivalTime(currentTime);
                    
                    if (readyQueue.enqueue(nextJob)) {
                        System.out.println("Loaded P" + nextJob.getPID() + 
                                         " to ready queue (Degree of Multiprogramming: " + 
                                         nextJob.getDegreeOfMultiprogramming() + ")");
                    }
                } else if (nextJob != null) {
                    
                    System.out.println("WARNING: P" + nextJob.getPID() + 
                                     "cannot fit in memory! Required: " + nextJob.getSize() + 
                                     "MB, Available: " + readyQueue.getAvailableMemory() + "MB");
                    
                    
                    jobQueue.dequeue();
                }
            }
            
            
            if (jobQueue.isEmpty()) {
                running = false;
                System.out.println("Memory Loader Thread finished. All jobs processed.\n");
                break;
            }
            
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }
    
    public void stop() {
        running = false;
    }
    
    public void setCurrentTime(int time) {
        this.currentTime = time;
    }
}