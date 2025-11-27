import java.util.Arrays;

public class CPUScheduler {

    private PCB[] readyQueue;
    private PCB[] jobQueue;    
    private JobLoader loader;   // to load next jobs and update the ready queue
    private int memorySize;

    public CPUScheduler(JobLoader loader) {
        this.loader = loader;
        this.readyQueue = loader.readyQueue;
        this.memorySize = loader.memorySize;
        this.jobQueue = loader.JobQueue;
    }


    // ------------------------------------------------------------
    // Shortest Job First helper it used to select the next process
    // ------------------------------------------------------------
    private PCB selectSJFProcess() {

        PCB shortestProcess = null;
        int i = 0;

        while (i < readyQueue.length) {

            PCB process = readyQueue[i];

            if (process != null) {
                if (shortestProcess == null) {
                    shortestProcess = process;
                } else {
                    if (process.getBrustTime() < shortestProcess.getBrustTime()) {
                        shortestProcess = process;
                    }
                }
            }

            i++;
        }

        return shortestProcess;
    }


    // ------------------------------------------------------------
    // REMOVE FINISHED PROCESS FROM READY QUEUE
    // ------------------------------------------------------------
    private void removeFromReadyQueue(PCB targetProcess) {
        int i = 0;
        while (i < readyQueue.length) {
            if (readyQueue[i] == targetProcess) {
                readyQueue[i] = null;
            }
            i++;
        }
    }
     public void runSJF() {

        System.out.println("=== SJF Scheduling Start ===");

        PCB currentProcess = selectSJFProcess();

        while (currentProcess != null) {

            System.out.println("Running P" + currentProcess.getPID() +
                    " (Burst=" + currentProcess.getBrustTime() + ")");

            // Simulate finishing (no threads yet, so instant)
            currentProcess.state = "finished";
            // Free up memory used by the process
            loader.totalRamUsed -= currentProcess.getSize();
            // Remove the process from readyQueue
            removeFromReadyQueue(currentProcess);
            // Update readyQueue by loading new jobs from jobQueue
            readyQueue = loader.updateReadyQueue();
            // Select next shortest job
            currentProcess = selectSJFProcess();
        }
        System.out.println("--- Shortest Job First is Finished, No More Processes ---");
    }
    

}