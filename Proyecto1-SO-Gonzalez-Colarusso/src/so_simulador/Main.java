// src/so_simulador/MainUnificado.java
package so_simulador;

import so_simulador.modelo.*;
import so_simulador.planificador.*;
import so_simulador.util.Metricas;
import so_simulador.config.*;
import so_simulador.memoria.*;

/**
 * Main unificado que integra:
 * - Configuración persistente (JSON)
 * - Gestión de memoria y estados suspendidos
 * - 6 algoritmos de planificación
 * - Métricas completas
 * - Manejo de E/S y bloqueos
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   SIMULADOR DE SISTEMAS OPERATIVOS     ║");
        System.out.println("║          Versión Unificada 1.0         ║");
        System.out.println("╚════════════════════════════════════════╝\n");
        
        // ========== FASE 1: CARGAR CONFIGURACIÓN ==========
        ConfiguracionSimulacion config = cargarConfiguracion();
        System.out.println("✓ Configuración cargada:");
        System.out.println("  - Duración de ciclo: " + config.getCicloDuracion() + "ms");
        System.out.println("  - Memoria total: " + config.getMemoriaTotal() + "KB");
        System.out.println("  - Memoria por proceso: " + config.getMemoriaPorProceso() + "KB");
        System.out.println("  - Algoritmo inicial: " + config.getAlgoritmoInicial());
        System.out.println("  - Procesos configurados: " + config.contarProcesos() + "\n");
        
        // ========== FASE 2: INICIALIZAR MEMORIA Y COLAS ==========
        GestorMemoria gestorMemoria = new GestorMemoria(
            config.getMemoriaTotal(), 
            config.getMemoriaPorProceso()
        );
        ColasMultinivel colas = new ColasMultinivel(gestorMemoria);
        
        System.out.println("✓ Sistema de memoria inicializado:");
        System.out.println("  " + gestorMemoria.getEstadisticas());
        System.out.println("  Capacidad máxima: " + gestorMemoria.getMaxProcesos() + " procesos en RAM\n");
        
        // ========== FASE 3: CREAR PROCESOS DESDE CONFIGURACIÓN ==========
        System.out.println("--- Admitiendo procesos al sistema ---");
        int procesosAdmitidos = 0;
        
        for (ProcesoConfig pc : config.getProcesos()) {
            if (pc != null) {
                Proceso p = new Proceso(
                    pc.getNombre(),
                    pc.getInstrucciones(),
                    pc.isEsCPUbound(),
                    pc.getCiclosExcepcion(),
                    pc.getCiclosAtencion(),
                    pc.getPrioridad()
                );
                
                colas.admitirProceso(p);
                procesosAdmitidos++;
                System.out.println("✓ " + p.getPCB().getNombre() + 
                                 " (" + (p.isCPUbound() ? "CPU-bound" : "I/O-bound") + 
                                 ", " + p.getInstruccionesTotales() + " inst.)");
            }
        }
        
        System.out.println("\nTotal admitidos: " + procesosAdmitidos);
        System.out.println(colas.getResumen() + "\n");
        
        // ========== FASE 4: CONFIGURAR PLANIFICADOR ==========
        GestorPlanificadores gestorPlanificadores = new GestorPlanificadores();
        gestorPlanificadores.registrar(new FCFS());
        gestorPlanificadores.registrar(new SJF());
        gestorPlanificadores.registrar(new RoundRobin(2));
        gestorPlanificadores.registrar(new Prioridades());
        gestorPlanificadores.registrar(new HRRN());
        
        int[] quantums = {1, 2, 4};
        MLFQ mlfq = new MLFQ(3, quantums, 5);
        gestorPlanificadores.registrar(mlfq);
        
        // Seleccionar algoritmo según configuración
        seleccionarAlgoritmo(gestorPlanificadores, config.getAlgoritmoInicial());
        
        Planificador planificador = gestorPlanificadores.getActivo();
        System.out.println("\n✓ Planificador activo: " + planificador.getNombre() + "\n");
        
        // ========== FASE 5: INICIAR SIMULACIÓN ==========
        System.out.println("═══════════════════════════════════════");
        System.out.println("      INICIANDO SIMULACIÓN");
        System.out.println("═══════════════════════════════════════\n");
        
        Metricas metricas = new Metricas();
        CPU cpu = new CPU();
        int ciclo = 0;
        ColaProcesos colaTerminados = new ColaProcesos();
        
        // ========== FASE 6: BUCLE PRINCIPAL DE SIMULACIÓN ==========
        
        // Planificación de largo plazo inicial
        int cargados = colas.planificarLargoPlazo(ciclo);
        System.out.println("[Ciclo " + ciclo + "] Planificador de largo plazo: " + 
                         cargados + " procesos cargados en memoria");
        
        // Registrar llegada de procesos cargados
        ColaProcesos.ColaTemporal itInicial = colas.getColaCortoPlazo().crearIterador();
        while (itInicial.tieneSiguiente()) {
            Proceso p = itInicial.siguiente();
            metricas.registrarLlegada(p, ciclo);
        }
        
        // BUCLE PRINCIPAL (maneja tanto MLFQ como otros algoritmos)
        if (planificador instanceof MLFQ) {
            ejecutarConMLFQ((MLFQ) planificador, colas, metricas, ciclo, gestorMemoria);
        } else {
            ejecutarConAlgoritmoGeneral(planificador, colas, cpu, metricas, ciclo, 
                                      colaTerminados, gestorMemoria, config);
        }
        
        // ========== FASE 7: RESULTADOS FINALES ==========
        System.out.println("\n═══════════════════════════════════════");
        System.out.println("      SIMULACIÓN FINALIZADA");
        System.out.println("═══════════════════════════════════════\n");
        
        System.out.println("--- Procesos Terminados ---");
        ColaProcesos.ColaTemporal itFinal = colaTerminados.crearIterador();
        while (itFinal.tieneSiguiente()) {
            System.out.println(itFinal.siguiente().getPCB());
        }
        
        System.out.println("\n" + colas.getResumen());
        System.out.println(gestorMemoria.getEstadisticas());
        
        metricas.imprimirReporte();
        
        System.out.println("\n✓ Simulación completada exitosamente");
    }
    
    // ==================== MÉTODOS AUXILIARES ====================
    
    /**
     * Carga configuración desde archivo o crea una por defecto
     */
    private static ConfiguracionSimulacion cargarConfiguracion() {
        String archivoConfig = "simulacion_config.json";
        ConfiguracionSimulacion config = JSONHandler.cargar(archivoConfig);
        
        // Si no hay procesos, crear configuración por defecto
        if (config.contarProcesos() == 0) {
            System.out.println("⚠ No se encontró configuración, creando por defecto...\n");
            config = crearConfiguracionPorDefecto();
            JSONHandler.guardar(config, archivoConfig);
        }
        
        return config;
    }
    
    /**
     * Crea configuración por defecto con procesos de prueba
     */
    private static ConfiguracionSimulacion crearConfiguracionPorDefecto() {
        ConfiguracionSimulacion config = new ConfiguracionSimulacion();
        config.setCicloDuracion(1000);
        config.setMemoriaTotal(1024);
        config.setMemoriaPorProceso(128);
        config.setAlgoritmoInicial("FCFS");
        
        // Procesos variados
        config.agregarProceso(new ProcesoConfig("Proceso A", 10, false, 0, 3, 3, 128)); // I/O
        config.agregarProceso(new ProcesoConfig("Proceso B", 7, true, 0, 0, 1, 128));   // CPU
        config.agregarProceso(new ProcesoConfig("Proceso C", 8, false, 0, 4, 2, 128));  // I/O
        config.agregarProceso(new ProcesoConfig("Proceso D", 5, true, 0, 0, 4, 128));   // CPU
        config.agregarProceso(new ProcesoConfig("Proceso E", 12, false, 0, 3, 5, 128)); // I/O
        config.agregarProceso(new ProcesoConfig("Proceso F", 9, true, 0, 0, 2, 128));   // CPU
        
        return config;
    }
    
    /**
     * Selecciona algoritmo por nombre
     */
    private static void seleccionarAlgoritmo(GestorPlanificadores gestor, String nombre) {
        String[] nombres = {"FCFS", "SJF", "Round Robin", "Prioridades", "HRRN", "MLFQ"};
        
        for (int i = 0; i < nombres.length; i++) {
            if (nombre.toLowerCase().contains(nombres[i].toLowerCase().split(" ")[0])) {
                gestor.seleccionar(i + 1);
                return;
            }
        }
        
        // Por defecto FCFS
        gestor.seleccionar(1);
    }
    
    /**
     * Ejecuta simulación con MLFQ
     */
    private static void ejecutarConMLFQ(MLFQ mlfq, ColasMultinivel colas, 
                                       Metricas metricas, int ciclo, 
                                       GestorMemoria gestorMemoria) {
        
        // Mover procesos de corto plazo a MLFQ
        while (!colas.getColaCortoPlazo().estaVacia()) {
            Proceso p = colas.getColaCortoPlazo().desencolar();
            p.setMlfqNivel(0);
            mlfq.encolarProceso(p);
        }
        
        ColaProcesos colaBloqueadosLocal = new ColaProcesos();
        
        while (true) {
            // Planificación de largo y mediano plazo
            colas.planificarLargoPlazo(ciclo);
            colas.planificarMedianoPlazo(ciclo);
            
            // Mover nuevos procesos de corto plazo a MLFQ
            while (!colas.getColaCortoPlazo().estaVacia()) {
                Proceso p = colas.getColaCortoPlazo().desencolar();
                p.setMlfqNivel(0);
                mlfq.encolarProceso(p);
            }
            
            // Gestión de swapping si es necesario
            if (!gestorMemoria.hayEspacioDisponible() && !colas.getColaLargoPlazo().estaVacia()) {
                Proceso victima = colas.realizarSwap(ciclo);
                if (victima != null) {
                    System.out.println("[Ciclo " + ciclo + "] SWAP: " + 
                                     victima.getPCB().getNombre() + " → SUSPENDIDO");
                }
            }
            
            // Decrementar E/S de bloqueados
            ColaProcesos.ColaTemporal itBloq = colaBloqueadosLocal.crearIterador();
            while (itBloq.tieneSiguiente()) {
                Proceso bloq = itBloq.siguiente();
                bloq.decrementarContadorES();
                
                if (bloq.getContadorES() <= 0) {
                    colaBloqueadosLocal.eliminarProceso(bloq);
                    bloq.getPCB().setEstado(EstadoProceso.LISTO);
                    mlfq.encolarProceso(bloq);
                    System.out.println("[Ciclo " + ciclo + "] " + bloq.getPCB().getNombre() + 
                                     " → DESBLOQUEADO");
                }
            }
            
            mlfq.aging();
            Proceso siguiente = mlfq.seleccionarProceso(null);
            
            if (siguiente == null) {
                if (!colaBloqueadosLocal.estaVacia()) {
                    ciclo++;
                    continue;
                }
                break;
            }
            
            gestorMemoria.cargarProceso(siguiente, ciclo);
            metricas.registrarInicio(siguiente, ciclo);
            
            int q = mlfq.getQuantumParaNivel(siguiente.getMlfqNivel());
            boolean terminado = false;
            boolean bloqueado = false;
            int ejecutadas = 0;
            
            for (int i = 0; i < q && !terminado && !bloqueado; i++) {
                siguiente.getPCB().incrementarPC();
                siguiente.getPCB().incrementarMAR();
                ejecutadas++;
                ciclo++;
                
                // Verificar E/S
                if (!siguiente.isCPUbound() && siguiente.getCiclosAtencion() > 0) {
                    if (siguiente.getPCB().getProgramCounter() % siguiente.getCiclosAtencion() == 0 
                        && siguiente.getPCB().getProgramCounter() < siguiente.getInstruccionesTotales()) {
                        bloqueado = true;
                        siguiente.iniciarES();
                        siguiente.getPCB().setEstado(EstadoProceso.BLOQUEADO);
                        colaBloqueadosLocal.encolar(siguiente);
                        System.out.println("[Ciclo " + ciclo + "] " + siguiente.getPCB().getNombre() + 
                                         " → BLOQUEADO (E/S)");
                    }
                }
                
                if (siguiente.getPCB().getProgramCounter() >= siguiente.getInstruccionesTotales()) {
                    terminado = true;
                }
            }
            
            metricas.registrarEjecucion(siguiente, ejecutadas, ciclo);
            
            if (terminado) {
                metricas.registrarFinalizacion(siguiente, ciclo);
                gestorMemoria.liberarProceso(siguiente);
                System.out.println("[Ciclo " + ciclo + "] " + siguiente.getPCB().getNombre() + 
                                 " → TERMINADO");
            } else if (!bloqueado) {
                if (siguiente.getMlfqNivel() < mlfq.getNivelesCount() - 1) {
                    siguiente.demoteMlfq();
                }
                mlfq.encolarProceso(siguiente);
            }
        }
    }
    
    /**
     * Ejecuta simulación con algoritmos generales (FCFS, SJF, RR, Prioridades, HRRN)
     */
    private static void ejecutarConAlgoritmoGeneral(Planificador planificador, 
                                                   ColasMultinivel colas, CPU cpu,
                                                   Metricas metricas, int ciclo,
                                                   ColaProcesos colaTerminados,
                                                   GestorMemoria gestorMemoria,
                                                   ConfiguracionSimulacion config) {
        
        while (!colas.getColaCortoPlazo().estaVacia() || 
               !colas.getColaBloqueados().estaVacia() ||
               !colas.getColaLargoPlazo().estaVacia() ||
               !colas.getColaMedianoPlazo().estaVacia()) {
            
            // Planificación de largo y mediano plazo
            int nuevos = colas.planificarLargoPlazo(ciclo);
            int reanudados = colas.planificarMedianoPlazo(ciclo);
            
            if (nuevos > 0) {
                System.out.println("[Ciclo " + ciclo + "] Largo plazo: " + nuevos + " procesos cargados");
            }
            if (reanudados > 0) {
                System.out.println("[Ciclo " + ciclo + "] Mediano plazo: " + reanudados + " procesos reanudados");
            }
            
            // Gestión de swapping si es necesario
            if (!gestorMemoria.hayEspacioDisponible() && !colas.getColaLargoPlazo().estaVacia()) {
                Proceso victima = colas.realizarSwap(ciclo);
                if (victima != null) {
                    System.out.println("[Ciclo " + ciclo + "] SWAP: " + victima.getPCB().getNombre() + 
                                     " → SUSPENDIDO (política: " + gestorMemoria.getPolitica() + ")");
                }
            }
            
            // Decrementar E/S de bloqueados
            ColaProcesos.ColaTemporal itBloq = colas.getColaBloqueados().crearIterador();
            while (itBloq.tieneSiguiente()) {
                Proceso bloq = itBloq.siguiente();
                bloq.decrementarContadorES();
                
                if (bloq.getContadorES() <= 0) {
                    colas.desbloquearProceso(bloq);
                    System.out.println("[Ciclo " + ciclo + "] " + bloq.getPCB().getNombre() + 
                                     " → DESBLOQUEADO");
                }
            }
            
            // Si no hay procesos listos, esperar
            if (colas.getColaCortoPlazo().estaVacia()) {
                if (!colas.getColaBloqueados().estaVacia() || 
                    !colas.getColaLargoPlazo().estaVacia() ||
                    !colas.getColaMedianoPlazo().estaVacia()) {
                    ciclo++;
                    System.out.println("[Ciclo " + ciclo + "] CPU inactiva (esperando procesos)");
                    continue;
                } else {
                    break;
                }
            }
            
            // Incrementar espera para HRRN
            if (planificador instanceof HRRN) {
                ColaProcesos.ColaTemporal aux = colas.getColaCortoPlazo().crearIterador();
                while (aux.tieneSiguiente()) {
                    aux.siguiente().incrementarEspera();
                }
            }
            
            // Seleccionar siguiente proceso
            Proceso siguiente = planificador.seleccionarProceso(colas.getColaCortoPlazo());
            if (siguiente == null) break;
            
            gestorMemoria.cargarProceso(siguiente, ciclo);
            metricas.registrarInicio(siguiente, ciclo);
            cpu.cargarProceso(siguiente);
            
            System.out.println("[Ciclo " + ciclo + "] CPU → " + siguiente.getPCB().getNombre());
            
            // Ejecutar según política
            if (planificador instanceof RoundRobin rr) {
                ejecutarRoundRobin(rr, siguiente, colas, metricas, ciclo);
            } else {
                ejecutarNoPreemptive(siguiente, colas, metricas, ciclo, colaTerminados, gestorMemoria);
            }
            
            cpu.liberarCPU();
        }
    }
    
    private static int ejecutarRoundRobin(RoundRobin rr, Proceso proceso, 
                                         ColasMultinivel colas, Metricas metricas, 
                                         int ciclo) {
        int quantum = rr.getQuantum();
        boolean terminado = false;
        boolean bloqueado = false;
        int ejecutadas = 0;
        
        for (int i = 0; i < quantum && !terminado && !bloqueado; i++) {
            proceso.getPCB().incrementarPC();
            proceso.getPCB().incrementarMAR();
            ejecutadas++;
            ciclo++;
            
            if (!proceso.isCPUbound() && proceso.getCiclosAtencion() > 0) {
                if (proceso.getPCB().getProgramCounter() % proceso.getCiclosAtencion() == 0 
                    && proceso.getPCB().getProgramCounter() < proceso.getInstruccionesTotales()) {
                    bloqueado = true;
                    proceso.iniciarES();
                    colas.bloquearProceso(proceso);
                    System.out.println("[Ciclo " + ciclo + "] " + proceso.getPCB().getNombre() + 
                                     " → BLOQUEADO (E/S)");
                }
            }
            
            if (proceso.getPCB().getProgramCounter() >= proceso.getInstruccionesTotales()) {
                terminado = true;
            }
        }
        
        metricas.registrarEjecucion(proceso, ejecutadas, ciclo);
        
        if (terminado) {
            proceso.getPCB().setEstado(EstadoProceso.TERMINADO);
            metricas.registrarFinalizacion(proceso, ciclo);
            System.out.println("[Ciclo " + ciclo + "] " + proceso.getPCB().getNombre() + 
                             " → TERMINADO");
        } else if (!bloqueado) {
            proceso.getPCB().setEstado(EstadoProceso.LISTO);
            colas.getColaCortoPlazo().encolar(proceso);
            System.out.println("[Ciclo " + ciclo + "] Quantum agotado → cola");
        }
        
        return ciclo;
    }
    
    private static int ejecutarNoPreemptive(Proceso proceso, ColasMultinivel colas, 
                                           Metricas metricas, int ciclo, 
                                           ColaProcesos colaTerminados,
                                           GestorMemoria gestorMemoria) {
        boolean terminado = false;
        int pcInicial = proceso.getPCB().getProgramCounter();
        
        while (!terminado && proceso.getPCB().getProgramCounter() < proceso.getInstruccionesTotales()) {
            proceso.getPCB().incrementarPC();
            proceso.getPCB().incrementarMAR();
            ciclo++;
            
            if (!proceso.isCPUbound() && proceso.getCiclosAtencion() > 0) {
                if (proceso.getPCB().getProgramCounter() % proceso.getCiclosAtencion() == 0 
                    && proceso.getPCB().getProgramCounter() < proceso.getInstruccionesTotales()) {
                    
                    int ejecutadas = proceso.getPCB().getProgramCounter() - pcInicial;
                    metricas.registrarEjecucion(proceso, ejecutadas, ciclo);
                    
                    proceso.iniciarES();
                    colas.bloquearProceso(proceso);
                    System.out.println("[Ciclo " + ciclo + "] " + proceso.getPCB().getNombre() + 
                                     " → BLOQUEADO (E/S)");
                    break;
                }
            }
            
            if (proceso.getPCB().getProgramCounter() >= proceso.getInstruccionesTotales()) {
                terminado = true;
            }
        }
        
        if (terminado) {
            int ejecutadas = proceso.getPCB().getProgramCounter() - pcInicial;
            metricas.registrarEjecucion(proceso, ejecutadas, ciclo);
            proceso.getPCB().setEstado(EstadoProceso.TERMINADO);
            metricas.registrarFinalizacion(proceso, ciclo);
            gestorMemoria.liberarProceso(proceso);
            colaTerminados.encolar(proceso);
            System.out.println("[Ciclo " + ciclo + "] " + proceso.getPCB().getNombre() + 
                             " → TERMINADO");
        }
        
        return ciclo;
    }
}