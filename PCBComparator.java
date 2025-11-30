
import java.util.Comparator;

public class PCBComparator implements Comparator<PCB> {
    private String type; // "priority", "sjf", or "pid"
    
    public PCBComparator() {
        this.type = "priority";
    }
    
    public PCBComparator(String type) {
        this.type = type;
    }
    
    @Override
    public int compare(PCB p1, PCB p2) {
        if (type.equals("sjf")) {
            // Shortest Job First - lower burst time first
            int burstCompare = Integer.compare(p1.getBrustTime(), p2.getBrustTime());
            if (burstCompare != 0) return burstCompare;
            return Integer.compare(p1.getPID(), p2.getPID()); // Tie-breaker by PID
        } else if (type.equals("priority")) {
            // Priority - higher priority value first (128 is highest)
            int priorityCompare = Integer.compare(p2.getPriority(), p1.getPriority());
            if (priorityCompare != 0) return priorityCompare;
            return Integer.compare(p1.getPID(), p2.getPID()); // Tie-breaker by PID
        } else {
            // Default: sort by PID
            return Integer.compare(p1.getPID(), p2.getPID());
        }
    }
}