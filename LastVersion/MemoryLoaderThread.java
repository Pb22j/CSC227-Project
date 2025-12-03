// REMOVED: import java.util.Queue; <--- This was causing the error
import java.util.Scanner;

public class MemoryLoaderThread implements Runnable {
    
    private Queue jobQueue;
    private ReadyQueue readyQueue;
    private volatile boolean running;
    private int currentTime;
    private final Object lock = new Object();
    private boolean processingNeeded = false;
    
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
            synchronized (lock) {
                // Wait for signal from CPU Scheduler
                while (!processingNeeded && running) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        return;
                    }
                }

                if (!running) break;

                // --- Loading Logic ---
                if (!jobQueue.isEmpty()) {
                    while (!jobQueue.isEmpty()) {
                        PCB nextJob = jobQueue.peek();
                        
                        if (nextJob == null) break;

                        // Skip if too big for total memory
                        if (nextJob.getSize() > 2048) {
                            System.out.println("WARNING: P" + nextJob.getPID() + 
                                             " cannot fit in memory!");
                            jobQueue.dequeue();
                            continue;
                        }

                        // Load if fits in AVAILABLE memory
                        if (nextJob.getSize() <= readyQueue.getAvailableMemory()) {
                            jobQueue.dequeue();
                            nextJob.setDegreeOfMultiprogramming(readyQueue.size());
                            nextJob.setReadyQueueArrivalTime(currentTime);
                            
                            if (readyQueue.enqueue(nextJob)) {
                                System.out.println("Loaded P" + nextJob.getPID() + 
                                                 " to ready queue at time " + currentTime +
                                                 " (Degree of Multiprogramming: " + 
                                                 nextJob.getDegreeOfMultiprogramming() + ")");
                            }
                        } else {
                            break; // Stop if memory is full
                        }
                    }
                }
                
                if (jobQueue.isEmpty()) {
                    running = false;
                    System.out.println("Memory Loader Thread finished - All jobs processed");
                }
                
                // Signal completion back to CPU Scheduler
                processingNeeded = false;
                lock.notifyAll(); 
            }
        }
    }
    
    // Handshake method called by CPU Scheduler
    public void syncLoadAtTime(int time) {
        synchronized (lock) {
            if (!running) return;
            
            this.currentTime = time;
            this.processingNeeded = true;
            lock.notifyAll(); // Wake up Loader
            
            // Wait for Loader to finish
            while (processingNeeded && running) {
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }
    
    public void stop() {
        synchronized (lock) {
            running = false;
            processingNeeded = true; // Ensure it wakes up to exit
            lock.notifyAll();
        }
    }
}