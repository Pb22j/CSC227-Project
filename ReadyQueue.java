// ReadyQueue.java
import java.util.PriorityQueue;

public class ReadyQueue {
    
    Queue readyQueue;
    PriorityQueue<PCB> PQ;
    private final int memorySize = 2048;
    private int availableMemory;
    private boolean lock;
    
    public ReadyQueue() {
        readyQueue = new Queue();
        availableMemory = 2048;
        lock = false;
        PQ = new PriorityQueue<>(new PCBComparator());
    }
    
    public boolean enqueue(PCB p) {
        if (p == null || p.getSize() > availableMemory || readyQueue.isFull())
            return false;
        availableMemory -= p.getSize();
        p.setState("ready");
        readyQueue.enqueue(p);
        return true;
    }
    
    public PCB dequeue() {
        if (readyQueue.isEmpty())
            return null;
        PCB removedPCB = readyQueue.dequeue();
        availableMemory += removedPCB.getSize();
        return removedPCB;
    }
    
    public PCB peek() {
        if (readyQueue.isEmpty())
            return null;
        return readyQueue.peek();
    }
    
    public void printReadyQueue() {
        if (readyQueue.isEmpty())
            return;
        readyQueue.printQueue();
    }
    
    public boolean isLock() {
        return lock;
    }
    
    public void setLock(boolean lock) {
        this.lock = lock;
    }
    
    public int size() {
        return readyQueue.size();
    }
    
    public boolean isEmpty() {
        return readyQueue.isEmpty();
    }
    
    public int getAvailableMemory() {
        return availableMemory;
    }
    
    public void orderingReadyQueue(String type) {
        PQ = new PriorityQueue<>(new PCBComparator(type));
        
        while (!readyQueue.isEmpty()) {
            PQ.add(readyQueue.dequeue()); 
        }
        
        while (!PQ.isEmpty()) {
            readyQueue.enqueue(PQ.poll());
        }
    }
}