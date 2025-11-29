import java.util.*;
// Queue.java
public class Queue {
    private Node front;
    private Node rear;
    private int size;
    private static final int MAX_SIZE = 30;
    
    private class Node {
        PCB data;
        Node next;
        
        Node(PCB data) {
            this.data = data;
            this.next = null;
        }
    }
    
    public Queue() {
        this.front = null;
        this.rear = null;
        this.size = 0;
    }
    
    public boolean isEmpty() {
        return front == null;
    }
    
    public boolean isFull() {
        return size >= MAX_SIZE;
    }
    
    public int size() {
        return size;
    }
    
    public boolean enqueue(PCB pcb) {
        if (isFull() || pcb == null) {
            return false;
        }
        
        Node newNode = new Node(pcb);
        
        if (isEmpty()) {
            front = newNode;
            rear = newNode;
        } else {
            rear.next = newNode;
            rear = newNode;
        }
        size++;
        return true;
    }
    
    public PCB dequeue() {
        if (isEmpty()) {
            return null;
        }
        
        PCB data = front.data;
        front = front.next;
        
        if (front == null) {
            rear = null;
        }
        size--;
        return data;
    }
    
    public PCB peek() {
        if (isEmpty()) {
            return null;
        }
        return front.data;
    }
    
    public void printQueue() {
        if (isEmpty()) {
            System.out.println("Queue is empty");
            return;
        }
        
        Node current = front;
        System.out.print("Queue: ");
        while (current != null) {
            System.out.print("P" + current.data.getPID() + " ");
            current = current.next;
        }
        System.out.println();
    }
    
    public List<PCB> toList() {
        List<PCB> list = new ArrayList<>();
        Node current = front;
        while (current != null) {
            list.add(current.data);
            current = current.next;
        }
        return list;
    }
}