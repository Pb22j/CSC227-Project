
import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;

public final class ShortesJobFirst {
    private Procces []processes;
    private int mainMemorySize;
    private boolean fit;
    public int totalbrustTime;
    

    public ShortesJobFirst(Procces []p) {
        processes=p;
        mainMemorySize = 2048;
        fit=isFit();
        totalbrustTime=0;

        
        int []Shortest =findShortestIndex(processes);
        //System.out.println("Shortest PID");
        //for(int i=0;i<Shortest.length;i++)
          //  System.out.println("PID:"+Shortest[i]);
          //System.out.println(processes[1].startTime+" b");
        for(int i=0;i<processes.length;i++){
            int a=Shortest[i];
            processes[a].startTime=totalbrustTime;
            processes[a].watingTime=totalbrustTime;
            totalbrustTime=totalbrustTime+processes[a].getBrustTime();
            processes[a].finishTime = processes[a].startTime + processes[a].getBrustTime();
            processes[a].turnAround = processes[a].finishTime - 0;
            
        }
    }
    public ShortesJobFirst(){}
    public boolean isFit() {
        int totalSize = 0;
        for(int i=0;i<processes.length;i++){
            totalSize=+processes[i].getBrustTime();
        }
        if(totalSize>mainMemorySize)
            return false;
        else
            return false;
    }


    public int[] findShortestIndex(Procces job[]){
        int[] ShortesArr=new int[job.length];
        Procces[] copyJobs= new Procces[job.length];
        for(int i=0;i<job.length;i++){
            copyJobs[i]=new Procces();
        }
        for(int i=0;i<job.length;i++){
            
            copyJobs[i].setBrustTime(job[i].getBrustTime());
            copyJobs[i].setPID(job[i].getPID());
            copyJobs[i].setPriority(job[i].getPriority());
            copyJobs[i].setSize(job[i].getSize());
            copyJobs[i].finishTime=0;
            copyJobs[i].startTime=0;
            copyJobs[i].turnAround=0;
            copyJobs[i].watingTime=0;
            
        }
        for(int i=0;i<job.length;i++){
            int shortes=9999999;
            int shortesIndex=-1;
            for(int j=0;j<copyJobs.length;j++){
                if((copyJobs[j].getBrustTime()<shortes)&&copyJobs[j].getPID()!=-1){
                    shortes=copyJobs[j].getBrustTime();
                    shortesIndex=j;
                }
            }
            ShortesArr[i]=shortesIndex;
            copyJobs[shortesIndex].setPID(-1);
        }
       
        return ShortesArr;
    }
}
