// CPUScheduler.java
import java.util.*;

public class CPUScheduler {
    
    private ReadyQueue readyQueue;
    private final int quantum = 6; // quantum for Round-Robin
    private int time;
    private List<PCB> allProcesses; // Track all processes for starvation detection
    
    public CPUScheduler(ReadyQueue r) {
        this.readyQueue = r;
        this.time = 0;
        this.allProcesses = new ArrayList<>();
    }
    
    // Round-Robin Scheduling
    public List<ProcessFormat> roundRobin() {
        List<ProcessFormat> outputList = new ArrayList<>();
        time = 0;
        
        System.out.println("\n=== Round-Robin Scheduling (Quantum = " + quantum + ") ===");
        
        while (!readyQueue.isEmpty()) {
            PCB currentPCB = readyQueue.dequeue();
            currentPCB.setState("running");
            
            int startTime = time;
            int executionTime = Math.min(currentPCB.getBrustTime(), quantum);
            
            time += executionTime;
            currentPCB.setBrustTime(currentPCB.getBrustTime() - executionTime);
            
            if (currentPCB.getBrustTime() == 0) {
                // Process completed
                currentPCB.setState("terminated");
                currentPCB.setCompletionTime(time);
                currentPCB.setTurnaroundTime(time); // Arrival time is 0
                currentPCB.setWaitingTime(currentPCB.getTurnaroundTime() - currentPCB.getOriginalBrustTime());
                
                readyQueue.setLock(false);
            } else {
                // Process needs more time
                currentPCB.setState("ready");
                readyQueue.setLock(true);
                readyQueue.enqueue(currentPCB);
                readyQueue.setLock(false);
            }
            
            outputList.add(new ProcessFormat(currentPCB, startTime, time, executionTime));
        }
        
        return outputList;
    }
    
    // Shortest Job First (Non-Preemptive)
    public List<ProcessFormat> SJF() {
        List<ProcessFormat> outputList = new ArrayList<>();
        time = 0;
        
        System.out.println("\n=== Shortest Job First (SJF) - Non-Preemptive ===");
        
        // Get all processes and track for starvation
        allProcesses.clear();
        while (!readyQueue.isEmpty()) {
            allProcesses.add(readyQueue.dequeue());
        }
        
        // Sort by burst time
        allProcesses.sort(new PCBComparator("sjf"));
        
        // Check for starvation and execute
        int executedCount = 0;
        
        for (PCB currentPCB : allProcesses) {
            // Check for starvation
            if (executedCount > currentPCB.getDegreeOfMultiprogramming()) {
                currentPCB.setHasStarved(true);
                System.out.println("⚠ Process P" + currentPCB.getPID() + " suffered from STARVATION!");
            }
            
            currentPCB.setState("running");
            int startTime = time;
            
            time += currentPCB.getBrustTime();
            
            currentPCB.setState("terminated");
            currentPCB.setCompletionTime(time);
            currentPCB.setTurnaroundTime(time); // Arrival time is 0
            currentPCB.setWaitingTime(currentPCB.getTurnaroundTime() - currentPCB.getOriginalBrustTime());
            
            outputList.add(new ProcessFormat(currentPCB, startTime, time, currentPCB.getOriginalBrustTime()));
            
            // Increment executed count for remaining processes
            for (PCB p : allProcesses) {
                if (!p.state.equals("terminated")) {
                    p.incrementProcessesExecutedSinceArrival();
                }
            }
            
            executedCount++;
        }
        
        return outputList;
    }
    
    // Priority Scheduling (Non-Preemptive) with Aging
   public List<ProcessFormat> priorityScheduling() {
    List<ProcessFormat> outputList = new ArrayList<>();
    time = 0;
    
    System.out.println("\n=== Priority Scheduling (Non-Preemptive) with Aging ===");
    
    allProcesses.clear();
    while (!readyQueue.isEmpty()) {
        allProcesses.add(readyQueue.dequeue());
    }
    
    int executedCount = 0;
    
    while (!allProcesses.isEmpty()) {
        // Update execution counter for all waiting processes
        for (PCB p : allProcesses) {
            p.setProcessesExecutedSinceArrival(executedCount);
        }
        
        // Check for starvation and apply aging
        for (PCB p : allProcesses) {
            if (p.getProcessesExecutedSinceArrival() > p.getDegreeOfMultiprogramming()) {
                // Mark as starved (only once)
                if (!p.hasStarved()) {
                    p.setHasStarved(true);
                    System.out.println("⚠ Process P" + p.getPID() + " is STARVING! (Original Priority: " + 
                                     p.getOriginalPriority() + ")");
                }
                
                // Apply aging - increase priority by 1 each time
                int oldPriority = p.getPriority();
                p.setPriority(Math.min(128, p.getPriority() + 1));
                
                if (oldPriority != p.getPriority()) {
                    System.out.println("  → Aging applied to P" + p.getPID() + 
                                     ": Priority " + oldPriority + " → " + p.getPriority());
                }
            }
        }
        
        // Select highest priority process
        PCB currentPCB = Collections.max(allProcesses, 
            (p1, p2) -> {
                int priorityCompare = Integer.compare(p1.getPriority(), p2.getPriority());
                if (priorityCompare != 0) return priorityCompare;
                return Integer.compare(p2.getPID(), p1.getPID());
            });
        
        allProcesses.remove(currentPCB);
        currentPCB.setState("running");
        int startTime = time;
        
        String priorityInfo = currentPCB.getPriority() != currentPCB.getOriginalPriority() ? 
                              " (boosted from " + currentPCB.getOriginalPriority() + ")" : "";
        System.out.println("Executing P" + currentPCB.getPID() + 
                         " [Priority: " + currentPCB.getPriority() + priorityInfo + "]");
        
        time += currentPCB.getBrustTime();
        currentPCB.setState("terminated");
        currentPCB.setCompletionTime(time);
        currentPCB.setTurnaroundTime(time - 0);
        currentPCB.setWaitingTime(currentPCB.getTurnaroundTime() - currentPCB.getOriginalBrustTime());
        
        outputList.add(new ProcessFormat(currentPCB, startTime, time, currentPCB.getOriginalBrustTime()));
        executedCount++;
    }
    
    return outputList;
}
    // Print results in Gantt Chart format
    public void printGanttChart(List<ProcessFormat> schedule, String algorithmName) {
        System.out.println("\n" + algorithmName + " Gantt Chart:");
        System.out.println("─".repeat(80));
        
        // Print process boxes
        System.out.print("|");
        for (ProcessFormat pf : schedule) {
            String label = " P" + pf.getPID() + " ";
            System.out.print(label + "|");
        }
        System.out.println();
        
        // Print timeline
        System.out.print("0");
        for (ProcessFormat pf : schedule) {
            int spaces = (" P" + pf.getPID() + " ").length();
            System.out.print(" ".repeat(spaces) + pf.getEndTime());
        }
        System.out.println("\n" + "─".repeat(80));
    }
    
    // Calculate and print statistics
    public void printStatistics(List<ProcessFormat> schedule, String algorithmName) {
        System.out.println("\n" + algorithmName + " Statistics:");
        System.out.println("─".repeat(80));
        System.out.printf("%-8s %-12s %-15s %-15s %-10s%n", 
                         "PID", "Burst Time", "Waiting Time", "Turnaround", "Starved");
        System.out.println("─".repeat(80));
        
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
        
        System.out.println("─".repeat(80));
        System.out.printf("Average Waiting Time: %.2f ms%n", totalWaiting / count);
        System.out.printf("Average Turnaround Time: %.2f ms%n", totalTurnaround / count);
        System.out.println("─".repeat(80));
    }
}