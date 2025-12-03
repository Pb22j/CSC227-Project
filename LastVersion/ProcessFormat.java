
public class ProcessFormat {
    private int PID;
    private int startTime;
    private int endTime;
    private int burstExecuted;
    private String state;
    private int waitingTime;
    private int turnaroundTime;
    private boolean starved;
    
    public ProcessFormat(PCB pcb, int startTime, int endTime, int burstExecuted) {
        this.PID = pcb.getPID();
        this.startTime = startTime;
        this.endTime = endTime;
        this.burstExecuted = burstExecuted;
        this.state = pcb.state;
        this.waitingTime = pcb.getWaitingTime();
        this.turnaroundTime = pcb.getTurnaroundTime();
        this.starved = pcb.hasStarved();
    }
    
    public int getPID() { return PID; }
    public int getStartTime() { return startTime; }
    public int getEndTime() { return endTime; }
    public int getBurstExecuted() { return burstExecuted; }
    public String getState() { return state; }
    public int getWaitingTime() { return waitingTime; }
    public int getTurnaroundTime() { return turnaroundTime; }
    public boolean isStarved() { return starved; }
    
    
    public String toString() {
        return "P" + PID + " [" + startTime + "-" + endTime + "] (burst: " + burstExecuted + ")";
    }
}