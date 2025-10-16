/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MainPackage;
import MainClasses.Proceso;
import EDD.Lista;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 *
 * @author pedro
 */



public class SimulationConfig {
    private int cicloDuration;
    private int numCPUs;
    private Lista<ProcesoConfig> procesos;

    // Getters y Setters
    public int getCicloDuration() { return cicloDuration; }
    public void setCicloDuration(int cicloDuration) { this.cicloDuration = cicloDuration; }
    
    public int getNumCPUs() { return numCPUs; }
    public void setNumCPUs(int numCPUs) { this.numCPUs = numCPUs; }
    
    public Lista<ProcesoConfig> getProcesos() { return procesos; }
    public void setProcesos(Lista<ProcesoConfig> procesos) { this.procesos = procesos; }
}


//public class Lector {
//    public static void cargarCSV(String ruta, Lista<Proceso> colaListos) {
//        try (BufferedReader br = new BufferedReader(new FileReader(ruta))) {
//            String linea;
//            boolean primeraLinea = true;
//            
//            while ((linea = br.readLine()) != null) {
//                if (primeraLinea) { // Ignorar cabecera
//                    primeraLinea = false;
//                    continue;
//                }
//                Proceso proceso = parsearLineaCSV(linea);
//                if (proceso != null) {
//                    colaListos.agregar(proceso);
//                }
//            }
//        } catch (IOException e) {
//            System.err.println("Error al leer el CSV: " + e.getMessage());
//        }
//    }

    // Convierte una línea del CSV en un objeto Proceso
//    private static Proceso parsearLineaCSV(String linea) {
//        String[] campos = linea.split(",");
//        
//        try {
//            String nombre = campos[0].trim();
//            int tiempo = Integer.parseInt(campos[1].trim());
//            boolean esIO = Boolean.parseBoolean(campos[2].trim());
//            int ciclosExcepcion = Integer.parseInt(campos[3].trim());
//            int duracionExcepcion = Integer.parseInt(campos[4].trim());
//            
//            Proceso p = new Proceso();
//            p.setName(nombre);
//            p.setTime(tiempo);
//            p.setIobound(esIO);
//            p.setCiclosParaExcepcion(ciclosExcepcion);
//            p.setExceptionDuration(duracionExcepcion);
//            
//            return p;
//            
//        } catch (Exception e) {
//            System.err.println("Error al parsear línea: " + linea);
//            return null;
//        }
//    }
//
//    // Guarda la configuración actual y procesos en un CSV
//    public static void guardarCSV(String ruta, int cicloDuration, int numProcesadores, Lista<Proceso> procesos) throws IOException {
//        try (FileWriter writer = new FileWriter(ruta)) {
//            // Cabecera
//            writer.write("Nombre,Instrucciones,EsIO,CiclosExcepcion,DuracionExcepcion\n");
//            
//            // Configuración general (podrías añadir más campos)
//            writer.write("#Config," + cicloDuration + "," + numProcesadores + "\n");
//            
//            // Procesos
//            for (Proceso p : procesos) {
//                writer.write(
//                    p.getName() + "," +
//                    p.getTime() + "," +
//                    p.isIobound() + "," +
//                    p.getCiclosParaExcepcion() + "," +
//                    p.getExceptionDuration() + "\n"
//                );
//            }   
//        } catch (IOException e) {
//            System.err.println("Error al guardar CSV: " + e.getMessage());
//        }
//    }
//}
