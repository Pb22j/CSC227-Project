import java.util.*;

public class Main {
    
    private static Queue originalJobQueue; 
    
    public static void main(String[] args) {
        Locale.setDefault(Locale.US);
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("========================================================");
        System.out.println("     CPU SCHEDULER SIMULATOR");
        System.out.println("========================================================\n");
        
        String filePath = "job.txt";
        JobLoaderThread jobLoader = new JobLoaderThread(filePath);
        Thread jobThread = new Thread(jobLoader);
        
        System.out.println("Starting job loading thread...");
        jobThread.start();
        
        try {
            jobThread.join();
        } catch (InterruptedException e) {
            System.err.println("Job loading interrupted: " + e.getMessage());
        }
        
        if (jobLoader.getJobQueue().isEmpty()) {
            System.err.println("No jobs loaded. Exiting.");
            return;
        }
        
        originalJobQueue = jobLoader.getJobQueue();
        boolean continueRunning = true;
        
        while (continueRunning) {
            System.out.println("\nSelect Scheduling Algorithm:");
            System.out.println("1. Round-Robin (RR)");
            System.out.println("2. Shortest Job First (SJF)");
            System.out.println("3. Priority Scheduling");
            System.out.println("4. Run All (Comparison)");
            System.out.println("5. Exit");
            System.out.print("Enter choice: ");
            
            int choice = scanner.nextInt();
            
            switch (choice) {
                case 1: runAlgorithm(originalJobQueue.deepCopy(), "RR"); break;
                case 2: runAlgorithm(originalJobQueue.deepCopy(), "SJF"); break;
                case 3: runAlgorithm(originalJobQueue.deepCopy(), "PS"); break;
                case 4: runAllAlgorithmsWithCopy(); break;
                case 5: continueRunning = false; break;
                default: System.out.println("Invalid choice!");
            }
        }
        scanner.close();
    }
    
    private static void runAlgorithm(Queue jobQueue, String algorithm) {
        ReadyQueue readyQueue = new ReadyQueue();
        
        // Initial load of processes
        System.out.println("Loading initial processes...");
        while (!jobQueue.isEmpty()) {
            PCB nextJob = jobQueue.peek();
            if (nextJob != null && nextJob.getSize() <= readyQueue.getAvailableMemory()) {
                jobQueue.dequeue();
                nextJob.setDegreeOfMultiprogramming(readyQueue.size());
                nextJob.setReadyQueueArrivalTime(0);
                readyQueue.enqueue(nextJob);
            } else {
                break;
            }
        }
        
        MemoryLoaderThread memoryLoader = new MemoryLoaderThread(jobQueue, readyQueue);
        CPUScheduler scheduler = new CPUScheduler(readyQueue);
        scheduler.setMemoryLoader(memoryLoader);
        
        List<ProcessFormat> schedule = null;
        String name = "";
        
        switch (algorithm) {
            case "RR": schedule = scheduler.roundRobin(jobQueue); name = "Round-Robin"; break;
            case "SJF": schedule = scheduler.SJF(jobQueue); name = "SJF"; break;
            case "PS": schedule = scheduler.priorityScheduling(jobQueue); name = "Priority"; break;
        }
        
        if (schedule != null) {
            scheduler.printGanttChart(schedule, name);
            scheduler.printStatistics(schedule, name);
        }
    }
    
    private static void runAllAlgorithmsWithCopy() {
        String[] algos = {"RR", "SJF", "PS"};
        for (String algo : algos) {
            runAlgorithm(originalJobQueue.deepCopy(), algo);
        }
    }
}