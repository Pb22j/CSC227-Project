// MemoryLoaderThread.java
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
                }
            }
            
            // Stop if job queue is empty and ready queue is empty
            if (jobQueue.isEmpty() && readyQueue.isEmpty()) {
                running = false;
            }
            
            try {
                Thread.sleep(10); // Small delay to prevent busy waiting
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        System.out.println("Memory Loader Thread finished.");
    }
    
    public void stop() {
        running = false;
    }
    
    public void setCurrentTime(int time) {
        this.currentTime = time;
    }
}