public class PCB {
    private int PID;
    private int BrustTime;
    private int originalBrustTime;
    private int Priority;
    private int originalPriority;
    private int Size;
    public String state;
    private int turnaroundTime;
    private int waitingTime;
    private int readyQueueArrivalTime;
    private int degreeOfMultiprogramming;
    private int processesExecutedSinceArrival;
    private boolean hasStarved;
    private int completionTime;
    
    public PCB(int pid, int BrustTime, int Priority, int Size){
        this.PID = pid;
        this.BrustTime = BrustTime;
        this.originalBrustTime = BrustTime;
        this.Priority = Priority;
        this.originalPriority = Priority;
        this.Size = Size;
        this.turnaroundTime = -1;
        this.waitingTime = -1;
        this.readyQueueArrivalTime = 0;
        this.degreeOfMultiprogramming = 0;
        this.processesExecutedSinceArrival = 0;
        this.hasStarved = false;
        this.completionTime = 0;
        
        state = "new";
    }

    public PCB(){}

    public int getPID() { return PID; }
    public void setPID(int PID) { this.PID = PID; }

    public int getBrustTime() { return BrustTime; }
    public void setBrustTime(int BrustTime) { 
        this.BrustTime = BrustTime; 
    }

    public int getOriginalBrustTime() { return originalBrustTime; }

    public int getPriority() { return Priority; }
    public void setPriority(int Priority) { this.Priority = Priority; }
    
    public int getOriginalPriority() { return originalPriority; }

    public int getSize() { return Size; }
    public void setSize(int Size) { this.Size = Size; }

    public int getTurnaroundTime() { return turnaroundTime; }
    public void setTurnaroundTime(int t) { turnaroundTime=t; }
    
    public int getWaitingTime() { return waitingTime; }
    public void setWaitingTime(int t) { waitingTime=t; }
    
    public void setState(String s) {state=s;}
    
    public int getReadyQueueArrivalTime() { return readyQueueArrivalTime; }
    public void setReadyQueueArrivalTime(int time) { this.readyQueueArrivalTime = time; }
    
    public int getDegreeOfMultiprogramming() { return degreeOfMultiprogramming; }
    public void setDegreeOfMultiprogramming(int degree) { this.degreeOfMultiprogramming = degree; }
    
    public int getProcessesExecutedSinceArrival() { return processesExecutedSinceArrival; }
    public void setProcessesExecutedSinceArrival(int count) { this.processesExecutedSinceArrival = count; }
    public void incrementProcessesExecutedSinceArrival() { this.processesExecutedSinceArrival++; }
    
    public boolean hasStarved() { return hasStarved; }
    public void setHasStarved(boolean starved) { this.hasStarved = starved; }
    
    public int getCompletionTime() { return completionTime; }
    public void setCompletionTime(int time) { this.completionTime = time; }

    public PCB deepCopy() {
        PCB copy = new PCB(PID, originalBrustTime, originalPriority, Size);
        copy.state = "new";
        return copy;
    }
    
    public PCB copyPCB() {
        PCB b = new PCB(PID, BrustTime, Priority, Size);
        b.setState(state);
        b.setTurnaroundTime(turnaroundTime);
        b.setWaitingTime(waitingTime);
        b.setReadyQueueArrivalTime(readyQueueArrivalTime);
        b.setDegreeOfMultiprogramming(degreeOfMultiprogramming);
        b.setProcessesExecutedSinceArrival(processesExecutedSinceArrival);
        b.setHasStarved(hasStarved);
        return b;
    }

    
    public String toString() {
        return "PCB [PID=" + PID + ", BrustTime=" + BrustTime + ", Priority=" + Priority + 
               ", Size=" + Size + ", state=" + state + ", turnaroundTime=" + turnaroundTime + 
               ", waitingTime=" + waitingTime + ", starved=" + hasStarved + "]";
    }
}
