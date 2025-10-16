/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package MainClasses;
import MainPackage.Main;
import java.util.Comparator;

import EDD.Lista;

import MainPackage.Main;

import EDD.Nodo;


/**
 *
 * @author pedro
 */

public class Scheduller {
    private Lista<Proceso> ColaListo;
    private Lista<Proceso> ColaBloqueados;
    private Lista<Proceso> ColaTerminados;
    private int quantum;
    private CPU cpu;
    private Proceso procesoActual;

    public Scheduller(int quantum,  Lista ColaListo, Lista ColaBloqueados, Lista ColaTerminados) {
        this.ColaListo = ColaListo;
        this.ColaBloqueados = ColaBloqueados;
        this.ColaTerminados = ColaTerminados;
        this.quantum = 5;
        this.procesoActual = null;


    }

    public Lista<Proceso> getColaListo() {
        return ColaListo;
    }

    public Lista<Proceso> getColaBloqueados() {
        return ColaBloqueados;
    }

    public void setColaBloqueados(Lista<Proceso> ColaBloqueados) {
        this.ColaBloqueados = ColaBloqueados;
    }

    public Lista<Proceso> getColaTerminados() {
        return ColaTerminados;
    }

    public void setColaTerminados(Lista<Proceso> ColaTerminados) {
        this.ColaTerminados = ColaTerminados;
    }

    public int getQuantum() {
        return quantum;
    }

    public void setQuantum(int quantum) {
        this.quantum = quantum;
    }

    public CPU getCpu() {
        return cpu;
    }

    public void setCpu(CPU cpu) {
        this.cpu = cpu;
    }

    public Proceso getProcesoActual() {
        return procesoActual;
    }

    public void setProcesoActual(Proceso procesoActual) {
        this.procesoActual = procesoActual;
    }
    
    
    

    
    public Proceso ejecutarPlanificacion(int politica) {
        switch (politica) {
            case 1 -> ordenarFCFS();
            case 2 -> ordenarRoundRobin();
            case 3 -> ordenarSRT();
            case 4 -> ordenarSPN();
            case 5 -> ordenarHRRN();
            default -> throw new IllegalArgumentException("Política inválida");
        }
        Main.colaListos=ColaListo;
        return ColaListo.isEmpty() ? null : ColaListo.getpFirst().gettInfo();
    }

    
    private void ordenarFCFS() {
        ordenarPorFCFS();
        // FCFS no necesita ordenamiento (se mantiene el orden de llegada)
    }

    private void ordenarRoundRobin() {
        ordenarPorFCFS();
        // RR solo mueve el proceso al final si no terminó (se maneja en la CPU)
    }

    private void ordenarSRT() {
        bubbleSort(new ComparatorSRT());
    }

    private void ordenarSPN() {
        bubbleSort(new ComparatorSPN());
    }

    private void ordenarHRRN() {
        bubbleSort(new ComparatorHRRN());
    }

    
    private static class ComparatorSRT implements Comparator<Proceso> {
        @Override
        public int compare(Proceso p1, Proceso p2) {
            return Integer.compare(p1.getRemainingTime(), p2.getRemainingTime());
        }
    }

    private static class ComparatorSPN implements Comparator<Proceso> {
        @Override
        public int compare(Proceso p1, Proceso p2) {
            return Integer.compare(p1.getTime(), p2.getTime());
        }
    }

    private static class ComparatorHRRN implements Comparator<Proceso> {
        @Override
        public int compare(Proceso p1, Proceso p2) {
            double ratio1 = (Main.cicloGlobal - p1.getLlegada() + p1.getTime()) / p1.getTime();
            double ratio2 = (Main.cicloGlobal - p2.getLlegada() + p2.getTime()) / p2.getTime();
            return Double.compare(ratio2, ratio1); // Orden descendente
        }
    }
    private static class ComparatorFCFS implements Comparator<Proceso> {
        @Override
        public int compare(Proceso p1, Proceso p2) {
            return Integer.compare(p1.getLlegada(), p2.getLlegada());
        }
    }
    
    private void ordenarPorFCFS() {
        bubbleSort(new ComparatorFCFS());
    }
    
    private void bubbleSort(Comparator<Proceso> comparator) {
        if (ColaListo.getSize() <= 1) return;

        boolean swapped;
        do {
            swapped = false;
            Nodo<Proceso> actual = ColaListo.getpFirst();
            while (actual != null && actual.getpNext() != null) {
                Proceso p1 = actual.gettInfo();
                Proceso p2 = actual.getpNext().gettInfo();

                if (comparator.compare(p1, p2) > 0) {
                    // Intercambiar nodos
                    Proceso temp = p1;
                    actual.settInfo(p2);
                    actual.getpNext().settInfo(temp);
                    swapped = true;
                }
                actual = actual.getpNext();
            }
        } while (swapped);
    }
}