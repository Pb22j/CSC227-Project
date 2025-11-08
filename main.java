
import java.util.List;

public class main {
    public static void main(String[] args) {
        String filePath = "job.txt";
        List<Procces> processes = Reader.readProcesses(filePath);
        for (Procces p : processes) {
            System.out.println("PID: " + p.getPID() + ", BrustTime: " + p.getBrustTime() +
                               ", Priority: " + p.getPriority() + ", Size: " + p.getSize());
        }

    }
}
