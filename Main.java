import java.util.*;

public class Main {
    
    private static Queue originalJobQueue; // Store original for reuse
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("========================================================");
        System.out.println("     CPU SCHEDULER SIMULATOR");
        System.out.println("========================================================\n");
        
        // Step 1: Load jobs from file ONCE
        String filePath = "job.txt";
        JobLoaderThread jobLoader = new JobLoaderThread(filePath);
        Thread jobThread = new Thread(jobLoader);
        
        System.out.println("Starting job loading thread...");
        jobThread.start();
        
        try {
            jobThread.join(); // Wait for job loading to complete
        } catch (InterruptedException e) {
            System.err.println("Job loading interrupted: " + e.getMessage());
        }
        
        if (jobLoader.getJobQueue().isEmpty()) {
            System.err.println("No jobs loaded. Exiting.");
            return;
        }
        
        // Store original queue for reuse
        originalJobQueue = jobLoader.getJobQueue();
        
        boolean continueRunning = true;
        
        while (continueRunning) {
            // Step 2: Choose scheduling algorithm
            System.out.println("\n========================================================");
            System.out.println("Select Scheduling Algorithm:");
            System.out.println("1. Round-Robin (RR) - Quantum = 6ms");
            System.out.println("2. Shortest Job First (SJF) - Non-Preemptive");
            System.out.println("3. Priority Scheduling - Non-Preemptive with Aging");
            System.out.println("4. Run All Algorithms (Comparison)");
            System.out.println("5. Exit");
            System.out.print("Enter your choice (1-5): ");
            
            int choice = scanner.nextInt();
            System.out.println("========================================================\n");
            
            switch (choice) {
                case 1:
                    runAlgorithm(originalJobQueue.deepCopy(), "RR");
                    break;
                case 2:
                    runAlgorithm(originalJobQueue.deepCopy(), "SJF");
                    break;
                case 3:
                    runAlgorithm(originalJobQueue.deepCopy(), "PS");
                    break;
                case 4:
                    runAllAlgorithmsWithCopy();
                    break;
                case 5:
                    continueRunning = false;
                    System.out.println("Exiting...");
                    break;
                default:
                    System.out.println("Invalid choice!");
            }
        }
        
        scanner.close();
    }
    
   private static void runAlgorithm(Queue jobQueue, String algorithm) {
    ReadyQueue readyQueue = new ReadyQueue();
    
    // Don't use a separate memory loader thread for now - handle it in scheduler
    // Load initial processes that fit in memory
    System.out.println("Loading initial processes to memory...");
    while (!jobQueue.isEmpty()) {
        PCB nextJob = jobQueue.peek();
        
        if (nextJob != null && nextJob.getSize() <= readyQueue.getAvailableMemory()) {
            jobQueue.dequeue();
            nextJob.setDegreeOfMultiprogramming(readyQueue.size());
            nextJob.setReadyQueueArrivalTime(0);
            
            if (readyQueue.enqueue(nextJob)) {
                System.out.println("Loaded P" + nextJob.getPID() + 
                                 " to ready queue (Degree of Multiprogramming: " + 
                                 nextJob.getDegreeOfMultiprogramming() + ")");
            }
        } else {
            // Can't load more processes initially
            System.out.println("⚠️ Cannot load P" + nextJob.getPID() + 
                             " initially - insufficient memory. Will load after some processes complete.");
            break;
        }
    }
    
    System.out.println("\nStarting scheduling...\n");
    
    CPUScheduler scheduler = new CPUScheduler(readyQueue);
    List<ProcessFormat> schedule = null;
    String algorithmName = "";
    
    switch (algorithm) {
        case "RR":
            schedule = scheduler.roundRobinDynamic(jobQueue);
            algorithmName = "Round-Robin";
            break;
        case "SJF":
            schedule = scheduler.SJFDynamic(jobQueue);
            algorithmName = "Shortest Job First";
            break;
        case "PS":
            schedule = scheduler.prioritySchedulingDynamic(jobQueue);
            algorithmName = "Priority Scheduling";
            break;
    }
    
    if (schedule != null) {
        scheduler.printGanttChart(schedule, algorithmName);
        scheduler.printStatistics(schedule, algorithmName);
    }
}
    // Use deep copy instead of reloading from file
    private static void runAllAlgorithmsWithCopy() {
        String[] algorithms = {"RR", "SJF", "PS"};
        
        System.out.println("\n========================================================");
        System.out.println("=     RUNNING ALL ALGORITHMS FOR COMPARISON             =");
        System.out.println("========================================================");
        
        for (String algo : algorithms) {
            // Use deep copy of original queue ✅
            Queue jobQueueCopy = originalJobQueue.deepCopy();
            runAlgorithm(jobQueueCopy, algo);
            System.out.println("\n");
        }
        
        System.out.println("\n========================================================");
        System.out.println("=           ALL ALGORITHMS COMPLETED                     =");
        System.out.println("========================================================");
    }
}