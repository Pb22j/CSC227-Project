import java.util.*;

public class CPUScheduler {
    
    private ReadyQueue readyQueue;
    private final int quantum = 6;
    private int time;
    private MemoryLoaderThread memoryLoader;
    
    public CPUScheduler(ReadyQueue r) {
        this.readyQueue = r;
        this.time = 0;
    }
    
    public void setMemoryLoader(MemoryLoaderThread loader) {
        this.memoryLoader = loader;
    }
    
    // Round Robin
    public List<ProcessFormat> roundRobin(Queue jobQueue) {
        List<ProcessFormat> outputList = new ArrayList<>();
        time = 0;
        
        System.out.println("\n=== Round Robin Scheduling (Quantum = " + quantum + ") ===");
        
        Thread loaderThread = new Thread(memoryLoader);
        loaderThread.start();
        
        while (!readyQueue.isEmpty() || !jobQueue.isEmpty()) {
            // CRITICAL: Synchronize with loader
            memoryLoader.syncLoadAtTime(time);
            
            if (readyQueue.isEmpty()) {
                if (!jobQueue.isEmpty()) {
                    time++;
                    continue;
                } else {
                    break;
                }
            }
            
            PCB currentPCB = readyQueue.dequeue();
            currentPCB.setState("running");
            
            int startTime = time;
            int executionTime = Math.min(currentPCB.getBrustTime(), quantum);
            
            time += executionTime;
            currentPCB.setBrustTime(currentPCB.getBrustTime() - executionTime);
            
            if (currentPCB.getBrustTime() == 0) {
                currentPCB.setState("terminated");
                currentPCB.setCompletionTime(time);
                currentPCB.setTurnaroundTime(time);
                currentPCB.setWaitingTime(currentPCB.getTurnaroundTime() - currentPCB.getOriginalBrustTime());
                System.out.println("P" + currentPCB.getPID() + " completed. Freed " + currentPCB.getSize() + "MB");
            } else {
                currentPCB.setState("ready");
                readyQueue.enqueue(currentPCB);
            }
            
            outputList.add(new ProcessFormat(currentPCB, startTime, time, executionTime));
        }
        
        memoryLoader.stop();
        try { loaderThread.join(); } catch (InterruptedException e) {}
        
        return outputList;
    }
    
    // SJF
    public List<ProcessFormat> SJF(Queue jobQueue) {
        List<ProcessFormat> outputList = new ArrayList<>();
        time = 0;
        
        System.out.println("\n=== Shortest Job First (SJF) - Non-Preemptive ===");
        
        int executedCount = 0;
        Thread loaderThread = new Thread(memoryLoader);
        loaderThread.start();
        
        while (!readyQueue.isEmpty() || !jobQueue.isEmpty()) {
            // CRITICAL: Synchronize with loader
            memoryLoader.syncLoadAtTime(time);
            
            if (readyQueue.isEmpty()) {
                if (!jobQueue.isEmpty()) {
                    time++;
                    continue;
                } else {
                    break;
                }
            }
            
            List<PCB> availableProcesses = new ArrayList<>();
            while (!readyQueue.isEmpty()) {
                availableProcesses.add(readyQueue.dequeue());
            }
            
            // Starvation Check
            /*
            for (PCB p : availableProcesses) {
                if (executedCount > p.getDegreeOfMultiprogramming()) {
                    p.setHasStarved(true);
                    System.out.println("Process P" + p.getPID() + " suffered from STARVATION!");
                }
            }
            */

            for (PCB p : availableProcesses) {
            int waitingTime = time - p.getReadyQueueArrivalTime();
            
            if (waitingTime > p.getDegreeOfMultiprogramming()) {
                if (!p.hasStarved()) {
                    p.setHasStarved(true);
                    System.out.println("Process P" + p.getPID() + " suffered from STARVATION! " +
                                     "(Waited " + waitingTime + "ms, DOM was " + 
                                     p.getDegreeOfMultiprogramming() + ")");
                }
            }
        }

            
            PCB currentPCB = Collections.min(availableProcesses, 
                (p1, p2) -> {
                    int burstCompare = Integer.compare(p1.getBrustTime(), p2.getBrustTime());
                    if (burstCompare != 0) return burstCompare;
                    return Integer.compare(p1.getPID(), p2.getPID());
                });
            
            availableProcesses.remove(currentPCB);
            
            for (PCB p : availableProcesses) {
                p.incrementProcessesExecutedSinceArrival();
                readyQueue.enqueue(p);
            }
            
            currentPCB.setState("running");
            int startTime = time;
            
            System.out.println("Executing P" + currentPCB.getPID() + " [Burst: " + currentPCB.getBrustTime() + "]");
            
            time += currentPCB.getBrustTime();
            currentPCB.setState("terminated");
            currentPCB.setCompletionTime(time);
            currentPCB.setTurnaroundTime(time);
            currentPCB.setWaitingTime(currentPCB.getTurnaroundTime() - currentPCB.getOriginalBrustTime());
            
            outputList.add(new ProcessFormat(currentPCB, startTime, time, currentPCB.getOriginalBrustTime()));
            
            System.out.println("P" + currentPCB.getPID() + " completed. Freed " + currentPCB.getSize() + "MB. Available: " + readyQueue.getAvailableMemory() + "MB");
            executedCount++;
        }
        
        memoryLoader.stop();
        try { loaderThread.join(); } catch (InterruptedException e) {}
        
        return outputList;
    }
    
