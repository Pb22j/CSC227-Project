// Main.java
import java.util.*;

public class Main {
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("     CPU SCHEDULER SIMULATOR");
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        // Step 1: Load jobs from file
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
        
        // Step 2: Choose scheduling algorithm
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("Select Scheduling Algorithm:");
        System.out.println("1. Round-Robin (RR) - Quantum = 6ms");
        System.out.println("2. Shortest Job First (SJF) - Non-Preemptive");
        System.out.println("3. Priority Scheduling - Non-Preemptive with Aging");
        System.out.println("4. Run All Algorithms (Comparison)");
        System.out.print("Enter your choice (1-4): ");
        
        int choice = scanner.nextInt();
        System.out.println("═══════════════════════════════════════════════════════\n");
        
        switch (choice) {
            case 1:
                runAlgorithm(jobLoader.getJobQueue(), "RR");
                break;
            case 2:
                runAlgorithm(jobLoader.getJobQueue(), "SJF");
                break;
            case 3:
                runAlgorithm(jobLoader.getJobQueue(), "PS");
                break;
            case 4:
                runAllAlgorithms(filePath);
                break;
            default:
                System.out.println("Invalid choice!");
        }
        
        scanner.close();
    }
    
    private static void runAlgorithm(Queue jobQueue, String algorithm) {
        ReadyQueue readyQueue = new ReadyQueue();
        MemoryLoaderThread memoryLoader = new MemoryLoaderThread(jobQueue, readyQueue);
        Thread memoryThread = new Thread(memoryLoader);
        
        memoryThread.start();
        
        // Wait for all jobs to be loaded to ready queue
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        CPUScheduler scheduler = new CPUScheduler(readyQueue);
        List<ProcessFormat> schedule = null;
        String algorithmName = "";
        
        switch (algorithm) {
            case "RR":
                schedule = scheduler.roundRobin();
                algorithmName = "Round-Robin";
                break;
            case "SJF":
                schedule = scheduler.SJF();
                algorithmName = "Shortest Job First";
                break;
            case "PS":
                schedule = scheduler.priorityScheduling();
                algorithmName = "Priority Scheduling";
                break;
        }
        
        if (schedule != null) {
            scheduler.printGanttChart(schedule, algorithmName);
            scheduler.printStatistics(schedule, algorithmName);
        }
        
        memoryLoader.stop();
    }
    
    private static void runAllAlgorithms(String filePath) {
        String[] algorithms = {"RR", "SJF", "PS"};
        
        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║     RUNNING ALL ALGORITHMS FOR COMPARISON             ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
        
        for (String algo : algorithms) {
            // Reload jobs for each algorithm
            JobLoaderThread jobLoader = new JobLoaderThread(filePath);
            Thread jobThread = new Thread(jobLoader);
            jobThread.start();
            
            try {
                jobThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            
            runAlgorithm(jobLoader.getJobQueue(), algo);
            System.out.println("\n");
        }
        
        System.out.println("\n╔═══════════════════════════════════════════════════════╗");
        System.out.println("║           ALL ALGORITHMS COMPLETED                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════╝");
    }
}