public class Procces {
    private int PID;
    private int BrustTime;
    private int Priority;
    private int Size;

    public Procces(int pid,int BrustTime,int Priority,int Size){
        this.PID=pid;
        this.BrustTime=BrustTime;
        this.Priority=Priority;
        this.Size=Size;
    }
    public Procces(){

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
