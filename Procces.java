public class Procces {
    private int PID;
    private int BrustTime;
    private int Priority;
    private int Size;
    public int watingTime;
    public int turnAround;
    public int startTime;
    public int finishTime;
    public int ArivalTime;
    public Procces(int pid,int BrustTime,int Priority,int Size){
        this.PID=pid;
        this.BrustTime=BrustTime;
        this.Priority=Priority;
        this.Size=Size;
        watingTime=0;
        turnAround=0;
        startTime=0;
        finishTime=0;
        ArivalTime=0;
    }
    public Procces(){
        BrustTime=-1;
        PID=-1;
        Priority=-1;
        Size=-1;
        watingTime=-1;
        turnAround=-1;
        startTime=-1;
        finishTime=-1;
        ArivalTime=-1;
    }
    public int getPID() {
        return PID;
    }
    public void setPID(int PID) {
        this.PID = PID;
    }
    public int getBrustTime() {
        return BrustTime;
    }
    public void setBrustTime(int BrustTime) {
        this.BrustTime = BrustTime;
    }
    public int getPriority() {
        return Priority;
    }
    public void setPriority(int Priority) {
        this.Priority = Priority;
    }
    public int getSize() {
        return Size;
    }
    public void setSize(int Size) {
        this.Size = Size;
    }
    
    
}
