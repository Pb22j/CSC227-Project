import java.io.*;
import java.util.*;

public class JobLoader {

    public PCB[] JobQueue;      
    public int memorySize;
    public PCB[] readyQueue;    
    public int totalRamUsed;

    public JobLoader() {}

    public JobLoader(String filePath, int RamSize) {

        // 1. Read all processes
        PCB[] processList = readProcesses(filePath);

        this.memorySize = RamSize;
        this.totalRamUsed = 0;

        // 2. Allocate JobQueue
        JobQueue = new PCB[processList.length];

        // Copy processes into JobQueue
        for (int i = 0; i < processList.length; i++) {
            JobQueue[i] = processList[i];
        }

        // 3. Allocate readyQueue with same max size
        readyQueue = new PCB[JobQueue.length];

        int readyIndex = 0;

        // 4. Load jobs that fit in RAM
        for (int i = 0; i < JobQueue.length; i++) {

            PCB p = JobQueue[i];
            if (p == null) continue;

            if (totalRamUsed + p.getSize() <= memorySize) {

                readyQueue[readyIndex] = p;
                readyQueue[readyIndex].state = "ready";
                readyIndex++;

                totalRamUsed += p.getSize();

                JobQueue[i] = null; // Remove from JobQueue
            }
        }
    }


    // ---------------------------------------------------------------
    // UPDATE READY QUEUE (LOAD REMAINING JOBS WHEN RAM IS FREED)
    // ---------------------------------------------------------------
    public PCB[] updateReadyQueue() {

        int tRam = 0;

        // 1. Count RAM used in readyQueue
        for (int i = 0; i < readyQueue.length; i++) {
            if (readyQueue[i] != null) {
                tRam += readyQueue[i].getSize();
            }
        }

        // 2. Find first empty slot in readyQueue
        int k = 0;
        while (k < readyQueue.length && readyQueue[k] != null) {
            k++;
        }

        // 3. Move jobs from JobQueue if they fit
        for (int j = 0; j < JobQueue.length; j++) {

            PCB p = JobQueue[j];
            if (p == null) continue;

            if (p.getSize() + tRam <= memorySize) {

                if (k >= readyQueue.length) break;

                readyQueue[k] = p;
                readyQueue[k].state = "ready";
                tRam += p.getSize();

                JobQueue[j] = null; // remove job

                k++;
            }
        }

        return readyQueue;
    }


    // ---------------------------------------------------------------
    // READ PROCESSES FROM FILE
    // ---------------------------------------------------------------
    public static PCB[] readProcesses(String filePath) {

        List<PCB> list = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {

            String line;

            while ((line = br.readLine()) != null) {

                line = line.trim();
                if (line.isEmpty()) continue;

                // Format: PID:BrustTime:Priority;Size
                String[] parts = line.split("[:;]");

                if (parts.length == 4) {
                    try {
                        PCB p = new PCB();
                        p.setPID(Integer.parseInt(parts[0].trim()));
                        p.setBrustTime(Integer.parseInt(parts[1].trim()));
                        p.setPriority(Integer.parseInt(parts[2].trim()));
                        p.setSize(Integer.parseInt(parts[3].trim()));

                        list.add(p);

                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number in line: " + line);
                    }
                } 
                else {
                    System.err.println("Invalid format in line: " + line);
                }
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }

        return list.toArray(new PCB[0]);
    }
}
