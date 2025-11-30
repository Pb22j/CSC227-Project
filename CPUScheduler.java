import java.util.*;

public class CPUScheduler {
    
    private ReadyQueue readyQueue;
    private final int quantum = 6;
    private int time;
    
    public CPUScheduler(ReadyQueue r) {
        this.readyQueue = r;
        this.time = 0;
    }
    
    // Round-Robin with Dynamic Loading
    public List<ProcessFormat> roundRobinDynamic(Queue jobQueue) {
        List<ProcessFormat> outputList = new ArrayList<>();
        time = 0;
        
        System.out.println("\n=== Round-Robin Scheduling (Quantum = " + quantum + ") ===");
        
        while (!readyQueue.isEmpty() || !jobQueue.isEmpty()) {
            
            // Try to load more processes
            while (!jobQueue.isEmpty() && !readyQueue.isLock()) {
                PCB nextJob = jobQueue.peek();
                if (nextJob != null && nextJob.getSize() <= readyQueue.getAvailableMemory()) {
                    jobQueue.dequeue();
                    nextJob.setDegreeOfMultiprogramming(readyQueue.size());
                    nextJob.setReadyQueueArrivalTime(time);
                    if (readyQueue.enqueue(nextJob)) {
                        System.out.println("Loaded P" + nextJob.getPID() + " at time " + time);
                    }
                } else {
                    break;
                }
            }
            
            if (readyQueue.isEmpty()) break;
            
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
        
        return outputList;
    }
    
    // SJF with Dynamic Loading
    public List<ProcessFormat> SJFDynamic(Queue jobQueue) {
        List<ProcessFormat> outputList = new ArrayList<>();
        time = 0;
        
        System.out.println("\n=== Shortest Job First (SJF) - Non-Preemptive ===");
        
        int executedCount = 0;
        
        while (!readyQueue.isEmpty() || !jobQueue.isEmpty()) {
            
            // Try to load more processes
            while (!jobQueue.isEmpty()) {
                PCB nextJob = jobQueue.peek();
                if (nextJob != null && nextJob.getSize() <= readyQueue.getAvailableMemory()) {
                    jobQueue.dequeue();
                    nextJob.setDegreeOfMultiprogramming(readyQueue.size());
                    nextJob.setReadyQueueArrivalTime(time);
                    if (readyQueue.enqueue(nextJob)) {
                        System.out.println("Loaded P" + nextJob.getPID() + " at time " + time);
                    }
                } else {
                    break;
                }
            }
            
            if (readyQueue.isEmpty()) break;
            
            // Get all processes and find shortest
            List<PCB> availableProcesses = new ArrayList<>();
            while (!readyQueue.isEmpty()) {
                availableProcesses.add(readyQueue.dequeue());
            }
            
            // Check starvation
            for (PCB p : availableProcesses) {
                if (executedCount > p.getDegreeOfMultiprogramming()) {
                    p.setHasStarved(true);
                    System.out.println("⚠ Process P" + p.getPID() + " suffered from STARVATION!");
                }
            }
            
            // Select shortest job
            PCB currentPCB = Collections.min(availableProcesses, 
                (p1, p2) -> {
                    int burstCompare = Integer.compare(p1.getBrustTime(), p2.getBrustTime());
                    if (burstCompare != 0) return burstCompare;
                    return Integer.compare(p1.getPID(), p2.getPID());
                });
            
            availableProcesses.remove(currentPCB);
            
            // Put others back
            for (PCB p : availableProcesses) {
                p.incrementProcessesExecutedSinceArrival();
                readyQueue.enqueue(p);
            }
            
            // Execute
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
        
        return outputList;
    }
    
    // Priority Scheduling with Dynamic Loading
    public List<ProcessFormat> prioritySchedulingDynamic(Queue jobQueue) {
        List<ProcessFormat> outputList = new ArrayList<>();
        time = 0;
        
        System.out.println("\n=== Priority Scheduling (Non-Preemptive) with Aging ===");
        
        int executedCount = 0;
        
        while (!readyQueue.isEmpty() || !jobQueue.isEmpty()) {
            
            // Try to load more processes
            while (!jobQueue.isEmpty()) {
                PCB nextJob = jobQueue.peek();
                if (nextJob != null && nextJob.getSize() <= readyQueue.getAvailableMemory()) {
                    jobQueue.dequeue();
                    nextJob.setDegreeOfMultiprogramming(readyQueue.size());
                    nextJob.setReadyQueueArrivalTime(time);
                    if (readyQueue.enqueue(nextJob)) {
                        System.out.println("Loaded P" + nextJob.getPID() + " to ready queue at time " + time + 
                                         " (Degree of Multiprogramming: " + nextJob.getDegreeOfMultiprogramming() + ")");
                    }
                } else {
                    break;
                }
            }
            
            if (readyQueue.isEmpty()) break;
            
            // Get all available processes
            List<PCB> availableProcesses = new ArrayList<>();
            while (!readyQueue.isEmpty()) {
                availableProcesses.add(readyQueue.dequeue());
            }
            
            // Update and check starvation
            for (PCB p : availableProcesses) { //! you cannot do `for each` by queue, it's working with lists (review this) 
                p.setProcessesExecutedSinceArrival(executedCount);
                
                if (p.getProcessesExecutedSinceArrival() > p.getDegreeOfMultiprogramming()) {
                    if (!p.hasStarved()) {
                        p.setHasStarved(true);
                        System.out.println("Process P" + p.getPID() + " is STARVING! (Original Priority: " + 
                                         p.getOriginalPriority() + ")");
                    }
                    
                    int oldPriority = p.getPriority();
                    p.setPriority(Math.min(128, p.getPriority() + 1));
                    
                    if (oldPriority != p.getPriority()) {
                        System.out.println("  => Aging applied to P" + p.getPID() + 
                                         ": Priority " + oldPriority + " => " + p.getPriority());
                    }
                }
            }
            
            // Select highest priority //! instead of using `ordering` method for priority queue, since we transfer the elements to the list
            PCB currentPCB = Collections.max(availableProcesses, 
                (p1, p2) -> {
                    int priorityCompare = Integer.compare(p1.getPriority(), p2.getPriority());
                    if (priorityCompare != 0) return priorityCompare;
                    return Integer.compare(p2.getPID(), p1.getPID());
                });
            
            availableProcesses.remove(currentPCB);
            
            // Put others back
            for (PCB p : availableProcesses) {
                readyQueue.enqueue(p);
            }
            
            // Execute
            currentPCB.setState("running");
            int startTime = time;
            
            String priorityInfo = currentPCB.getPriority() != currentPCB.getOriginalPriority() ? 
                                  " (boosted from " + currentPCB.getOriginalPriority() + ")" : "";
            System.out.println("Executing P" + currentPCB.getPID() + 
                             " [Priority: " + currentPCB.getPriority() + priorityInfo + "] at time " + time);
            
            time += currentPCB.getBrustTime();
            currentPCB.setState("terminated");
            currentPCB.setCompletionTime(time);
            currentPCB.setTurnaroundTime(time);
            currentPCB.setWaitingTime(currentPCB.getTurnaroundTime() - currentPCB.getOriginalBrustTime());
            
            outputList.add(new ProcessFormat(currentPCB, startTime, time, currentPCB.getOriginalBrustTime()));
            
            System.out.println("P" + currentPCB.getPID() + " completed. Freed " + currentPCB.getSize() + "MB. Available: " + readyQueue.getAvailableMemory() + "MB");
            
            executedCount++;
        }
        
        return outputList;
    }
    
    // Gantt Chart
    public void printGanttChart(List<ProcessFormat> schedule, String algorithmName) {
        System.out.println("\n" + algorithmName + " Gantt Chart:");
        System.out.println("─".repeat(80));
        
        System.out.print("|");
        for (ProcessFormat pf : schedule) {
            String label = " P" + pf.getPID() + " ";
            System.out.print(label + "|");
        }
        System.out.println();
        
        System.out.print("0");
        for (ProcessFormat pf : schedule) {
            int spaces = (" P" + pf.getPID() + " ").length();
            System.out.print(" ".repeat(spaces) + pf.getEndTime());
        }
        System.out.println("\n" + "─".repeat(80));
    }
    
    // Statistics
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
                                pf.getPID(), 
                                pf.getBurstExecuted(),
                                pf.getWaitingTime(), 
                                pf.getTurnaroundTime(),
                                pf.isStarved() ? "YES" : "NO");
                
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