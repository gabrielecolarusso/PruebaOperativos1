 /*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package MainPackage;

import EDD.Lista;
import MainClasses.CPU;
import MainClasses.Proceso;
import MainClasses.Scheduller;
import java.util.concurrent.Semaphore;
import GUI.Home;

/**
 *
 * @author pedro
 */
public class Main {
    public static Semaphore semaforo = new Semaphore(1);
    public static Lista<Proceso> colaListos = new Lista<>();
    public static Lista<Proceso> colaBloqueados = new Lista<>();
    public static Lista<Proceso> colaTerminados = new Lista<>();
    public static CPU[] cpus = new CPU[2]; 
    public static int cicloGlobal = 0;
    public static int politicaActual = 1;
    public static int cicloDuration = 3000; // 3 segundos por ciclo
    public static Scheduller scheduler = new Scheduller(5, colaListos, colaBloqueados, colaTerminados);
    public static boolean cambioRealizado = false;

    public static void main(String[] args) throws InterruptedException {

        Home h = new Home();
        h.setVisible(true);
        crearProcesosPrueba();
        

//        crearProcesosPrueba();
        System.out.println(colaListos.recorrer());
        scheduler.ejecutarPlanificacion(politicaActual);
        System.out.println(colaListos.recorrer());

        // Inicializar CPUs
        cpus[0] = new CPU(1, true);
        cpus[1] = new CPU(2, true);
        
//        for (CPU cpu : cpus) {
//            if (cpu != null) cpu.start();
//        }

//        while (true) {
//
//
////             Cambiar política a SJF en el ciclo 6
////            if (cicloGlobal == 6 && !cambioRealizado) {
////                cambiarPolitica(2); // SJF
////                cambioRealizado = true;
////                System.out.println("\n=== POLÍTICA CAMBIADA A RR ===");
////            }
////            
////            imprimirEstado();
////            cicloGlobal++;
////            Thread.sleep(cicloDuration);
//        }
    }

    private static void crearProcesosPrueba() {
        crearProceso("CPU-1", 10, false, 0, 0);
        crearProceso("CPU-2", 8, false, 0, 0);
        crearProceso("IO-1", 6, true, 2, 3);
        crearProceso("IO-2", 5, true, 3, 2);
    }
    
    private static void cambiarPolitica(int nuevaPolitica) {
        try {
            semaforo.acquire();
            politicaActual = nuevaPolitica;
            scheduler.ejecutarPlanificacion(nuevaPolitica);
            semaforo.release();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static void crearProceso(String nombre, int tiempo, boolean isIO, 
                                   int ciclosExcepcion, int duracionExcepcion) {
        Proceso p = new Proceso(
            colaListos.getSize() + 1,
            nombre,
            "Ready",
            0,
            tiempo,
            tiempo,
            !isIO,
            isIO,
            0,
            0,
            cicloGlobal
        );
        
        if (isIO) {
            p.setCiclosParaExcepcion(ciclosExcepcion);
            p.setExceptionDuration(duracionExcepcion);
        }
        
        colaListos.agregar(p);
    }

    private static void imprimirEstado() {
        System.out.println("\n=== Ciclo " + cicloGlobal + " (" + (cicloGlobal * 3) + "s) ===");
        System.out.println("Cola de Listos: " + colaListos.recorrer());
        System.out.println("Cola Bloqueados: " + colaBloqueados.recorrer());
        System.out.println("Cola Terminados: " + colaTerminados.recorrer());
        
        for (CPU cpu : cpus) {
            if (cpu != null) {
                String estado = (cpu.getProceso() != null) ? 
                    cpu.getProceso().getName() : "System";
                System.out.println("CPU " + cpu.getCPUid() + ": " + estado);
            }
        }
    }
    
    public static Scheduller getScheduler() {
        return scheduler;
    }
}