<<<<<<< HEAD

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
=======

import java.util.List;

public class main {
    public static void main(String[] args) {
        String filePath = "job.txt";
        List<Procces> processes = Reader.readProcesses(filePath);
        Procces[] p = processes.toArray(new Procces[0]);
        ShortesJobFirst SJF= new ShortesJobFirst(p);
        
        for(int i=0;i<p.length;i++){
            System.out.print(i+":");
            System.out.println("PID:"+p[i].getPID()+"\n BrustTime:"+p[i].getBrustTime()+"\n Priority:"+p[i].getPriority()+"\n Size:"+p[i].getSize()+"\n StartTime:"+p[i].startTime+"\n FinishTime:"+p[i].finishTime+"\n WaitingTime:"+p[i].watingTime+"\n TurnAround:"+p[i].turnAround);
            System.out.println("-------------------------");
        }
        
        
    }
}
>>>>>>> 0eec6be (UpdateFiles)