    // Priority Scheduling
    public List<ProcessFormat> priorityScheduling(Queue jobQueue) {
        List<ProcessFormat> outputList = new ArrayList<>();
        time = 0;
        
        System.out.println("\n=== Priority Scheduling ===");
        
        int executedCount = 0;
        Thread loaderThread = new Thread(memoryLoader);
        loaderThread.start();
        
        while (!readyQueue.isEmpty() || !jobQueue.isEmpty()) {
            // CRITICAL: Synchronize with loader
            memoryLoader.syncLoadAtTime(time);
            
            if (readyQueue.isEmpty()) {
                if (!jobQueue.isEmpty()) {
                    time++;
                    continue;
                } else {
                    break;
                }
            }
            
            List<PCB> availableProcesses = new ArrayList<>();
            while (!readyQueue.isEmpty()) {
                availableProcesses.add(readyQueue.dequeue());
            }
            
            // Aging & Starvation
            for (PCB p : availableProcesses) { 
                p.setProcessesExecutedSinceArrival(executedCount);
               /* 
                if (p.getReadyQueueArrivalTime() > p.getDegreeOfMultiprogramming()) {
                    if (!p.hasStarved()) {
                        p.setHasStarved(true);
                        System.out.println("Process P" + p.getPID() + " is STARVING!");
                    }
                 

                
                    p.setPriority(Math.min(128, p.getPriority() + 1));
                }
                */ 

                int waitingTime = currentTime - p.getReadyQueueArrivalTime();

// Check starvation condition
            if (waitingTime > p.getDegreeOfMultiprogramming()) {
                if (!p.hasStarved()) {
                    p.setHasStarved(true);
                    System.out.println("Process P" + p.getPID() + " suffered from STARVATION!");
                    
                    // Only apply aging for Priority Scheduling
                    if (usingPriorityScheduling) {
                        p.setPriority(Math.min(128, p.getPriority() + 1));
                    }
                }
            }
            }
            
            PCB currentPCB = Collections.max(availableProcesses, 
                (p1, p2) -> {
                    int priorityCompare = Integer.compare(p1.getPriority(), p2.getPriority());
                    if (priorityCompare != 0) return priorityCompare;
                    return Integer.compare(p2.getPID(), p1.getPID());
                });
            
            availableProcesses.remove(currentPCB);
            
            for (PCB p : availableProcesses) {
                readyQueue.enqueue(p);
            }
            
            currentPCB.setState("running");
            int startTime = time;
            
            System.out.println("Executing P" + currentPCB.getPID() + " [Priority: " + currentPCB.getPriority() + "] at time " + time);
            
            time += currentPCB.getBrustTime();
            currentPCB.setState("terminated");
            currentPCB.setCompletionTime(time);
            currentPCB.setTurnaroundTime(time);
            currentPCB.setWaitingTime(currentPCB.getTurnaroundTime() - currentPCB.getOriginalBrustTime());
            
            outputList.add(new ProcessFormat(currentPCB, startTime, time, currentPCB.getOriginalBrustTime()));
            
            System.out.println("P" + currentPCB.getPID() + " completed. Free " + currentPCB.getSize() + "MB. Available: " + readyQueue.getAvailableMemory() + "MB");
            executedCount++;
        }
        
        memoryLoader.stop();
        try { loaderThread.join(); } catch (InterruptedException e) {}
        
        return outputList;
    }

    // --- Helpers (Gantt and Stats) ---
    public void printGanttChart(List<ProcessFormat> schedule, String algorithmName) {
    System.out.println("\n" + algorithmName + " Gantt Chart:");
    
    // We use StringBuilders to construct lines dynamically
    StringBuilder border = new StringBuilder();
    StringBuilder processes = new StringBuilder("|");
    StringBuilder timeline = new StringBuilder("0"); // Starts at time 0

    // Build the strings loop
    for (ProcessFormat pf : schedule) {
        String pLabel = " P" + pf.getPID() + " |";
        String endTimeStr = String.valueOf(pf.getEndTime());

        // 1. Add to the top/bottom border based on length of the label
        border.append("─".repeat(pLabel.length()));

        // 2. Add the process label to the middle line
        processes.append(pLabel);

        // 3. Align the timeline
        // We calculate how many spaces we need so the end time aligns with the '|'
        // Target length is current length of 'processes' minus the length of the number we are about to print
        int targetLength = processes.length() - endTimeStr.length();
        
        while (timeline.length() < targetLength) {
            timeline.append(" ");
        }
        timeline.append(endTimeStr);
    }

    // Print everything
    System.out.println(border);
    System.out.println(processes);
    System.out.println(border);
    System.out.println(timeline);
}
    
    public void printStatistics(List<ProcessFormat> schedule, String algorithmName) {
        System.out.println("\n" + algorithmName + " Statistics:");
        System.out.println("=".repeat(80));
        System.out.printf("%-8s %-12s %-15s %-15s %-10s%n", 
                         "PID", "Burst Time", "Waiting Time", "Turnaround", "Starved");
        System.out.println("=".repeat(80));
        
        double totalWaiting = 0;
        double totalTurnaround = 0;
        int count = 0;
        Set<Integer> printedPIDs = new HashSet<>();
        
        for (ProcessFormat pf : schedule) {
            if (pf.getState().equals("terminated") && !printedPIDs.contains(pf.getPID())) {
                System.out.printf("%-8d %-12d %-15d %-15d %-10s%n", 
                                pf.getPID(), pf.getBurstExecuted(), pf.getWaitingTime(), 
                                pf.getTurnaroundTime(), pf.isStarved() ? "YES" : "NO");
                totalWaiting += pf.getWaitingTime();
                totalTurnaround += pf.getTurnaroundTime();
                count++;
                printedPIDs.add(pf.getPID());
            }
        }
        
        System.out.println("=".repeat(80));
        System.out.printf("Average Waiting Time: %.2f ms%n", totalWaiting / count);
        System.out.printf("Average Turnaround Time: %.2f ms%n", totalTurnaround / count);
        System.out.println("=".repeat(80));
    }

}
