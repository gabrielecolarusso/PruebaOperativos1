/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MainClasses;

/**
 *
 * @author pedro
 */
public class Proceso {
    private int id; // Identificador único del proceso
    private String name; // Nombre del proceso
    private String status; // Estado del proceso (Ready, Running, Blocked, Completed)
    private int cpu; // CPU asignado
    private int time; // Tiempo total de ejecución
    private int remainingTime; // Tiempo restante para completar el proceso
    private boolean cpubound; //Se verifica si es CPUBound
    private boolean iobound; //Se verifica si es iobound
    private int MAR; 
    private int PC; //Cantidad de programas se inicializa en 0
    private int Llegada;
    private int ExceptionDuration;
    private int CiclosParaExcepcion;

    public int getExceptionDuration() {
        return ExceptionDuration;
    }

    public void setExceptionDuration(int ExceptionDuration) {
        this.ExceptionDuration = ExceptionDuration;
    }

    public int getCiclosParaExcepcion() {
        return CiclosParaExcepcion;
    }

    public void setCiclosParaExcepcion(int CiclosParaExcepcion) {
        this.CiclosParaExcepcion = CiclosParaExcepcion;
    }
    
    public Proceso(int id, String name, String status, int cpu, int time, int remainingTime, boolean cpubound, boolean iobound, int MAR, int PC, int Llegada){
        this.id = id;
        this.name = name;
        this.status = status;
        this.cpu = cpu;
        this.time = time;
        this.remainingTime = remainingTime;
        this.cpubound = cpubound;
        this.iobound = iobound;
        this.MAR = 0;
        this.PC = 1;
        this.Llegada = Llegada;
    }
    
    public void TipoProceso(boolean iobound){
        int tiempointerrupcion = 0;
        if (iobound){
            ProcesoIO(tiempointerrupcion);
        }
    }

    public int getLlegada() {
        return Llegada;
    }

    public void setLlegada(int Llegada) {
        this.Llegada = Llegada;
    }
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * @return the cpu
     */
    public int getCpu() {
        return cpu;
    }

    /**
     * @param cpu the cpu to set
     */
    public void setCpu(int cpu) {
        this.cpu = cpu;
    }

    /**
     * @return the time
     */
    public int getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(int time) {
        this.time = time;
    }

    /**
     * @return the remainingTime
     */
    public int getRemainingTime() {
        return remainingTime;
    }

    /**
     * @param remainingTime the remainingTime to set
     */
    public void setRemainingTime(int remainingTime) {
        this.remainingTime = remainingTime;
    }

    /**
     * @return the cpubound
     */
    public boolean isCpubound() {
        return cpubound;
    }

    /**
     * @param cpubound the cpubound to set
     */
    public void setCpubound(boolean cpubound) {
        this.cpubound = cpubound;
    }

    /**
     * @return the iobound
     */
    public boolean isIobound() {
        return iobound;
    }

    /**
     * @param iobound the iobound to set
     */
    public void setIobound(boolean iobound) {
        this.iobound = iobound;
    }

    /**
     * @return the MAR
     */
    public int getMAR() {
        return MAR;
    }

    /**
     * @param MAR the MAR to set
     */
    public void setMAR(int MAR) {
        this.MAR = MAR;
    }

    /**
     * @return the PC
     */
    public int getPC() {
        return PC;
    }

    /**
     * @param PC the PC to set
     */
    public void setPC(int PC) {
        this.PC = PC;
    }

    public Integer ProcesoIO(int interruption){
        int tiempo = 0;
        if(interruption == 0){
            System.out.println("Error no se puede tener un tiempo de interrupcion de 0");
        }else{
            tiempo += interruption;
        }
        return tiempo;
    }
    
    @Override
    public String toString() {
        return "ID: " + this.id + "\n" +
               " | STATUS: " + this.status + "\n" +
               " | Nombre: " + this.name + "\n" +
               " | PC: " + this.PC + "\n" +
               " | MAR: " + this.MAR;
    }
}