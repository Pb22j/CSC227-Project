import java.io.*;

public class JobLoaderThread implements Runnable {

    public Queue JobQueue;
    public String filePath;
    
    public JobLoaderThread(String filePath) {
        this.filePath = filePath;
        JobQueue = new Queue();
    }

    public void run() {
        try (BufferedReader br = new BufferedReader(new FileReader(this.filePath))) {
            String line;

            while ((line = br.readLine()) != null) {
                line = line.trim(); //!
                if (line.isEmpty()) continue;

                // Format: PID:BurstTime:Priority;Size
                String[] parts = line.split("[:;]");

                if (parts.length == 4) {
                    try {
                        PCB process = new PCB(
                            Integer.parseInt(parts[0].trim()),  
                            Integer.parseInt(parts[1].trim()), 
                            Integer.parseInt(parts[2].trim()), 
                            Integer.parseInt(parts[3].trim())
                        );
                        JobQueue.enqueue(process);
                        System.out.println("Loaded job: P" + process.getPID());
                    } catch (NumberFormatException e) {
                        System.err.println("Invalid number in line: " + line);
                    }
                } else {
                    System.err.println("Invalid format in line: " + line);
                }
            }
            System.out.println("Job loading complete. Total jobs: " + JobQueue.size());

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    public Queue getJobQueue() {
        return JobQueue;
    }
}