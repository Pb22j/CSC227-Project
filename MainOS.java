
public class MainOS {
    public static void main(String[] args) {

        String filePath = "job.txt";
        JobLoader loader = new JobLoader("job.txt", 2048);

        CPUScheduler CpuScheduler = new CPUScheduler(loader);

        
        CpuScheduler.runSJF();
    }
}
 
        