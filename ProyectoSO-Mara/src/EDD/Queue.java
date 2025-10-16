/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EDD;

import MainClasses.Proceso;

/**
 *
 * @author pedro
 */
public class Queue<T> {
    private Nodo<T> head;
    private Nodo<T> tail;
    private int size;

    public Queue() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public boolean isEmpty() {
        return this.head == null;
    }

    public void empty() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    public void encolar(T dato) {
        Nodo<T> nuevo = new Nodo<>(dato);
        if (isEmpty()) {
            this.head = nuevo;
            this.tail = nuevo;
        } else {
            this.tail.setpNext(nuevo);
            this.tail = nuevo;
        }
        this.size++;
    }

    public void desencolar() {
        if (!isEmpty()) {
            if (this.size == 1) {
                empty();
            } else {
                this.head = this.head.getpNext();
                this.size--;
            }
        }
    }

    public String travel() {
        StringBuilder toPrint = new StringBuilder();
        if (!isEmpty()) {
            Nodo<T> actual = this.head;
            while (actual != null) {
                toPrint.append(actual.gettInfo()).append("-->");
                actual = actual.getpNext();
            }
        }
        return toPrint.toString();
    }

    public int getSize() {
        return size;
    }

    public void remove(Proceso menor) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}