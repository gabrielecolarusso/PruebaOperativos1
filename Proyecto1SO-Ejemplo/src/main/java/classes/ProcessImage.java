/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package classes;
import primitivas.*;
/**
 *
 * @author DELL
 */
public class ProcessImage {
    private List instructions; //lista para saber cuales generan excepciones
    private String type;
    private int id;
    private String status;
    private String name;
    private int programCounter;
    private int memoryAddressRegister;
    private int duration;
    private int quantum;
    private int waitingTime;

    public int getId() {
        return id;
    }

    public ProcessImage(List instructions, String type, int id, String status, String name, int programCounter, int memoryAddressRegister, int duration) {
        this.instructions = instructions;
        this.type = type;
        this.id = id;
        this.status = status;
        this.name = name;
        this.programCounter = programCounter;
        this.memoryAddressRegister = memoryAddressRegister;
        this.duration = duration;
        this.waitingTime = 0;
    }
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public void setId(int id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getProgramCounter() {
        return programCounter;
    }

    public void setProgramCounter(int programCounter) {
        this.programCounter = programCounter;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public void setWaitingTime(int waitingTime) {
        this.waitingTime = waitingTime;
    }

    public int getMemoryAddressRegister() {
        return memoryAddressRegister;
    }

    public void setMemoryAddressRegister(int memoryAddressRegister) {
        this.memoryAddressRegister = memoryAddressRegister;
    }
    
    /**
     *
     * @return
     */
    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public int getQuantum() {
        return quantum;
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }
    
    public List getInstructions() {
        return instructions;
    }

    public void setInstructions(List<Integer> instructions) {
        this.instructions = (instructions != null) ? instructions : new List<>();
    }
}
